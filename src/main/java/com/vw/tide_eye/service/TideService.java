package com.vw.tide_eye.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vw.tide_eye.exception.TideDataFetchException;
import com.vw.tide_eye.model.Harbor;
import com.vw.tide_eye.model.SurgeData;
import com.vw.tide_eye.model.Tide;
import com.vw.tide_eye.utils.DateTimeConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TideService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;


    public TideService(RestTemplate restTemplate,RedisTemplate<String, Object> redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }


    @Cacheable(value = "tides", key = "#harborCode", unless = "#result == null")
    public Tide fetchTideData(String harborCode) throws TideDataFetchException {
        harborCode = harborCode.toLowerCase();

        String cacheKey = "tide:" + harborCode;
        Tide cachedTideData = (Tide) redisTemplate.opsForValue().get(cacheKey);

        if (cachedTideData != null) {
            return cachedTideData;
        }

        String url = String.format("https://api.met.no/weatherapi/tidalwater/1.1/?harbor=%s", harborCode);
        String responseText = restTemplate.getForObject(url, String.class);

        if (responseText == null || responseText.isEmpty()) {
            throw new TideDataFetchException("Failed to fetch data from MET API");
        }

        Tide tideData = parseToTide(responseText, harborCode);

        // TODO does this work correctly?
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime nextNoonUTC = now.withHour(12).withMinute(0).withSecond(0);
        if (now.isAfter(nextNoonUTC)) {
            nextNoonUTC = nextNoonUTC.plusDays(1);
        }

        Duration cacheDuration = Duration.between(now, nextNoonUTC);

        // Cache the tide data with expiration time set to next noon UTC
        redisTemplate.opsForValue().set(cacheKey, tideData, cacheDuration.getSeconds(), TimeUnit.SECONDS);

        return tideData;
    }

    @Cacheable(value = "harbors", unless = "#result == null")
    public List<Harbor> fetchHarbors() throws TideDataFetchException {
        String url = "https://api.met.no/weatherapi/tidalwater/1.1/locations";
        String response = restTemplate.getForObject(url, String.class);

        // Only need the harbor names, so parse the response to get them

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response);
            JsonNode featuresNode = root.path("features");

            return StreamSupport.stream(featuresNode.spliterator(), false)
                    .map(feature -> {
                        Harbor harbor = new Harbor();
                        harbor.setName(feature.path("title").asText());
                        JsonNode coordinates = feature.path("geometry").path("coordinates");
                        harbor.setLongitude(coordinates.get(0).asDouble());
                        harbor.setLatitude(coordinates.get(1).asDouble());
                        return harbor;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new TideDataFetchException("Error parsing harbor data", e);
        }
    }

    private Tide parseToTide(String text, String harbor) throws TideDataFetchException {
        Tide tide = new Tide();
        tide.setHarbor(harbor);
        ArrayList<SurgeData> surgeDataList = new ArrayList<>();

        for (String line : text.split("\n")) {
            String[] parts = line.trim().split("\\s+");

            // set last updated
            if (parts[0].equals("SIST")) {
                tide.setLastUpdated(DateTimeConverter.parseDateTime(parts[2] + " " + parts[3]));
            }

            // Get data from the string, but not the header
            if (parts.length == 13 && !parts[0].equals("AAR")) {
                try {
                    SurgeData surgeData = new SurgeData();
                    surgeData.setYear(Integer.parseInt(parts[0]));
                    surgeData.setMonth(Integer.parseInt(parts[1]));
                    surgeData.setDay(Integer.parseInt(parts[2]));
                    surgeData.setHour(Integer.parseInt(parts[3]));
                    //surgeData.setPrognosis(Integer.parseInt(parts[4]));
                    surgeData.setSurge(Float.parseFloat(parts[5]));
                    surgeData.setTide(Float.parseFloat(parts[6]));
                    surgeData.setTotal(Float.parseFloat(parts[7]));
                    /*surgeData.setSurgePercentage0(Float.parseFloat(parts[8]));
                    surgeData.setSurgePercentage25(Float.parseFloat(parts[9]));
                    surgeData.setSurgePercentage50(Float.parseFloat(parts[10]));
                    surgeData.setSurgePercentage75(Float.parseFloat(parts[11]));
                    surgeData.setSurgePercentage100(Float.parseFloat(parts[12]));*/
                    surgeDataList.add(surgeData);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    throw new TideDataFetchException("Error parsing surge data", e);
                }
            }
        }
        tide.setSurgeData(surgeDataList);

        return tide;
    }
}
