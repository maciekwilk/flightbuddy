package com.flightbuddy.google;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.flightbuddy.google.request.GoogleRequest;
import com.flightbuddy.google.request.Passengers;
import com.flightbuddy.google.request.Request;
import com.flightbuddy.google.request.Slice;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.error.Error;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.search.ImmutablePassengers;
import com.flightbuddy.search.ImmutableSearchInputData;

@Service
class GoogleConnectionService {
	
	private final Logger log = LoggerFactory.getLogger(GoogleConnectionService.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Value("${google.date.format}")
	private String dateFormat;
	@Value("${google.currency}")
	private String currency;
	@Value("${google.api.key}")
	private String googleApiKey;
	@Value("${google.request.url}")
	private String requestUrl;

//	@Hysterix rest
	public GoogleResponse askGoogleForTheTrips(ImmutableSearchInputData searchInputData) {
		try {
			RequestEntity<GoogleRequest> requestEntity = prepareRequestEntity(searchInputData);
			ResponseEntity<GoogleResponse> response = restTemplate.exchange(requestEntity, GoogleResponse.class);
			return handleResponse(response);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return new GoogleResponse();
	}

	private RequestEntity<GoogleRequest> prepareRequestEntity(ImmutableSearchInputData searchInputData) throws URISyntaxException {
		GoogleRequest googleRequest = createGoogleRequest(searchInputData);
		MultiValueMap<String, String> headers = createHeaders();
		URI uri = new URI(requestUrl + "?key=" + googleApiKey);
		return new RequestEntity<>(googleRequest, headers, HttpMethod.POST, uri);
	}

	private GoogleRequest createGoogleRequest(ImmutableSearchInputData searchInputData) {
		Request request = new Request();
		request.setPassengers(createPassengers(searchInputData));
		request.setMaxPrice(currency + searchInputData.getMaxPrice() + ".00");
		Slice[] slices = new Slice[2];
		slices[0] = createSlice(searchInputData.getFrom(), searchInputData.getTo(), searchInputData.getDates()[0]);
		if (searchInputData.isWithReturn()) {
			slices[1] = createSlice(searchInputData.getTo(), searchInputData.getFrom(), searchInputData.getDates()[1]);
		} 
		request.setSlice(slices);
		return new GoogleRequest(request);
	}
	
	private Passengers createPassengers(ImmutableSearchInputData searchInputData) {
		Passengers passengers = new Passengers();
		ImmutablePassengers searchInputPassengers = searchInputData.getPassengers();
		passengers.setAdultCount(searchInputPassengers.getAdultCount());
		passengers.setChildCount(searchInputPassengers.getChildCount());
		passengers.setInfantInLapCount(searchInputPassengers.getInfantInLapCount());
		passengers.setInfantInSeatCount(searchInputPassengers.getInfantInSeatCount());
		passengers.setSeniorCount(searchInputPassengers.getSeniorCount());
		return passengers;
	}

	private Slice createSlice(String from, String to, LocalDate date) {
		Slice slice = new Slice();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		slice.setDate(date.format(formatter));
		slice.setOrigin(from);
		slice.setDestination(to);
		return slice;
	}

	private MultiValueMap<String, String> createHeaders() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(2);
		headers.add("content-type", "application/json");
		headers.add("Accept","application/json");
		return headers;
	}

	private GoogleResponse handleResponse(ResponseEntity<GoogleResponse> response) {
		if (response == null || response.getBody() == null) {
			log.error(Messages.get("error.google.response.empty"));
			return new GoogleResponse();
		}
		GoogleResponse googleResponse = response.getBody();
		if (googleResponse.getError() != null) {
			handleErrorResponse(googleResponse.getError());
		}
	    return googleResponse;
	}

	private void handleErrorResponse(Error googleError) {
		String code = googleError.getCode();
		String message = googleError.getMessage();
		log.error(Messages.get("error.google.response.error", code, message));
	}
}
