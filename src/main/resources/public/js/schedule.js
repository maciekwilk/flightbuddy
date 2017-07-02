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
});
