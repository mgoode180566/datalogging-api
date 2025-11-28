package com.example.vbolaps.web;

import com.example.vbolaps.model.*;
import com.example.vbolaps.repo.LapRepo;
import com.example.vbolaps.repo.SampleRepo;
import com.example.vbolaps.repo.SessionRepo;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LapController {

    private final LapRepo lapRepo;
    private final SampleRepo sampleRepo;
    private final SessionRepo sessionRepo;

    public LapController(SessionRepo sessionRepo, LapRepo lapRepo, SampleRepo sampleRepo) {
        this.sessionRepo = sessionRepo;
        this.lapRepo = lapRepo;
        this.sampleRepo = sampleRepo;
    }
    
    @GetMapping("/sessions")
    public List<SessionDto> getSessions() {
        List<Session> sessions = sessionRepo.findAll();
        List<SessionDto> dtos = sessions.stream().map(s -> SessionMapper.toDto(s)).collect(Collectors.toList());
        return sessions.stream().map(s -> SessionMapper.toDto(s)).collect(Collectors.toList());
    }
    
    @GetMapping("/sessions/{sessionId}/laps")
    public List<Map<String, Object>> listLaps(@PathVariable("sessionId") Long sessionId) {
        return lapRepo.findBySessionIdOrderByNumber(sessionId).stream()
          .map(l -> Map.<String, Object>of(
            "id", l.getId(),
            "number", l.getNumber(),
            "lapTimeSeconds", l.getLapTimeSeconds(),
            "samples", l.getSamples().stream().map(s -> SampleMapper.toDto(s)).collect(Collectors.toList())
          ))
          .collect(Collectors.toList());
    }
    
    @GetMapping("/laps/{lapId}/polyline")
    public Map<String, Object> lapPolyline(@PathVariable("lapId") Long lapId) {
        List<Sample> list = sampleRepo.findByLapIdOrderBySeq(lapId);
        
        List<List<Double>> coords = list.stream()
                .map(s -> List.of(s.getLon(), s.getLat(), s.getTime()))
                .collect(Collectors.toList());
        
        double startTime = list.get(0).getTime();
        double endTime = list.get(list.size() - 1).getTime();
        double duration = endTime - startTime;
        
        
        return Map.of(
                "lapId", lapId,
                "lapTime", duration,
                "points", coords,
                "count", coords.size());
    }

    @GetMapping("/sessions/{sessionId}/overlay")
    public Map<String, Object> overlay(@PathVariable("sessionId") Long sessionId) {
        List<Lap> laps = lapRepo.findBySessionIdOrderByNumber(sessionId);
        List<Map<String, Object>> lines = new ArrayList<>();
        for (Lap l: laps) {
            List<Sample> samples = sampleRepo.findByLapIdOrderBySeq(l.getId());
            List<List<Double>> coords = samples.stream()
                    .map(s -> List.of(s.getLon(), s.getLat()))
                    .collect(Collectors.toList());
            lines.add(Map.of("lap", l.getNumber(), "lapId", l.getId(), "points", coords));
        }
        return Map.of("sessionId", sessionId, "laps", lines);
    }
}
