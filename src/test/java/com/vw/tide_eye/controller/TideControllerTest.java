package com.vw.tide_eye.controller;

import com.vw.tide_eye.service.TideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TideController.class)
public class TideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TideService tideService;

    @Test
    public void testGetTideData_Success() throws Exception {
        String harborCode = "bergen";

        mockMvc.perform(get("/api/tides/{harborCode}", harborCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tideData").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetTideData_Failure() throws Exception {
        String invalidHarborCode = "InvalidCode"; // Use an invalid harbor code to simulate failure

        mockMvc.perform(get("/api/tides/{harborCode}", invalidHarborCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andDo(MockMvcResultHandlers.print());
    }
}
