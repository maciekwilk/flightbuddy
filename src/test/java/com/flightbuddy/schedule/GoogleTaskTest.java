package com.flightbuddy.schedule;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;
import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.google.schedule.GoogleTask;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GoogleTaskTest {
	
	@Autowired GoogleTask googleTask;
	
	@MockBean
	private GoogleService googleService;
    @MockBean
    private FoundTripService foundTripService;
    @MockBean
    private MailService mailService;
	
    private SearchInputData emptyInputData;
    
    @Before
	public void setUp() {
		emptyInputData = new SearchInputData(null, null, null, new LocalDate[]{}, false);
	}
    
	@Test
	public void runGoogleTaskWithNullSearchInputData() {
		googleTask.setSearchInputData(null);
		googleTask.run();
		verify(googleService, times(0)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
	}

	@Test
	public void runGoogleTaskWithEmptyTrips() {
		List<FoundTrip> emptyTrips = new ArrayList<>(0);
		when(googleService.getTrips(eq(emptyInputData))).thenReturn(emptyTrips);
		googleTask.setSearchInputData(emptyInputData);
		googleTask.run();
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
	}
	
	@Test
	public void runGoogleTask() {
		List<FoundTrip> trips = new ArrayList<>(1);
		FoundTrip trip = new FoundTrip();
		trips.add(trip);
		when(googleService.getTrips(eq(emptyInputData))).thenReturn(trips);
		googleTask.setSearchInputData(emptyInputData);
		googleTask.run();
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(1)).saveFoundTrips(any());
		verify(mailService, times(1)).sendTrips(any());
	}
}
