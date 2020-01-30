package com.flightbuddy.results;

import java.util.Collections;
import java.util.List;

public class FoundTripsWrapper {

    private List<FoundTrip> foundTrips;
    private String errorMessage;

    public FoundTripsWrapper(List<FoundTrip> foundTrips) {
        this.foundTrips = foundTrips;
        this.errorMessage = "";
    }

    public FoundTripsWrapper(String errorMessage) {
        this.foundTrips = Collections.emptyList();
        this.errorMessage = errorMessage;
    }

    public List<FoundTrip> getFoundTrips() {
        return foundTrips;
    }

    public void setFoundTrips(List<FoundTrip> foundTrips) {
        this.foundTrips = foundTrips;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
