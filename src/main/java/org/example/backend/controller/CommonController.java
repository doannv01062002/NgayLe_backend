package org.example.backend.controller;

import org.example.backend.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Autowired
    private VisitService visitService;

    @PostMapping("/track-visit")
    public ResponseEntity<Void> trackVisit() {
        visitService.trackVisit();
        return ResponseEntity.ok().build();
    }
}
