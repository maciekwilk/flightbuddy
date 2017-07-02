angular.module('schedule', [])
.controller('schedule', function ($http) {
	
	var self = this;
	
	self.showMessage = false;
	
	self.searchSchedule = {
			from : '',
			to : '',
			price : '',
			dates : [],
			withReturn : ''
	};
	
	self.save = function() {
		self.showMessage = true;
		$http.post('/search/schedule/save', self.searchSchedule).then(
			function(response) {
				if (response.data.error) {
					self.error = response.data.error;
				} else {
					self.message = response.data.message;
				}
			}, function(response) {
				self.error = response.data.message;
			}
		);
	}
});
