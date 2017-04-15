package com.flightbuddy.google.schedule;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;

@Component
public class GoogleTask implements Runnable {
	
	Logger log = Logger.getLogger(GoogleTask.class);

	@Autowired GoogleService googleService;
    @Autowired FoundTripService foundTripService;
    @Autowired MailService mailService;
    
    private SearchInputData searchInputData;
    
	@Override
	public void run() {
		log.info("TRIGGERED!");
		if (searchInputData == null) {
			log.error("empty input data - google task aborted");
			return;
		}
		List<FoundTrip> foundTrips = googleService.getTrips(searchInputData);
		if (!foundTrips.isEmpty()) {
	    	foundTripService.saveFoundTrips(foundTrips);
	    	mailService.sendTrips(foundTrips);
		} else {
			log.error("no trips found in google search");
		}
	}

	public void setSearchInputData(SearchInputData searchInputData) {
		this.searchInputData = searchInputData;
	}
}