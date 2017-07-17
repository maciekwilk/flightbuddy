package com.flightbuddy.mails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.flightbuddy.results.Airline;
import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.Stop;

@Component
public class MessageWriter {
	
	private String dateFormat = "uuuu-MM-dd HH:mm";

	public String prepareMessage(List<FoundTrip> foundTrips) {
        String message = foundTrips.stream().map( 
        		foundTrip -> getMessagePartFrom(foundTrip)
        		).collect(Collectors.joining());
        return message;
	}
	
	private String getMessagePartFrom(FoundTrip foundTrip) {
		String flightsInfo = extractFlightsInfo(foundTrip);
		return "{id: " + foundTrip.getId() + ", price: " + String.valueOf(foundTrip.getPrice()) + 
				",\nflights: [\n" + flightsInfo + "]}\n";
	}

	private String extractFlightsInfo(FoundTrip foundTrip) {
		List<Flight> flights = foundTrip.getFlights();
		String flightsInfo = flights.stream().map( 
        		flight -> getMessagePartFrom(flight)
        		).collect(Collectors.joining());
		return flightsInfo;
	}
	
	private String getMessagePartFrom(Flight flight) {
		String stopsInfo = extractStopsInfo(flight);
		String airlinesInfo = extractAirlinesInfo(flight);
		String date = parseDate(flight.getDate());
		return "{stops: " + stopsInfo + ", airlines: " + airlinesInfo + ", date: " + date + "}\n";
	}

	private String extractStopsInfo(Flight flight) {
		List<Stop> stops = flight.getStops();
		String stopsInfo = stops.stream()
				.map(stop -> stop.getCode())
				.collect(Collectors.joining(" -> "));
		return stopsInfo;
	}

	private String extractAirlinesInfo(Flight flight) {
		List<Airline> airlines = flight.getAirlines();
		String airlinesInfo = airlines.stream()
				.map(airline -> airline.getName())
				.collect(Collectors.joining(" -> "));
		return airlinesInfo;
	}

	private String parseDate(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		return formatter.format(date) + ", " + date.getDayOfWeek().toString();
	}
}
