package com.flightbuddy.google;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
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
	
	private static final LocalDate DEFAULT_DATE = LocalDate.of(2000, 1, 1);
	
	private GoogleFlightConverter googleFlightConverter;
	private DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	
	@Before
	public void setUp() {
		googleFlightConverter = new GoogleFlightConverter();
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyResponse() {
		GoogleResponse googleResponse = createEmptyGoogleResponse();
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertThat(result, equalTo(Collections.EMPTY_LIST));
	}
	
	@Test
	public void checkConvertResponseToTripsWithNullPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(0.0f));
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(0.0f));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithLetters() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR5R4", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(0.0f));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithTooShortCurrency() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EU540", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(40f));
	}

	@Test
	public void checkConvertResponseToTripsWithPriceWithoutCents() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(540.0f));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithCents() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540.40", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(540.40f));
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutSlices() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertThat(trip.getFlights(), equalTo(Collections.EMPTY_LIST));
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutSegments() {
		TripData tripData = new TripData();
		Segment[] segments = new Segment[0];
		Slice[] slices = new Slice[1];
		slices[0] = createSlice(segments);
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertDateAndStopsAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutLegs() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[0];
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertDateAndStopsAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutTripData() {
		Leg[] legs = new Leg[0];
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, null);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertDateAndStopsAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyLeg() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsEqualTo(trip, DEFAULT_DATE, new String[]{null, null});
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutValidCarrier() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL")};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};;
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertAirlinesEqualTo(result, new String[]{"KL"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithValidCarrier() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal"), createCarrier("KL", "KLM Airlines")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL")};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertAirlinesEqualTo(result, new String[]{"KLM Airlines"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoValidCarriers() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal"), createCarrier("KL", "KLM Airlines")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL"), createSegment(legs, "EJ")};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertAirlinesEqualTo(result, new String[]{"KLM Airlines", "EasyJet"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithNotISO8601Date() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, "2011-12-03")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
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
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
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
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsEqualTo(trip, DEFAULT_DATE, new String[]{"BASEL", null});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithDestination() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, "CRACOW", null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsEqualTo(trip, DEFAULT_DATE, new String[]{null, "CRACOW"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithOriginAndDestination() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsEqualTo(trip, DEFAULT_DATE, new String[]{"BASEL", "CRACOW"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoLegs() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2013-01-12T08:15+05:00")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		assertDateAndStopsEqualTo(trip, parseLocalDate("2011-12-03T10:15+01:00"), new String[]{"BASEL", "CRACOW", "LONDON"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoSlices() {
		TripData tripData = new TripData();
		Leg[] firstLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+01:00")};
		Leg[] secondLegs = new Leg[] {createLeg("LONDON", "AMSTERDAM", "2011-12-05T10:15+01:00"), createLeg("AMSTERDAM", "BASEL", "2011-12-05T12:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(firstLegs, null)};
		Segment[] secondSegments = new Segment[] {createSegment(secondLegs, null)};
		Slice[] slices = new Slice[] {createSlice(segments), createSlice(secondSegments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		assertDateAndStopsEqualTo(flights.get(0), parseLocalDate("2011-12-03T10:15+01:00"), new String[] {"BASEL", "CRACOW", "LONDON"});
		assertDateAndStopsEqualTo(flights.get(1), parseLocalDate("2011-12-05T10:15+01:00"), new String[] {"LONDON", "AMSTERDAM", "BASEL"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoTrips() {
		TripData tripData = new TripData();
		Leg[] firstLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(firstLegs, null)};
		Slice[] slices = new Slice[] {createSlice(segments)};
		Leg[] secondLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-04T15:15+01:00")};
		Segment[] secondSegments = new Segment[] {createSegment(secondLegs, null)};
		Slice[] secondSlices = new Slice[] {createSlice(secondSegments)};
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540", slices), createTripOption("EUR240", secondSlices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = googleFlightConverter.convertResponseToTrips(googleResponse);
		assertPriceAndDateAndStopsEqualTo(result.get(0), 540f, parseLocalDate("2011-12-03T10:15+01:00"), new String[]{"BASEL", "CRACOW", "LONDON"});
		assertPriceAndDateAndStopsEqualTo(result.get(1), 240f, parseLocalDate("2011-12-03T10:15+01:00"), new String[]{"BASEL", "CRACOW", "LONDON"});
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
	
	private Slice createSlice(Segment[] segments) {
		Slice slice = new Slice();
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

	private void assertDateAndStopsAreNull(List<FoundTrip> result) {
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDate(), equalTo(null));
		assertThat(flight.getStops(), equalTo(null));
	}
	
	private void assertPriceAndDateAndStopsEqualTo(FoundTrip trip, float price, LocalDate date, String[] stopCodes) {
		assertThat(trip.getPrice(), equalTo(price));
		assertDateAndStopsEqualTo(trip, date, stopCodes);
	}

	private void assertDateAndStopsEqualTo(FoundTrip trip, LocalDate date, String[] stopCodes) {
		List<Flight> flights = trip.getFlights();
		assertDateAndStopsEqualTo(flights.get(0), date, stopCodes);
	}
	
	private void assertDateAndStopsEqualTo(Flight flight, LocalDate date, String[] stopCodes) {
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

	private LocalDate parseLocalDate(String date) {
		return LocalDate.parse(date, formatter);
	}
}