package com.flightbuddy.google;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.exceptions.NoFlightsFoundException;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.results.FoundTrip;

@Service
public class GoogleService {

	@Autowired
	private GoogleConnectionService googleConnectionService;
	@Autowired
	private GoogleFlightConverter googleFlightConverter;
	
	@Value("${google.date.format}")
	private String dateFormat = "uuuu-MM-dd";
	
	public List<FoundTrip> getGoogleTrips(SearchInputData searchInputData) {
		GoogleResponse response = googleConnectionService.askGoogleForTheTrips(searchInputData);
		Trips trips = response.getTrips();
		if (trips == null || trips.getTripOption() == null || trips.getTripOption().length == 0) {
			handleResponseWithoutFlights(searchInputData);
		}
		return googleFlightConverter.convertResponseToTrips(response);
	}

	private void handleResponseWithoutFlights(SearchInputData searchInputData) {
		LocalDate[] dates = searchInputData.getDates();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		String fromDate = "", returnDate = "";
		if (dates != null && dates.length > 0) {
			fromDate = dates[0].format(formatter);
			returnDate = formatReturnDate(dates, formatter);
		}
		throw new NoFlightsFoundException(Messages.get("error.google.flights.no", new Object[]{searchInputData.getPrice(), fromDate, returnDate,
				searchInputData.getFrom(), searchInputData.getTo()}));
	}

	private String formatReturnDate(LocalDate[] dates, DateTimeFormatter formatter) {
		if (dates.length > 1) {
			return dates[1].format(formatter);
		}
		return "";
	}
	
	public void setGoogleConnectionService(GoogleConnectionService googleConnectionService) {
		this.googleConnectionService = googleConnectionService;
	}
	
	public void setGoogleFlightConverter(GoogleFlightConverter googleFlightConverter) {
		this.googleFlightConverter = googleFlightConverter;
	}
}
