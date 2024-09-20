package com.vw.tide_eye.controller;

import com.vw.tide_eye.exception.TideDataFetchException;
import com.vw.tide_eye.model.Tide;
import com.vw.tide_eye.service.TideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TideController.class)
public class TideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TideService tideService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void getTideData_ReturnsTideData_ForValidHarborCode() throws Exception {
        String harborCode = "bergen";
        when(tideService.fetchTideData(harborCode)).thenReturn(new Tide());

        mockMvc.perform(get("/api/tides/{harborCode}", harborCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tideData").exists())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetTideData_Failure() throws Exception {
        String invalidHarborCode = "InvalidCode"; // Use an invalid harbor code to simulate failure

        when(tideService.fetchTideData(invalidHarborCode)).thenThrow(new TideDataFetchException("Failed to fetch data"));


        mockMvc.perform(get("/api/tides/{harborCode}", invalidHarborCode)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists())
                .andDo(MockMvcResultHandlers.print());
    }
}
