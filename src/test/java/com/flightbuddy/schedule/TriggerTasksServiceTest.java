package com.flightbuddy.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.Trigger;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;
import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.GoogleService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TriggerTasksServiceTest {
	
	@Autowired TriggerTasksService triggerTasksService;
	
	@MockBean
	private GoogleService googleService;
	
	@Test
	public void checkGoogleTasksEmptyList() {
		List<SearchInputData> searchInputDataList = new ArrayList<>(0);
		when(googleService.getSearchInputData()).thenReturn(searchInputDataList);
		Runnable googleRunnable = mock(Runnable.class);
		when(googleService.getTask(any(SearchInputData.class))).thenReturn(googleRunnable);
        Trigger googleTrigger = mock(Trigger.class);
        when(googleService.getTrigger()).thenReturn(googleTrigger);
		googleService = mock(GoogleService.class);
		Map<Runnable, Trigger> googleTasks = triggerTasksService.createTriggerTasks();
		
		assertThat(googleTasks.isEmpty(), equalTo(true));
	}
	
	@Test
	public void checkGoogleTasksOneSearchInputData() {
		List<SearchInputData> searchInputDataList = createOneSearchInputDataList();
		when(googleService.getSearchInputData()).thenReturn(searchInputDataList);
		Runnable googleRunnable = mock(Runnable.class);
		when(googleService.getTask(any(SearchInputData.class))).thenReturn(googleRunnable);
        Trigger googleTrigger = mock(Trigger.class);
        when(googleService.getTrigger()).thenReturn(googleTrigger);
		googleService = mock(GoogleService.class);
		Map<Runnable, Trigger> googleTasks = triggerTasksService.createTriggerTasks();
		
		assertThat(googleTasks.size(), equalTo(1));
		assertThat(googleTasks.containsKey(googleRunnable), equalTo(true));
		assertThat(googleTasks.get(googleRunnable), equalTo(googleTrigger));
	}
	
	@Test
	public void checkGoogleTasksManySearchInputData() {
		List<SearchInputData> searchInputDataList = createAndMockThreeSearchInputDataList();
		when(googleService.getSearchInputData()).thenReturn(searchInputDataList);
        Trigger googleTrigger = mock(Trigger.class);
        when(googleService.getTrigger()).thenReturn(googleTrigger);
		googleService = mock(GoogleService.class);
		Map<Runnable, Trigger> googleTasks = triggerTasksService.createTriggerTasks();
		
		assertThat(googleTasks.size(), equalTo(3));
	}
	
	private List<SearchInputData> createOneSearchInputDataList() {
		List<SearchInputData> searchInputDataList = new ArrayList<>();
		searchInputDataList.add(new SearchInputData(null, null, null, new LocalDate[0], false));
		return searchInputDataList;
	}
	
	private List<SearchInputData> createAndMockThreeSearchInputDataList() {
		List<SearchInputData> searchInputDataList = new ArrayList<>();
		searchInputDataList.add(createAndMockSearchInputData());
		searchInputDataList.add(createAndMockSearchInputData());
		searchInputDataList.add(createAndMockSearchInputData());
		return searchInputDataList;
	}

	private SearchInputData createAndMockSearchInputData() {
		SearchInputData searchInputData = new SearchInputData(null, null, null, new LocalDate[0], false);
		Runnable googleRunnable = mock(Runnable.class);
		when(googleService.getTask(eq(searchInputData))).thenReturn(googleRunnable);
		return searchInputData;
	}
}
