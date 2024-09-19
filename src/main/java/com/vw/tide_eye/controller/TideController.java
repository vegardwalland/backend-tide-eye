package com.vw.tide_eye.controller;

import com.vw.tide_eye.exception.TideDataFetchException;
import com.vw.tide_eye.service.TideService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/tides")
public class TideController {

    private final TideService tideService;

    public TideController(TideService tideService) {
        this.tideService = tideService;
    }

    @GetMapping("/{harborCode}")
    public ResponseEntity<Map<String, Object>> getTideData(@PathVariable String harborCode) throws TideDataFetchException {
        return ResponseEntity.ok(Map.of("tideData", tideService.fetchTideData(harborCode)));
    }
}