package com.flightbuddy.google;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertThat(result, equalTo(Collections.EMPTY_LIST));
	}
	
	@Test
	public void checkConvertResponseToTripsWithNullPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithLetters() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR5R4", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(BigDecimal.ZERO));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithTooShortCurrency() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EU540", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(new BigDecimal(40)));
	}

	@Test
	public void checkConvertResponseToTripsWithPriceWithoutCents() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(new BigDecimal(540.0)));
	}
	
	@Test
	public void checkConvertResponseToTripsWithPriceWithCents() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540.40", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(createBigDecimal(540.40)));
	}
	
	@Test
	public void checkConvertResponseToTrpisWithMinimalPrice() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540.40", new Slice[0]), createTripOption("EUR200.00", new Slice[0]),
				createTripOption("EUR300.40", new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		int minPrice = 250;
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, minPrice);
		assertThat(result.size(), equalTo(2));
		FoundTrip trip = result.get(0);
		assertThat(trip.getPrice(), equalTo(createBigDecimal(540.40)));
		trip = result.get(1);
		assertThat(trip.getPrice(), equalTo(createBigDecimal(300.40)));
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutSlices() {
		TripData tripData = new TripData();
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, new Slice[0])};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertThat(trip.getFlights(), equalTo(Collections.EMPTY_LIST));
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutSegments() {
		TripData tripData = new TripData();
		Segment[] segments = new Segment[0];
		Slice[] slices = new Slice[1];
		slices[0] = createSlice(segments, 0);
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertDateAndStopsAndDurationAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutLegs() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[0];
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertDateAndStopsAndDurationAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutTripData() {
		Leg[] legs = new Leg[0];
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, null);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertDateAndStopsAndDurationAreNull(result);
	}
	
	@Test
	public void checkConvertResponseToTripsWithEmptyLeg() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{null, null});
	}
	
	@Test
	public void checkConvertResponseToTripsWithoutValidCarrier() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL")};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertAirlinesEqualTo(result, new String[]{"KL"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithValidCarrier() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal"), createCarrier("KL", "KLM Airlines")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL")};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertAirlinesEqualTo(result, new String[]{"KLM Airlines"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoValidCarriers() {
		TripData tripData = new TripData();
		Carrier[] carriers = new Carrier[] {createCarrier("EJ", "EasyJet"), createCarrier("TP", "Tap Portugal"), createCarrier("KL", "KLM Airlines")};
		tripData.setCarrier(carriers);
		Leg[] legs = new Leg[] {createLeg(null, null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, "KL"), createSegment(legs, "EJ")};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		assertAirlinesEqualTo(result, new String[]{"KLM Airlines", "EasyJet"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithNotISO8601Date() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, "2011-12-03", null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDate(), equalTo(DEFAULT_DATE));
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithValidDate() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, null, "2011-12-03T10:15+01:00", "2011-12-03T15:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 0)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDate(), equalTo(parseLocalDate("2011-12-03T10:15+01:00")));
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithOrigin() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", null, null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{"BASEL", null});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithDestination() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg(null, "CRACOW", null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{null, "CRACOW"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithOriginAndDestination() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", null, null)};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		assertDateAndStopsAndDurationEqualTo(trip, DEFAULT_DATE, new String[]{"BASEL", "CRACOW"});
	}
	
	@Test
	public void checkConvertResponseToTripsWithLegWithTimes() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00", "2011-12-05T12:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		List<LocalDateTime> times = prepareTimes(null, "2011-12-03T10:15+01:00", "2011-12-05T12:15+01:00", null);
		assertDateAndStopsAndDurationEqualTo(trip, parseLocalDate("2011-12-03T10:15+01:00"), times, new String[]{"BASEL", "CRACOW"}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoLegs() {
		TripData tripData = new TripData();
		Leg[] legs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00", "2011-12-03T12:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+05:00", "2011-12-03T16:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(legs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		List<LocalDateTime> times = prepareTimes(null, "2011-12-03T10:15+01:00", "2011-12-03T12:15+01:00", "2011-12-03T15:15+05:00", "2011-12-03T16:15+01:00", null);
		assertDateAndStopsAndDurationEqualTo(trip, parseLocalDate("2011-12-03T10:15+01:00"), times, new String[]{"BASEL", "CRACOW", "LONDON"}, 120);
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoSlices() {
		TripData tripData = new TripData();
		Leg[] firstLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00", "2011-12-03T12:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+01:00", "2011-12-03T16:15+01:00")};
		Leg[] secondLegs = new Leg[] {createLeg("LONDON", "AMSTERDAM", "2011-12-05T10:15+01:00", "2011-12-05T12:15+01:00"), createLeg("AMSTERDAM", "BASEL", "2011-12-05T15:15+01:00", "2011-12-05T16:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(firstLegs, null)};
		Segment[] secondSegments = new Segment[] {createSegment(secondLegs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120), createSlice(secondSegments, 60)};
		TripOption[] tripOptions = new TripOption[] {createTripOption(null, slices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		List<LocalDateTime> firstTimes = prepareTimes(null, "2011-12-03T10:15+01:00", "2011-12-03T12:15+01:00", "2011-12-03T15:15+05:00", "2011-12-03T16:15+01:00", null);
		List<LocalDateTime> secondTimes = prepareTimes(null, "2011-12-05T10:15+01:00", "2011-12-05T12:15+01:00", "2011-12-05T15:15+05:00", "2011-12-05T16:15+01:00", null);
		assertDateAndStopsAndDurationEqualTo(flights.get(0), parseLocalDate("2011-12-03T10:15+01:00"), firstTimes, new String[] {"BASEL", "CRACOW", "LONDON"}, 120);
		assertDateAndStopsAndDurationEqualTo(flights.get(1), parseLocalDate("2011-12-05T10:15+01:00"), secondTimes, new String[] {"LONDON", "AMSTERDAM", "BASEL"}, 60);
	}
	
	@Test
	public void checkConvertResponseToTripsWithTwoTrips() {
		TripData tripData = new TripData();
		Leg[] firstLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-03T10:15+01:00", "2011-12-03T12:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-03T15:15+01:00", "2011-12-04T16:15+01:00")};
		Segment[] segments = new Segment[] {createSegment(firstLegs, null)};
		Slice[] slices = new Slice[] {createSlice(segments, 120)};
		Leg[] secondLegs = new Leg[] {createLeg("BASEL", "CRACOW", "2011-12-05T10:15+01:00", "2011-12-05T12:15+01:00"), createLeg("CRACOW", "LONDON", "2011-12-05T15:15+01:00", "2011-12-05T16:15+01:00")};
		Segment[] secondSegments = new Segment[] {createSegment(secondLegs, null)};
		Slice[] secondSlices = new Slice[] {createSlice(secondSegments, 60)};
		TripOption[] tripOptions = new TripOption[] {createTripOption("EUR540", slices), createTripOption("EUR240", secondSlices)};
		GoogleResponse googleResponse = createGoogleResponse(tripOptions, tripData);
		List<FoundTrip> result = GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
		List<LocalDateTime> firstTimes = prepareTimes(null, "2011-12-03T10:15+01:00", "2011-12-03T12:15+01:00", "2011-12-03T15:15+05:00", "2011-12-03T16:15+01:00", null);
		List<LocalDateTime> secondTimes = prepareTimes(null, "2011-12-05T10:15+01:00", "2011-12-05T12:15+01:00", "2011-12-05T15:15+05:00", "2011-12-05T16:15+01:00", null);
		assertPriceAndDateAndStopsAndDurationEqualTo(result.get(0), 540, parseLocalDate("2011-12-03T10:15+01:00"), firstTimes, new String[]{"BASEL", "CRACOW", "LONDON"}, 120);
		assertPriceAndDateAndStopsAndDurationEqualTo(result.get(1), 240, parseLocalDate("2011-12-05T10:15+01:00"), secondTimes, new String[]{"BASEL", "CRACOW", "LONDON"}, 60);
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

	private Leg createLeg(String origin, String destination, String departureTime, String arrivalTime) {
		Leg leg = new Leg();
		leg.setDestination(destination);
		leg.setOrigin(origin);
		leg.setDepartureTime(departureTime);
		leg.setArrivalTime(arrivalTime);
		return leg;
	}
	
	private Carrier createCarrier(String code, String name) {
		Carrier carrier = new Carrier();
		carrier.setCode(code);
		carrier.setName(name);
		return carrier;
	}
	
	private List<LocalDateTime> prepareTimes(String... times) {
		return Arrays.stream(times)
				.map(time -> time == null ?  null : parseLocalDate(time))
				.collect(Collectors.toList());
	}

	private void assertDateAndStopsAndDurationAreNull(List<FoundTrip> result) {
		FoundTrip trip = result.get(0);
		List<Flight> flights = trip.getFlights();
		Flight flight = flights.get(0);
		assertThat(flight.getDuration(), equalTo(0));
		assertThat(flight.getDate(), equalTo(null));
		assertThat(flight.getStops(), equalTo(null));
	}
	
	private void assertPriceAndDateAndStopsAndDurationEqualTo(FoundTrip trip, double price, LocalDateTime date, List<LocalDateTime> times, String[] stopCodes, int duration) {
		assertThat(trip.getPrice(), equalTo(new BigDecimal(price)));
		assertDateAndStopsAndDurationEqualTo(trip, date, times, stopCodes, duration);
	}
	private void assertDateAndStopsAndDurationEqualTo(FoundTrip trip, LocalDateTime date, String[] stopCodes) {
		List<Flight> flights = trip.getFlights();
		assertDateAndStopsAndDurationEqualTo(flights.get(0), date, stopCodes);
	}
	
	private void assertDateAndStopsAndDurationEqualTo(Flight flight, LocalDateTime date, String[] stopCodes) {
		assertThat(flight.getDuration(), equalTo(120));
		assertThat(flight.getDate(), equalTo(date));
		List<Stop> stops = flight.getStops();
		for (int i = 0; i < stopCodes.length - 1; i++) {
			Stop stop = stops.get(i);
			assertThat(stop.getCode(), equalTo(stopCodes[i]));
		}
	}

	private void assertDateAndStopsAndDurationEqualTo(FoundTrip trip, LocalDateTime date, List<LocalDateTime> times, String[] stopCodes, int duration) {
		List<Flight> flights = trip.getFlights();
		assertDateAndStopsAndDurationEqualTo(flights.get(0), date, times, stopCodes, duration);
	}
	
	private void assertDateAndStopsAndDurationEqualTo(Flight flight, LocalDateTime date, List<LocalDateTime> times, String[] stopCodes, int duration) {
		assertThat(flight.getDuration(), equalTo(duration));
		assertThat(flight.getDate(), equalTo(date));
		List<Stop> stops = flight.getStops();
		for (int i = 0; i < stopCodes.length - 1; i++) {
			assertStop(times, stopCodes, stops, i);
		}
	}

	private void assertStop(List<LocalDateTime> times, String[] stopCodes, List<Stop> stops, int i) {
		Stop stop = stops.get(i);
		LocalDateTime arrivalTime = times.get(2 * i);
		assertThat(stop.getArrivalTime(), equalTo(arrivalTime));
		LocalDateTime departureTime = times.get(2 * i + 1);
		assertThat(stop.getDepartureTime(), equalTo(departureTime));
		assertThat(stop.getCode(), equalTo(stopCodes[i]));
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