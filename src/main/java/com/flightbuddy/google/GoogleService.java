package com.flightbuddy.google;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.schedule.GoogleTask;
import com.flightbuddy.google.schedule.GoogleTrigger;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.results.FoundTrip;

@Service
public class GoogleService {

	Logger log = LoggerFactory.getLogger(GoogleService.class);
	
	@Autowired GoogleConnectionService googleConnectionService;
	@Autowired GoogleTask googleTask;

	@Value("${flights.travel.to}")
	private String travelTo;
	@Value("${flights.travel.from}")
	private String travelFrom;
	@Value("${flights.travel.price}")
	private String travelPrice;
	@Value("${flights.travel.with.return}")
	private boolean travelWithReturn;
	@Value("${google.date.format}")
	private String dateFormat;
	@Value("${google.request.max}")
	private int maxAmountOfRequests;
	
	public List<FoundTrip> getTrips(SearchInputData searchInputData) {
		GoogleResponse response = googleConnectionService.askGoogleForTheTrips(searchInputData);
		Trips trips = response.getTrips();
		if (trips == null || trips.getTripOption() == null || trips.getTripOption().length == 0) {
			handleResponseWithoutFlights(searchInputData);
		}
		return GoogleFlightConverter.convertResponseToTrips(response);
	}
	
	public Trigger getTrigger() {
		return new GoogleTrigger(getSearchInputData().size(), maxAmountOfRequests);
	}
	
	public Runnable getTask(SearchInputData searchInputData) {
		googleTask.setSearchInputData(searchInputData);
		return googleTask;
	}
	
	public List<SearchInputData> getSearchInputData() {
		List<SearchInputData> searchInputDataList = new ArrayList<>(1);
		LocalDate[] dates = new LocalDate[] {LocalDate.now().plusWeeks(2), LocalDate.now().plusWeeks(2).plusDays(2)};
		SearchInputData searchInputData = new SearchInputData(travelFrom, travelTo, travelPrice, dates, travelWithReturn);
		searchInputDataList.add(searchInputData);
		return searchInputDataList;
	}
	
	public int getMaxAmountOfRequests() {
		return maxAmountOfRequests;
	}

	private void handleResponseWithoutFlights(SearchInputData searchInputData) {
		LocalDate[] dates = searchInputData.getDates();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		String fromDate = "", returnDate = "";
		if (dates != null && dates.length > 0) {
			fromDate = dates[0].format(formatter);
			returnDate = formatReturnDate(dates, formatter);
		}
		log.error(Messages.get("error.google.flights.no", new Object[]{searchInputData.getPrice(), fromDate, returnDate,
				searchInputData.getFrom(), searchInputData.getTo()}));
	}

	private String formatReturnDate(LocalDate[] dates, DateTimeFormatter formatter) {
		if (dates.length > 1) {
			return dates[1].format(formatter);
		}
		return "";
	}
}
