package com.flightbuddy;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flightbuddy")
public class MainController {
	
	@RequestMapping("/")
	public void handleaTrips() {
		
	}
}