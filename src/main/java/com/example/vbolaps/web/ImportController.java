package com.example.vbolaps.web;

import com.example.vbolaps.model.Session;
import com.example.vbolaps.model.SessionDto;
import com.example.vbolaps.repo.SessionRepo;
import com.example.vbolaps.service.ImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImportController {
    private final ImportService importService;
    private final SessionRepo sessionRepo;
    
    private final ObjectMapper objectMapper;

    public ImportController(ImportService importService, SessionRepo sessionRepo, ObjectMapper objectMapper) {
        this.importService = importService;
        this.sessionRepo = sessionRepo;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value="/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> importVbo(@RequestPart("file") MultipartFile file, @RequestPart("session") SessionDto sessionDto) throws Exception {
        Session session = importService.importVbo(file.getInputStream(), sessionDto);
        return Map.of("sessionId", session.getId(), "circuit", session.getCircuit(), "driver", session.getDriver());
    }
}
