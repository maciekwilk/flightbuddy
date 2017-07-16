angular.module('home', [])
.controller('home', function($http) {
    var self = this;
    
    self.showMessage = false;
    
    self.searchData = {
			from : '',
			to : '',
			price : '',
			dates : [],
			withReturn : false
	};
    
    self.search = function() {
    	$http.post('/search/perform', self.searchData).then(
			function(response) {
				self.showMessage = true;
				if (response.data.error) {
					self.error = response.data.error;
				} else {
					self.message = response.data.message;
					self.foundTrips = response.data.foundTrips;
				}
			}, function(response) {
				self.showMessage = true;
				if (response.data.message) {
					self.error = response.data.message;
				} else {
					self.error = response.status + ' ' + response.statusText;
				}
			}
    )};
    
});