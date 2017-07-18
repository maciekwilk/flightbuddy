package com.flightbuddy.google;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.response.tripdata.Carrier;
import com.flightbuddy.google.response.tripdata.TripData;
import com.flightbuddy.google.response.tripoption.TripOption;
import com.flightbuddy.google.response.tripoption.slice.Leg;
import com.flightbuddy.google.response.tripoption.slice.ResponseFlight;
import com.flightbuddy.google.response.tripoption.slice.Segment;
import com.flightbuddy.google.response.tripoption.slice.Slice;
import com.flightbuddy.results.Airline;
import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.Stop;

public class GoogleFlightConverterTest {
	
	private static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2000, 1, 1, 12, 30);
	
	private DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
	@Test
	public void checkConvertResponseToTripsWithEmptyResponse() {
		GoogleResponse googleResponse = createEmptyGoogleResponse();
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertThat(result, equalTo(Collections.EMPTY_LIST));
	}
	
	@Test
	public void checkConvertResponseToTripsWithNullPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithLetters() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR5R4", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithTooShortCurrency() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EU540", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(new BigDecimal(40)));
	}

	@Test
	public void checkConvertResponseToTripsWithPriceWithoutCents() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(new BigDecimal(540.0)));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithCents() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540.40", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(createBigDecimal(540.40)));
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutSlices() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getFlights(), equalTo(Collections.EMPTY_LIST));
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutSegments() {
		TripData tripData = new TripData();
		Segment[] segments = new Segment[0];
		Slice[] slices = new Slice[1];
		slices[0] = createSlice(segments, 0);
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertDateAndStopsAndDurationAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutLegs() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[0];
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertDateAndStopsAndDurationAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutTripData() {
		Leg[] legs = new Leg[0];
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, null);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertDateAndStopsAndDurationAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyLeg() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{null, null}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutValidCarrier() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL")};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertAirlinesEqualTo(result, new String[]{"KL"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithValidCarrier() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal"), createCarrier("KL", "KLM Airlines")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL")};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertAirlinesEqualTo(result, new String[]{"KLM Airlines"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoValidCarriers() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal"), createCarrier("KL", "KLM Airlines")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL"), createSegment(legs, "EJ")};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertAirlinesEqualTo(result, new String[]{"KLM Airlines", "EasyJet"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithNotISO8601Date() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, "2011-12-03")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDate(), equalTo(DEFAULT_DATE));
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithValidDate() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, "2011-12-03T10:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDate(), equalTo(parseLocalDate("2011-12-03T10:15+01:00")));
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithOrigin() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{"BASEL", null}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithDestination() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, "CRACOW", null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{null, "CRACOW"}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithOriginAndDestination() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{"BASEL", "CRACOW"}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoLegs() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2013-01-12T08:15+05:00")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, parseLocalDate("2011-12-03T10:15+01:00"), new String[]{"BASEL", "CRACOW", "LONDON"}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoSlices() {
		TripData tripData = new TripData();
		Leg[] firstLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+01:00")};
		Leg[] secondLegs = new Leg[] {createLeg("LONDON", "AMSTERDAM", "2011-12-05T10:15+01:00"), createLeg("AMSTERDAM", "BASEL", "2011-12-05T12:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(firstLegs, null)};
		Segment[] secondSegments = new Segment[] {createSegment(secondLegs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120), createSlice(secondSegments, 60)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		assertDateAndStopsAndDurationEqualTo(flights.get(0), parseLocalDate("2011-12-03T10:15+01:00"), new String[] {"BASEL", "CRACOW", "LONDON"}, 120);
		assertDateAndStopsAndDurationEqualTo(flights.get(1), parseLocalDate("2011-12-05T10:15+01:00"), new String[] {"LONDON", "AMSTERDAM", "BASEL"}, 60);
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoTrips() {
		TripData tripData = new TripData();
		Leg[] firstLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(firstLegs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		Leg[] secondLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-04T15:15+01:00")};
		Segment[] secondSegments = new Segment[] {createSegment(secondLegs, null)};
		Slice[] secondSlices = new Slice[] {createSlice(secondSegments, 60)};
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540", slices), createTripOption("EUR240", secondSlices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse);
		assertPriceAndDateAndStopsAndDurationEqualTo(result.get(0), 540, parseLocalDate("2011-12-03T10:15+01:00"), new String[]{"BASEL", "CRACOW", "LONDON"}, 120);
		assertPriceAndDateAndStopsAndDurationEqualTo(result.get(1), 240, parseLocalDate("2011-12-03T10:15+01:00"), new String[]{"BASEL", "CRACOW", "LONDON"}, 60);
	}
	
	private GoogleResponse createEmptyGoogleResponse() {
		return createGoogleResponse(new TripOption[0], new TripData());
	}
	
	private GoogleResponse createGoogleResponse(TripOption[] tripOptions, TripData tripData) {
		GoogleResponse googleResponse = new GoogleResponse();
		Trips trips = new Trips();
		trips.setData(tripData);
		trips.setTripOption(tripOptions);
		googleResponse.setTrips(trips);
		return googleResponse;
	}

	private TripOption createTripOption(String price, Slice[] slices) {
		TripOption tripOption = new TripOption();
		tripOption.setSaleTotal(price);
		tripOption.setSlice(slices);
		return tripOption;
	}
	
	private Slice createSlice(Segment[] segments, int duration) {
		Slice slice = new Slice();
		slice.setDuration(duration);
		slice.setSegment(segments);
		return slice;
	}
	
	private Segment createSegment(Leg[] legs, String carrier) {
		Segment segment = new Segment();
		segment.setLeg(legs);
		ResponseFlight flight = new ResponseFlight();
		flight.setCarrier(carrier);
		segment.setFlight(flight);
		return segment;
	}

	private Leg createLeg(String origin, String destination, String departureTime) {
		Leg leg = new Leg();
		leg.setDestination(destination);
		leg.setOrigin(origin);
		leg.setDepartureTime(departureTime);
		return leg;
	}
	
	private Carrier createCarrier(String code, String name) {
		Carrier carrier = new Carrier();
		carrier.setCode(code);
		carrier.setName(name);
		return carrier;
	}

	private void assertDateAndStopsAndDurationAreNull(List<FoundTrip> result) {
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDuration(), equalTo(0));
		assertThat(flight.getDate(), equalTo(null));
		assertThat(flight.getStops(), equalTo(null));
	}
	
	private void assertPriceAndDateAndStopsAndDurationEqualTo(FoundTrip trip, double price, LocalDateTime date, String[] stopCodes, int duration) {
		assertThat(trip.getPrice(), equalTo(new BigDecimal(price)));
		assertDateAndStopsAndDurationEqualTo(trip, date, stopCodes, duration);
	}

	private void assertDateAndStopsAndDurationEqualTo(FoundTrip trip, LocalDateTime date, String[] stopCodes, int duration) {
		List<Flight> flights = trip.getFlights();
		assertDateAndStopsAndDurationEqualTo(flights.get(0), date, stopCodes, duration);
	}
	
	private void assertDateAndStopsAndDurationEqualTo(Flight flight, LocalDateTime date, String[] stopCodes, int duration) {
		assertThat(flight.getDuration(), equalTo(duration));
		assertThat(flight.getDate(), equalTo(date));
		List<Stop> stops = flight.getStops();
		for (int i = 0; i < stopCodes.length - 1; i++) {
			assertThat(stops.get(i).getCode(), equalTo(stopCodes[i]));
		}
	}
	
	private void assertAirlinesEqualTo(List<FoundTrip> result, String[] airlineNames) {
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		List<Airline> airlines = flight.getAirlines();
		for (int i = 0; i < airlineNames.length - 1; i++) {
			assertThat(airlines.get(i).getName(), equalTo(airlineNames[i]));
		}
	}

	private LocalDateTime parseLocalDate(String date) {
		return LocalDateTime.parse(date, formatter);
	}

	private BigDecimal createBigDecimal(double price) {
		return new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);
	}
}