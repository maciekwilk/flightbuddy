package com.flightbuddy.google.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

public class GoogleTrigger implements Trigger {

	@Override 
	public Date nextExecutionTime(TriggerContext triggerContext) {
    	Calendar nextExecutionTime =  new GregorianCalendar();
        Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
        nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
        nextExecutionTime.add(Calendar.MILLISECOND, 10000); 
        return nextExecutionTime.getTime();
	}

}
