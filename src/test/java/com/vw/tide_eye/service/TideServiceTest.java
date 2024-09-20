package com.vw.tide_eye.service;

import com.vw.tide_eye.exception.TideDataFetchException;
import com.vw.tide_eye.model.SurgeData;
import com.vw.tide_eye.model.Tide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class TideServiceTest {

    @InjectMocks
    private TideService tideService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tideService = new TideService(restTemplate, redisTemplate);
    }

    @Test
    void testFetchTideData_Success() throws TideDataFetchException {
        String mockResponse = """
                SIST OPPDATERT: 20240915 18:17 UTC
                ==========================================
                BERGEN
                ------------------------------
                AAR MND DAG TIM PROG  SURGE  TIDE   TOTAL  0p     25p    50p    75p    100p
                2024   9  15   0    0   0.11  -0.27  -0.16   0.06   0.11   0.11   0.11   0.16
                2024   9  15   1    1   0.11  -0.36  -0.25   0.06   0.10   0.10   0.11   0.16
                2024   9  15   2    2   0.11  -0.36  -0.25   0.06   0.11   0.11   0.12   0.16
                2024   9  15   3    3   0.12  -0.26  -0.14   0.07   0.12   0.12   0.13   0.17
                2024   9  15   4    4   0.13  -0.10   0.03   0.08   0.12   0.12   0.13   0.18
                2024   9  15   5    5   0.13   0.09   0.22   0.08   0.12   0.12   0.13   0.18
                2024   9  15   6    6   0.12   0.27   0.39   0.07   0.11   0.11   0.12   0.17
                2024   9  15   7    7   0.13   0.39   0.52   0.08   0.12   0.13   0.13   0.18
                2024   9  15   8    8   0.13   0.41   0.54   0.08   0.13   0.13   0.13   0.19
                2024   9  15   9    9   0.13   0.34   0.47   0.08   0.13   0.14   0.14   0.19
                2024   9  15  10   10   0.12   0.19   0.31   0.07   0.12   0.13   0.13   0.18
                2024   9  15  11   11   0.13   0.00   0.13   0.08   0.13   0.13   0.13   0.18
                2024   9  15  12   12   0.14  -0.18  -0.04   0.08   0.14   0.14   0.14   0.19
                2024   9  15  13   13   0.14  -0.32  -0.18   0.08   0.13   0.14   0.14   0.19
                2024   9  15  14   14   0.14  -0.34  -0.20   0.08   0.14   0.14   0.14   0.19
                2024   9  15  15   15   0.13  -0.26  -0.13   0.08   0.14   0.14   0.15   0.19
                2024   9  15  16   16   0.13  -0.10   0.03   0.07   0.14   0.14   0.15   0.19
                2024   9  15  17   17   0.13   0.10   0.23   0.08   0.13   0.13   0.14   0.19
                2024   9  15  18   18   0.12   0.31   0.43   0.07   0.12   0.13   0.13   0.18
                """;

        // Mock the RestTemplate response
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(mockResponse);

        Tide tide = tideService.fetchTideData("bergen");

        // Verify the result
        assertNotNull(tide);
        assertEquals("bergen", tide.getHarbor());
        assertEquals("2024-09-15T18-17", tide.getLastUpdated());
        assertFalse(tide.getSurgeData().isEmpty());
        assertEquals(19, tide.getSurgeData().size());

        // Verify surge data parsing
        SurgeData surgeData = tide.getSurgeData().get(0);
        assertEquals(2024, surgeData.getYear());
        assertEquals(9, surgeData.getMonth());
        assertEquals(15, surgeData.getDay());
        assertEquals(0.11f, surgeData.getSurge());
        assertEquals(-0.27f, surgeData.getTide());
        assertEquals(-0.16f, surgeData.getTotal());
    }

    @Test
    void testFetchTideData_EmptyResponse() {
        // Mocking an empty response from the API
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("");

        Exception exception = assertThrows(TideDataFetchException.class, () -> {
            tideService.fetchTideData("bergen");
        });

        // Verify the exception message
        assertEquals("Failed to fetch data from MET API", exception.getMessage());
    }

    @Test
    void testFetchTideData_NullResponse() {
        // Mocking a null response from the API
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(null);

        Exception exception = assertThrows(TideDataFetchException.class, () -> {
            tideService.fetchTideData("bergen");
        });

        // Verify the exception message
        assertEquals("Failed to fetch data from MET API", exception.getMessage());
    }

    @Test
    void testParseToTide_InvalidData() {
        // Provide invalid data that will cause a parsing error
        String invalidMockResponse = """
                SIST OPPDATERT: 20240915 18:17 UTC
                ==========================================
                BERGEN
                ------------------------------
                AAR MND DAG TIM PROG  SURGE  TIDE   TOTAL  0p     25p    50p    75p    100p
                Invalid   9  15   0    0   0.11  -0.27  -0.16   0.06   0.11   0.11   0.11   0.16
                """;

        // Mocking the response from the API
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(invalidMockResponse);

        Exception exception = assertThrows(TideDataFetchException.class, () -> {
            tideService.fetchTideData("bergen");
        });

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Error parsing surge data"));
    }
}
