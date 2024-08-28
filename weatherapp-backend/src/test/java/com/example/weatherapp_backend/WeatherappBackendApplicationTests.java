package com.example.weatherapp_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class WeatherappBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	public static void main(String[] args) {
		String API_KEY = "0176d07cba1b5186c8873f6c09ceb4c8";
		String location = "Chicago";
		String BASE_URL = "http://api.openweathermap.org/data/2.5/";

		// Test current weather URL
		String currentWeatherUrl = BASE_URL + "weather?q=" + location + "&appid=" + API_KEY;
		System.out.println("Testing Current Weather URL: " + currentWeatherUrl);

		RestTemplate restTemplate = new RestTemplate();
		try {
			Object currentWeatherResponse = restTemplate.getForObject(currentWeatherUrl, Object.class);
			System.out.println("Current Weather Response: " + currentWeatherResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
