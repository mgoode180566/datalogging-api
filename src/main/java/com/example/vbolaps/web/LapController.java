package com.example.vbolaps.web;

import com.example.vbolaps.dto.SessionDto;
import com.example.vbolaps.dto.SessionItemDto;
import com.example.vbolaps.mapper.SessionItemMapper;
import com.example.vbolaps.mapper.SessionMapper;
import com.example.vbolaps.model.*;
import com.example.vbolaps.repo.SessionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class LapController {
    
    private final SessionRepo sessionRepo;

    public LapController(SessionRepo sessionRepo) {
        this.sessionRepo = sessionRepo;
    }
    
    @GetMapping("/sessions")
    public List<SessionItemDto> getSessions() {
        return sessionRepo.findAll().stream().map(s -> SessionItemMapper.toDto(s)).collect(Collectors.toList());
    }
    
    @GetMapping("/sessions/{sessionId}")
    public SessionDto getSession(@PathVariable("sessionId") Long sessionId) {
        Session session = sessionRepo.getReferenceById(sessionId);
        log.info("Laps : {}", session.getLaps().size());
        return SessionMapper.toDto(session);
    }
    
    @GetMapping("/sessions/{sessionId}/laps")
    public SessionDto listLaps(@PathVariable("sessionId") Long sessionId) {
        Session session = sessionRepo.getReferenceById(sessionId);
        return SessionMapper.toDto(session);
        //return session.getLaps().stream().map(LapMapper::toDto).toList();
        
//        log.info("Session has {} laps", session.getLaps().size());
//        return session.getLaps()
//          .stream()
//          .map(LapMapper::toDto)
//          .toList();
    }
    
    @DeleteMapping("/sessions/deleteall")
    public void deleteAllSessions() {
        sessionRepo.deleteAll();
    }
    
//    @GetMapping("/laps/{lapId}/polyline")
//    public Map<String, Object> lapPolyline(@PathVariable("lapId") Long lapId) {
//        List<Sample> list = sampleRepo.findByLapIdOrderBySeq(lapId);
//
//        List<List<Double>> coords = list.stream()
//                .map(s -> List.of(s.getLon(), s.getLat(), s.getTime()))
//                .collect(Collectors.toList());
//
//        double startTime = list.get(0).getTime();
//        double endTime = list.get(list.size() - 1).getTime();
//        double duration = endTime - startTime;
//
//
//        return Map.of(
//                "lapId", lapId,
//                "lapTime", duration,
//                "points", coords,
//                "count", coords.size());
//    }

//    @GetMapping("/sessions/{sessionId}/overlay")
//    public Map<String, Object> overlay(@PathVariable("sessionId") Long sessionId) {
//        List<Lap> laps = lapRepo.findBySessionIdOrderByNumber(sessionId);
//        List<Map<String, Object>> lines = new ArrayList<>();
//        for (Lap l: laps) {
//            List<Sample> samples = sampleRepo.findByLapIdOrderBySeq(l.getId());
//            List<List<Double>> coords = samples.stream()
//                    .map(s -> List.of(s.getLon(), s.getLat()))
//                    .collect(Collectors.toList());
//            lines.add(Map.of("lap", l.getNumber(), "lapId", l.getId(), "points", coords));
//        }
//        return Map.of("sessionId", sessionId, "laps", lines);
//    }
}
