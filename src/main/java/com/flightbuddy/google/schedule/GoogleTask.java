package com.flightbuddy.google.schedule;

import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;

public class GoogleTask implements Runnable {
	
	Logger log = Logger.getLogger(GoogleTask.class);

	@Autowired GoogleService googleService;
    @Autowired FoundTripService foundTripService;
    @Autowired MailService mailService;
    
	@Value("${flights.travel.to}")
	private String travelTo;
	@Value("${flights.travel.from}")
	private String travelFrom;
	@Value("${flights.travel.price}")
	private String travelPrice;
	@Value("${flights.travel.with.return}")
	private boolean travelWithReturn;
    
	@Override
	public void run() {
		log.info("TRIGGERED!");
//		SearchInputData inputData = prepareInputData();
//		List<FoundTrip> foundTrips = googleService.getGoogleTrips(inputData);
//    	foundTripService.saveFoundTrips(foundTrips);
//    	mailService.sendTrips(foundTrips);
	}

	private SearchInputData prepareInputData() {
		SearchInputData inputData = new SearchInputData();
    	inputData.setDates(new LocalDate[] {LocalDate.now().plusWeeks(1), LocalDate.now().plusWeeks(1).plusDays(2)});
    	inputData.setFrom(travelFrom);
    	inputData.setTo(travelTo);
    	inputData.setPrice(travelPrice);
    	inputData.setWithReturn(travelWithReturn);
		return inputData;
	}
}