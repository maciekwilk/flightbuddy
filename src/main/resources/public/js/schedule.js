angular.module('schedule', [])
.controller('schedule', function ($http) {
	
	var self = this;
	
	self.showMessage = false;
	
	self.searchSchedule = {
			from : '',
			to : '',
			price : '',
			dates : [],
			withReturn : false
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
});
