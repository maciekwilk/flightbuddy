package com.flightbuddy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flightbuddy")
public class MainController {
    @Autowired FlightService flightService;

    @GetMapping("/google")
    public void google() {
    	flightService.handleGoogleTrips();
    }

}
