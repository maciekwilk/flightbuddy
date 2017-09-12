package com.flightbuddy.google;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.search.ImmutableSearchInputData;

@Service
public class GoogleService {

	Logger log = LoggerFactory.getLogger(GoogleService.class);
	
	@Autowired GoogleConnectionService googleConnectionService;
	@Autowired ScheduledSearchService scheduledSearchService;

	@Value("${google.date.format}")
	private String dateFormat;
	@Value("${google.request.max}")
	private int maxAmountOfRequests;
	
	public List<FoundTrip> getTrips(ImmutableSearchInputData searchInputData) {
		GoogleResponse response = googleConnectionService.askGoogleForTheTrips(searchInputData);
		Trips trips = response.getTrips();
		if (trips == null || trips.getTripOption() == null || trips.getTripOption().length == 0) {
			handleResponseWithoutFlights(searchInputData);
		}
		return GoogleFlightConverter.convertResponseToTrips(response, searchInputData.getMinPrice());
	}
	
	public int getMaxAmountOfRequests() {
		return maxAmountOfRequests;
	}

	private void handleResponseWithoutFlights(ImmutableSearchInputData searchInputData) {
		LocalDate[] dates = searchInputData.getDates();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		String fromDate = "", returnDate = "";
		if (dates != null && dates.length > 0) {
			fromDate = dates[0].format(formatter);
			returnDate = formatReturnDate(dates, formatter);
		}
		log.error(Messages.get("error.google.flights.no", new Object[]{searchInputData.getMaxPrice(), fromDate, returnDate,
				searchInputData.getFrom(), searchInputData.getTo()}));
	}

	private String formatReturnDate(LocalDate[] dates, DateTimeFormatter formatter) {
		if (dates.length > 1) {
			return dates[1].format(formatter);
		}
		return "";
	}
}
