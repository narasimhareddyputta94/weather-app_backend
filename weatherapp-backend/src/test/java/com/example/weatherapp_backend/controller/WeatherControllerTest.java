package com.example.weatherapp_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private final String SAMPLE_CURRENT_WEATHER_RESPONSE = """
        {
          "latitude": 41.879482,
          "longitude": -87.64975,
          "current_weather": {
            "temperature": 23.3,
            "windspeed": 7.2
          }
        }
        """;

    private final String SAMPLE_FORECAST_RESPONSE = """
        {
          "latitude": 41.879482,
          "longitude": -87.64975,
          "daily": {
            "temperature_2m_max": [27.7, 24.8],
            "temperature_2m_min": [21.5, 21.7],
            "precipitation_sum": [6.9, 0.0],
            "windspeed_10m_max": [21.9, 13.1]
          }
        }
        """;

    private final String SAMPLE_HOURLY_FORECAST_RESPONSE = """
        {
          "latitude": 41.879482,
          "longitude": -87.64975,
          "hourly": {
            "temperature_2m": [22.9, 22.6],
            "weathercode": [51, 63],
            "windspeed_10m": [14.4, 16.7]
          }
        }
        """;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetCurrentWeather() throws Exception {
        when(restTemplate.getForObject("https://api.opencagedata.com/geocode/v1/json?q=Chicago&key=cea6bbb5f1be40b098819cce4463a114", Map.class))
                .thenReturn(Map.of("results", List.of(Map.of("geometry", Map.of("lat", 41.879482, "lng", -87.64975)))));

        when(restTemplate.getForObject("https://api.open-meteo.com/v1/forecast?latitude=41.879482&longitude=-87.64975&current_weather=true&timezone=America/Chicago", Object.class))
                .thenReturn(SAMPLE_CURRENT_WEATHER_RESPONSE);

        mockMvc.perform(get("/api/weather/current").param("location", "Chicago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(closeTo(41.879482, 0.001)))
                .andExpect(jsonPath("$.longitude").value(closeTo(-87.64975, 0.001)));
    }

    @Test
    void testGetWeatherForecast() throws Exception {
        when(restTemplate.getForObject("https://api.opencagedata.com/geocode/v1/json?q=Chicago&key=cea6bbb5f1be40b098819cce4463a114", Map.class))
                .thenReturn(Map.of("results", List.of(Map.of("geometry", Map.of("lat", 41.879482, "lng", -87.64975)))));

        when(restTemplate.getForObject("https://api.open-meteo.com/v1/forecast?latitude=41.879482&longitude=-87.64975&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,windspeed_10m_max&timezone=America/Chicago", Object.class))
                .thenReturn(SAMPLE_FORECAST_RESPONSE);

        mockMvc.perform(get("/api/weather/forecast").param("location", "Chicago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(closeTo(41.879482, 0.001)))
                .andExpect(jsonPath("$.longitude").value(closeTo(-87.64975, 0.001)));
    }

    @Test
    void testGetHourlyForecast() throws Exception {
        when(restTemplate.getForObject("https://api.opencagedata.com/geocode/v1/json?q=Chicago&key=cea6bbb5f1be40b098819cce4463a114", Map.class))
                .thenReturn(Map.of("results", List.of(Map.of("geometry", Map.of("lat", 41.879482, "lng", -87.64975)))));

        when(restTemplate.getForObject("https://api.open-meteo.com/v1/forecast?latitude=41.879482&longitude=-87.64975&hourly=temperature_2m,weathercode,windspeed_10m&timezone=America/Chicago", Object.class))
                .thenReturn(SAMPLE_HOURLY_FORECAST_RESPONSE);

        mockMvc.perform(get("/api/weather/hourly").param("location", "Chicago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(closeTo(41.879482, 0.001)))
                .andExpect(jsonPath("$.longitude").value(closeTo(-87.64975, 0.001)));
    }

    @Test
    void testInvalidLocation() throws Exception {
        when(restTemplate.getForObject("https://api.opencagedata.com/geocode/v1/json?q=InvalidLocation&key=cea6bbb5f1be40b098819cce4463a114", Map.class))
                .thenReturn(Map.of("results", List.of()));

        mockMvc.perform(get("/api/weather/current").param("location", "InvalidLocation"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Location not found: InvalidLocation"));
    }

}
