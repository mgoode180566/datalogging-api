package com.example.vbolaps.service;

import com.example.vbolaps.model.*;
import com.example.vbolaps.repo.*;
import com.example.vbolaps.utils.VBoxConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImportService {
    
    private static Logger log = LoggerFactory.getLogger(ImportService.class.getName());

    private final SessionRepo sessionRepo;
    private final LapRepo lapRepo;
    private final SampleRepo sampleRepo;

    public ImportService(SessionRepo sessionRepo, LapRepo lapRepo, SampleRepo sampleRepo) {
        this.sessionRepo = sessionRepo;
        this.lapRepo = lapRepo;
        this.sampleRepo = sampleRepo;
    }
    
    @Transactional
    public Session importVbo(InputStream vboStream) throws Exception {
        VboParser parser = new VboParser();
        VboParser.Parsed parsed = parser.parse(vboStream);

        Session session = new Session();
        session.setCircuit(parsed.sessionMeta.getOrDefault("circuit", parsed.sessionMeta.getOrDefault("name", "Unknown")));
        session.setDriver(parsed.sessionMeta.getOrDefault("driver", ""));
        session.setVehicle(parsed.sessionMeta.getOrDefault("vehicle", ""));
        session.setWeather(parsed.sessionMeta.getOrDefault("weather", ""));

        // Flatten rows to maps
        List<Map<String, Double>> rows = parsed.rows.stream().map(r -> r.values).collect(Collectors.toList());
        
        ArrayList<Sample> samples = new ArrayList<>();
        for(Map<String, Double> row : rows) {
            Sample sample = new Sample();
            sample.setLat(VBoxConverter.convertRawLatitude(row.get("latitude")));
            sample.setLon(VBoxConverter.convertRawLongitude(row.get("longitude")));
            sample.setTime(VBoxConverter.convertVboTime(row.get("time")));
            sample.setVelocityKmh(row.get("velocity kmh"));
            sample.setSamplePeriod(row.get("sampleperiod"));
            samples.add(sample);
        }
        
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
            
            // naive lap time from first/last 'time' column if present
            double t0 = parsed.rows.get(idx.get(0)).values.getOrDefault("time", Double.NaN);
            double t1 = parsed.rows.get(idx.get(idx.size()-1)).values.getOrDefault("time", Double.NaN);
            double samplePeriod = parsed.rows.get(idx.get(0)).values.getOrDefault("sampleperiod", Double.NaN);
            
            lap.setLapTimeSeconds( idx.size() * samplePeriod );

            session.getLaps().add(lap);
            int seq = 0;
            for (int rowIndex : idx) {
                Map<String, Double> rv = parsed.rows.get(rowIndex).values;
                Sample s = new Sample();
                s.setLap(lap);
                s.setSeq(seq++);
                s.setTime(VBoxConverter.convertVboTime(rv.getOrDefault("time", Double.NaN)));
                s.setLat(VBoxConverter.convertRawLatitude(rv.getOrDefault("latitude", Double.NaN)));
                s.setLon(VBoxConverter.convertRawLongitude(rv.getOrDefault("longitude", Double.NaN)));
                s.setVelocityKmh(rv.getOrDefault("velocity kmh", Double.NaN));
                s.setHeading(rv.getOrDefault("heading", Double.NaN));
                s.setHeight(rv.getOrDefault("height", Double.NaN));
                s.setSamplePeriod(rv.getOrDefault("sampleperiod", Double.NaN));
                s.setVertVel(rv.getOrDefault("vert-vel", Double.NaN));
                s.setTsample(rv.getOrDefault("Tsample", Double.NaN));
                lap.getSamples().add(s);
                sampleRepo.save(s);
            }
        }
        session = sessionRepo.save(session);
        return session;
    }
}
