package com.flightbuddy.mails;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.search.SearchDataConverter;
import com.flightbuddy.search.SearchResult;

@Component
public class MessageWriter {

	public String prepareMessage(List<FoundTrip> foundTrips) {
        String message = foundTrips.stream().map( 
        		foundTrip -> getMessagePartFrom(foundTrip)
        		).collect(Collectors.joining());
        return message;
	}
	
	private String getMessagePartFrom(FoundTrip foundTrip) {
		SearchResult searchResult = SearchDataConverter.convertToSearchResult(foundTrip);
		return "Flight code: " + foundTrip.getId() + "\n" +
				"Price: " + String.valueOf(foundTrip.getPrice()) + "\n" + 
				"Hours:\n" + extractInfo(searchResult.getHours()) + "\n" +
				"Durations:\n" + extractInfo(searchResult.getDurations()) + "\n" +
				"Trip:\n" + extractInfo(searchResult.getTrips()) + "\n" +
				"Stops:\n" + extractStopsInfo(searchResult.getStops()) + "\n";
	}
	
	private String extractInfo(List<String> infos) {
		return infos.stream()
				.map(info -> "\t" + info)
				.collect(Collectors.joining("\n"));
	}
	
	private String extractStopsInfo(List<Integer> stops) {
		return stops.stream()
				.map(stop -> "\t" + stop)
				.collect(Collectors.joining("\n"));
	}
}
