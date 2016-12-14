package com.flightbuddy.google;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbuddy.SearchInputData;
import com.flightbuddy.exceptions.ConnectionException;
import com.flightbuddy.google.request.GoogleRequest;
import com.flightbuddy.google.request.Passengers;
import com.flightbuddy.google.request.Request;
import com.flightbuddy.google.request.Slice;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.error.Error;
import com.flightbuddy.google.response.error.ErrorResponse;
import com.flightbuddy.resources.Messages;

@Service
public class GoogleConnectionService {
	
	Logger log = LoggerFactory.getLogger(GoogleConnectionService.class);

	@Value("${google.date.format}")
	private String dateFormat;
	@Value("${google.currency}")
	private String currency;
	@Value("${google.api.key}")
	private String googleApiKey;
	@Value("${google.request.url}")
	private String requestUrl;
	
	public GoogleResponse askGoogleForTheTrips(SearchInputData searchInputData) {
		try {
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = prepareHttpPost(searchInputData);
			HttpResponse response = httpclient.execute(httpPost);
			GoogleResponse googleResponse = handleResponse(response);
			return googleResponse;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new GoogleResponse();
	}

	private HttpPost prepareHttpPost(SearchInputData searchInputData) throws UnsupportedEncodingException, JsonProcessingException {
		HttpPost httpPost = new HttpPost(requestUrl + "?key=" + googleApiKey);
		addEntity(searchInputData, httpPost);
		addHeaders(httpPost);
		return httpPost;
	}

	private void addEntity(SearchInputData searchInputData, HttpPost httpPost) throws JsonProcessingException, UnsupportedEncodingException {
		GoogleRequest request = createGoogleRequest(searchInputData);
		String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
		log.info(json);
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);
	}

	private GoogleRequest createGoogleRequest(SearchInputData searchInputData) {
		Request request = new Request();
		request.setPassengers(new Passengers());
		request.setMaxPrice(currency + searchInputData.getPrice());
		Slice[] slices = new Slice[2];
		slices[0] = createSlice(searchInputData.getFrom(), searchInputData.getTo(), searchInputData.getDates()[0]);
		if (searchInputData.isWithReturn()) {
			slices[1] = createSlice(searchInputData.getTo(), searchInputData.getFrom(), searchInputData.getDates()[1]);
		} 
		request.setSlice(slices);
		return new GoogleRequest(request);
	}
	
	private Slice createSlice(String from, String to, LocalDate date) {
		Slice slice = new Slice();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		slice.setDate(date.format(formatter));
		slice.setOrigin(from);
		slice.setDestination(to);
		return slice;
	}

	private void addHeaders(HttpPost httpPost) {
		httpPost.addHeader("content-type", "application/json");
		httpPost.addHeader("Accept","application/json");
	}

	private GoogleResponse handleResponse(HttpResponse response) throws JsonParseException, JsonMappingException, IOException {
		String responseEntity = EntityUtils.toString(response.getEntity());
		if (responseEntity == null || responseEntity.isEmpty()) {
			throw new ConnectionException(Messages.get("error.google.response.empty"));
		}
		if (responseEntity.contains("error")) {
			handleErrorResponse(responseEntity);
		}
	    return new ObjectMapper().readValue(responseEntity, GoogleResponse.class);
	}

	private void handleErrorResponse(String responseEntity) throws JsonParseException, JsonMappingException, IOException {
		ErrorResponse errorResponse = new ObjectMapper().readValue(responseEntity, ErrorResponse.class);
		Error error = errorResponse.getError();
		throw new ConnectionException(Messages.get("error.google.response.error", error.getCode(), error.getMessage()));
	}
}
