package com.example.vbolaps.service;

import com.example.vbolaps.dto.SessionDto;
import com.example.vbolaps.model.*;
import com.example.vbolaps.repo.*;
import com.example.vbolaps.utils.VBoxConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImportService {
    
    private String[] fixedColumns = {"satellites", "time", "latitude", "longitude", "velocity kmh", "heading", "height", "vertical velocity m/s", "sampleperiod", "solution type", "avifileindex", "avisynctime"};
    
    
    private static Logger log = LoggerFactory.getLogger(ImportService.class.getName());

    private final SessionRepo sessionRepo;

    public ImportService(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }
    
    @Transactional
    public Session importVbo(InputStream vboStream, SessionDto sessionDto) throws Exception {
        VboParser parser = new VboParser();
        log.info("About to parse");
        VboParser.Parsed parsed = parser.parse(vboStream);
        log.info("Parsing complete");
        Session session = new Session();
        session.setCircuit(sessionDto.circuit());
        session.setDriver(sessionDto.driver());
        session.setVehicle(sessionDto.vehicle());
        //session.setWeather(parsed.sessionMeta.getOrDefault("weather", ""));
        session.setDate(sessionDto.date());

        // Flatten rows to maps
        List<Map<String, Double>> rows = parsed.rows.stream().map(r -> r.baseValues).collect(Collectors.toList());
        log.info("Row count: {}",rows.size());
        
        List<Integer> lapAssignments;
        if (parsed.start1.isPresent()) { // detect laps if the start line is valid
            lapAssignments = LapDetector.detectLaps(rows, parsed.start1.get());
        } else {
            // use the first recorded point to determine the laps
            lapAssignments = LapDetector.detectLapsFirstPoint(rows);
        }

        Map<Integer, List<Integer>> indicesByLap = new LinkedHashMap<>();
        for (int i=0; i<lapAssignments.size(); i++) {
            int lapNo = lapAssignments.get(i);
            if (lapNo<=0) continue;
            indicesByLap.computeIfAbsent(lapNo, k->new ArrayList<>()).add(i);
        }

        for (Map.Entry<Integer, List<Integer>> e : indicesByLap.entrySet()) {
            int lapNo = e.getKey();
            List<Integer> idx = e.getValue();
            Lap lap = new Lap();
            lap.setSession(session);
            lap.setNumber(lapNo);
            
            log.info("Lap : {}", lap.getNumber());
            double samplePeriod = parsed.rows.get(idx.get(0)).baseValues.getOrDefault("sampleperiod", Double.NaN);
            
            lap.setLapTimeSeconds( idx.size() * samplePeriod );
            
            session.setChannels(parsed.headers.stream().filter(value -> Arrays.asList(fixedColumns).contains(value)).collect(Collectors.joining(",")));
            
            // build map of empty graphs
            for( int i = 0; i < parsed.headers.size(); i++) {
                GraphData graphData = new GraphData();
                graphData.setLap(lap);
                graphData.setName(parsed.headers.get(i));
                lap.getGraphs().add(graphData);
            };
            for (int rowIndex : idx) {
                Map<String, Double> rv = parsed.rows.get(rowIndex).baseValues;
                List<String> lstNames = new ArrayList<>(rv.keySet());
                for( int i = 0; i < lstNames.size(); i++) {
                    GraphData graphData = lap.getGraphs().get(i);
                    List<Double> lstValues = new ArrayList<>(rv.values());
                    
                    Double v = lstValues.get(i);
                    if (graphData.getName().equals("latitude")) {
                        v = VBoxConverter.convertRawLatitude(v);
                    } else if (graphData.getName().equals("longitude")) {
                        v = VBoxConverter.convertRawLongitude(v);
                    };
                    graphData.getPoints().add(v);
                };
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            for(int i = 0; i < lap.getGraphs().size(); i++) {
                GraphData graphData = lap.getGraphs().get(i);
                List<Double> points = graphData.getPoints();
                String s = objectMapper.writeValueAsString(points);
                graphData.setJson(s);
            }
            session.getLaps().add(lap);
        }
        log.info("Session contains {} laps", session.getLaps().size());
        try {
            session = sessionRepo.save(session);
        } catch (Exception ex) {
            log.info("Error when saving {}", ex.getMessage());
        }
        log.info("Session saved");
        return session;
    }
}