package com.flightbuddy.schedule;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;

import com.flightbuddy.google.GoogleService;

@Service
public class TriggerTasksService {

	@Autowired GoogleService googleService;
	
	public Map<Runnable, Trigger> createTriggerTasks() {
		Map<Runnable, Trigger> triggerTasks = new HashMap<>();
    	addTriggerTasksForGoogle(triggerTasks);
		return triggerTasks;
	}

	private void addTriggerTasksForGoogle(Map<Runnable, Trigger> triggerTasks) {
		Runnable runnable = googleService.getTask();
        Trigger trigger = googleService.getTrigger();
        triggerTasks.put(runnable, trigger);
	}
}
