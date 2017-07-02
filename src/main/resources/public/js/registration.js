angular.module('registration', [])
.controller('registration', function ($http) {

	  var self = this;
	  
	  self.showMessage = false;
	  
	  self.formData = {
			  username : '',
	  		  password : '',
	  		  repeatedPassword : ''
	  };

	  self.register = function() {	 
		  $http.post('/user/register', self.formData).then(
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
		    });
		  
	  };
});