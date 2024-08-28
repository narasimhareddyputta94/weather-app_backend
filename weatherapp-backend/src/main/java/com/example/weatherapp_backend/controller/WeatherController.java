package com.example.weatherapp_backend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class WeatherController {

    private final String API_KEY = "0176d07cba1b5186c8873f6c09ceb4c8";  // Replace with your OpenWeatherMap API key
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

    @GetMapping("/api/weather/current")
    public Object getCurrentWeather(@RequestParam String location) {
        String url = BASE_URL + "weather?q=" + location + "&appid=" + API_KEY + "&units=imperial";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Object.class);
    }

    @GetMapping("/api/weather/forecast")
    public Object getWeatherForecast(@RequestParam String location) {
        String url = BASE_URL + "forecast?q=" + location + "&appid=" + API_KEY + "&units=imperial";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Object.class);
    }
}
