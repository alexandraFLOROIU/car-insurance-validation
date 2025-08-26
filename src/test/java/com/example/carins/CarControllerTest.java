package com.example.carins;


import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void carNotFound() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/cars/8/insurance-valid?date=2025-02-01", Map.class);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Car with id 8 not found", response.getBody().get("error"));
    }

    @Test
    void invalidDateFormat() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/cars/1/insurance-valid?date=iarna", Map.class);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid date format. Expected YYYY-MM-DD", response.getBody().get("error"));
    }

    @Test
    void dateOutOfRange() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/cars/1/insurance-valid?date=1245-03-01", Map.class);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Date must be between 1900 and 2100", response.getBody().get("error"));
    }
}
