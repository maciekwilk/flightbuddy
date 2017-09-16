package com.flightbuddy.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SearchResultDetails {
	
	private final List<SearchResultDetailsEntry> searchResultDetailsEntries;
	
	public SearchResultDetails(List<SearchResultDetailsEntry> searchResultDetailsEntries) {
		this.searchResultDetailsEntries = Collections.unmodifiableList(new ArrayList<>(searchResultDetailsEntries));
	}

	public List<SearchResultDetailsEntry> getSearchResultDetailsEntries() {
		return searchResultDetailsEntries;
	}
}
