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
		  var formData = self.formData;
		  self.showMessage = true;
		  if (!formData.username) {
			  self.message = 'Username not valid!';
			  return;
		  }
		  if (!formData.password) {
			  self.message = "Password not valid!";
			  return;
		  }
		  if (formData.password != formData.repeatedPassword) {
			  self.message = "Repeated password needs to have the same value as password!"
			  return;
		  }
		  $http.post('/user/register', self.formData).then(
			function(response) {
		    	self.message = response.data.message;
		    }, function(response) {
		    	self.message = response.message;
		    });
	  };
});