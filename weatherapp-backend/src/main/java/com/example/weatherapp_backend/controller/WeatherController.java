package com.example.weatherapp_backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class WeatherController {

    private final String OPENCAGE_API_KEY = "cea6bbb5f1be40b098819cce4463a114"; // Replace with your OpenCage API key

    @GetMapping("/api/weather/current")
    public Object getCurrentWeather(@RequestParam String location) {
        // Get latitude and longitude from the location using the geocoding service
        double[] latLon = getLatLon(location);
        double lat = latLon[0];
        double lon = latLon[1];

        // Construct the URL for the Open-Meteo API to get current weather
        String currentWeatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true&timezone=America/Chicago";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(currentWeatherUrl, Object.class);
    }

    @GetMapping("/api/weather/forecast")
    public Object getWeatherForecast(@RequestParam String location) {
        // Get latitude and longitude from the location using the geocoding service
        double[] latLon = getLatLon(location);
        double lat = latLon[0];
        double lon = latLon[1];

        // Construct the URL for the Open-Meteo API to get daily forecast
        String forecastUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum,windspeed_10m_max,windgusts_10m_max,uv_index_max&timezone=America/Chicago";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(forecastUrl, Object.class);
    }

    @GetMapping("/api/weather/hourly")
    public Object getHourlyForecast(@RequestParam String location) {
        // Get latitude and longitude from the location using the geocoding service
        double[] latLon = getLatLon(location);
        double lat = latLon[0];
        double lon = latLon[1];

        // Construct the URL for the Open-Meteo API to get hourly forecast
        String hourlyForecastUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&hourly=temperature_2m,weathercode,windspeed_10m,relativehumidity_2m,cloudcover,uv_index&timezone=America/Chicago";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(hourlyForecastUrl, Object.class);
    }

    // Method to get latitude and longitude using OpenCage Geocoding API
    public double[] getLatLon(String location) {
        String geocodingUrl = "https://api.opencagedata.com/geocode/v1/json?q=" + location + "&key=" + OPENCAGE_API_KEY;

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(geocodingUrl, Map.class);

        // Extract latitude and longitude from the response
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results.isEmpty()) {
            throw new RuntimeException("Location not found: " + location);
        }

        Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
        double lat = (double) geometry.get("lat");
        double lon = (double) geometry.get("lng");

        return new double[] { lat, lon };
    }
}
