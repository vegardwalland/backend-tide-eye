package com.vw.tide_eye.controller;

import com.vw.tide_eye.exception.TideDataFetchException;
import com.vw.tide_eye.service.TideService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/harbors")
public class HarborController {

    private final TideService tideService;

    public HarborController(TideService tideService) {
        this.tideService = tideService;
    }

    @GetMapping
    public List<String> getHarbors() throws TideDataFetchException {
        return tideService.fetchHarbors();
    }
}