package com.flightbuddy.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class SearchResultDetails {
	
	private final List<SearchResultDetailsEntry> searchResultDetailsEntries;
	
	SearchResultDetails(List<SearchResultDetailsEntry> searchResultDetailsEntries) {
		this.searchResultDetailsEntries = Collections.unmodifiableList(new ArrayList<>(searchResultDetailsEntries));
	}

	public List<SearchResultDetailsEntry> getSearchResultDetailsEntries() {
		return searchResultDetailsEntries;
	}
}
