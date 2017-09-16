angular.module('schedule', ['rzModule'])
.controller('schedule', function ($http) {
	
	var self = this;
	
	self.showMessage = false;
	
	self.searchSchedule = {
			from : '',
			to : '',
			minPrice : 0,
			maxPrice : 400,
			dates : [],
			withReturn : false,
			passengers : {
				adultCount : 1,
				childCount : 0,
				infantInLapCount : 0,
				infantInSeatCount : 0,
				seniorCount : 0
			}
	};
	
	self.initAirports = function() {
    	$http.get('/airport/all').then(
			function(response) {
				self.airports = response.data;
			}
    )};
    
    self.initAirports();
    
    self.airportSearch = function(query) {
        var results = query ? self.airports.filter(createFilterFor(query)) : self.airports;
        return results;
    }
    
    function createFilterFor(query) {
        var lowercaseQuery = angular.lowercase(query);
        return function filterFn(airport) {
        	var lowercaseAirport = angular.lowercase(airport);
        	return (lowercaseAirport.indexOf(lowercaseQuery) === 0);
        };
    }
	
	self.slider = {
		options: {
			floor: 0,
			ceil: 3000,
			noSwitching: true,
			draggableRange: true,
			translate: function(value, sliderId, label) {
				switch (label) {
					case 'model':
						return '<b>Min price:</b> ' + value + '&euro;';
					case 'high':
						return '<b>Max price:</b> ' + value + '&euro;';
					default:
						return value + '&euro;';
				}
			}
		}
	};
	
	self.firstDateOptions = {
	    minDate: new Date(),
	    startingDay: 1,
	    showWeeks: false
    };
    
    self.secondDateOptions = {
	    minDate: new Date(),
	    startingDay: 1,
	    showWeeks: false
    };
    
    self.openFirstDatePopup = function() {
        self.firstDatePopup.opened = true;
    };
    
    self.firstDatePopup = {
	    opened: false
    };
    
    self.openSecondDatePopup = function() {
    	self.secondDateOptions.minDate = self.searchSchedule.dates[0] ? self.searchSchedule.dates[0] : new Date();
        self.secondDatePopup.opened = true;
    };
    
    self.secondDatePopup = {
	    opened: false
    };
    
    self.totalPassengers = function() {
    	var passengers = self.searchSchedule.passengers;
    	return passengers.adultCount + passengers.childCount + passengers.infantInLapCount + passengers.infantInSeatCount + passengers.seniorCount;
    };
	
	self.save = function() {
		$http.post('/search/schedule/save', self.searchSchedule).then(
			function(response) {
				self.showMessage = true;
				if (response.data.error) {
					self.error = response.data.error;
				} else {
					self.message = response.data.message;
				}
			}, function(response) {
				self.showMessage = true;
				if (response.data.message) {
					self.error = response.data.message;
				} else {
					self.error = response.status + ' ' + response.statusText;
				}
			}
		);
	}
});
