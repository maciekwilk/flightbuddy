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
});
