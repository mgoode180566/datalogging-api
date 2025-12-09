package com.example.vbolaps.web;

import com.example.vbolaps.model.Session;
import com.example.vbolaps.dto.SessionDto;
import com.example.vbolaps.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImportController {
    private final static Logger log = LoggerFactory.getLogger(ImportService.class);
    
    private final ImportService importService;
    
    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping(value="/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String,Object>  importVbo(@RequestPart("file") MultipartFile file, @RequestPart("session") SessionDto sessionDto) throws Exception {
        log.info("Received file: {}", file.getName());
        Session session = importService.importVbo(file.getInputStream(), sessionDto);
        //return "Completed";
        log.info("Session lap count={}",session.getLaps().size());
        return Map.of("sessionId", session.getId(), "circuit", session.getCircuit(), "driver", session.getDriver(), "lap count", session.getLaps().size());
    }
}
