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
		  self.showMessage = true;
		  $http.post('/user/register', self.formData).then(
			function(response) {
				if (response.data.error) {
					self.error = response.data.error;
				} else {
					self.message = response.data.message;
				}
		    }, function(response) {
		    	self.error = response.data.message;
		    });
		  
	  };
});