package com.vw.tide_eye.controller;

import com.vw.tide_eye.service.TideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/harbors")
public class HarborController {

    private final TideService tideService;

    public HarborController(TideService tideService) {
        this.tideService = tideService;
    }

    @GetMapping
    public ResponseEntity<String> getHarbors() {
        String harbors = tideService.fetchHarbors();
        return ResponseEntity.ok(harbors);
    }
}