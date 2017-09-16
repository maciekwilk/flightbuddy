package com.flightbuddy.airports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirportController {
	
	@Autowired AirportService airportService;

	@RequestMapping("/airport/all")
	public List<String> getAllAirports() {
		return airportService.getAirportList();
	}
}
