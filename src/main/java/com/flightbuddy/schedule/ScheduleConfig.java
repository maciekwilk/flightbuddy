package com.flightbuddy.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.flightbuddy.resources.Messages;

@Configuration
@EnableScheduling
public class ScheduleConfig implements SchedulingConfigurer {

	Logger log = Logger.getLogger(ScheduleConfig.class);
	
	@Autowired TriggerTasksService triggerTasksService;
	@Autowired ScheduleTrigger scheduleTrigger;
	@Autowired ScheduleTask scheduleTask;
	
	private final static int NUMBER_OF_THREADS = 10;
	
	@Value("${schedule.enable}")
	private boolean scheduleEnabled;
	

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(NUMBER_OF_THREADS);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskExecutor());
    	if (scheduleEnabled) {
    		log.info(Messages.get("schedule.enabled"));
        	Map<Runnable, Trigger> triggerTasks = new HashMap<>();
        	triggerTasks.put(scheduleTask, scheduleTrigger);
            taskRegistrar.setTriggerTasks(triggerTasks);
    	} 
    }
}