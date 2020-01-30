package com.flightbuddy.search;

import java.util.Collections;
import java.util.List;

public class SearchResultsWrapper {

    private List<SearchResult> searchResults;
    private String errorMessage;

    SearchResultsWrapper(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
        this.errorMessage = "";
    }

    SearchResultsWrapper(String errorMessage) {
        this.searchResults = Collections.emptyList();
        this.errorMessage = errorMessage;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
