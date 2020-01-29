angular.module('registration', [])
.controller('RegistrationController', function ($http) {

	  var self = this;
	  
	  self.formData = {
			  username : '',
	  		  password : '',
	  		  repeatedPassword : ''
	  };

	  self.register = function() {	 
		  $http.post('/user/register', self.formData).then(
			function(response) {
				if (response.data.error) {
					self.error = response.data.error;
				} else {
					self.message = response.data.message;
				}
		    }, function(response) {
				if (response.data.message) {
					self.error = response.data.message;
				} else {
					self.error = response.status + ' ' + response.statusText;
				}
		    });
		  
	  };
});