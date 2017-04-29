angular.module('navigation', ['auth'])
.controller('navigation', function ($rootScope, $location, auth) {

	  var self = this;
	  
	  self.credentials = {};
	  
	  self.authenticated = function() {
          return auth.authenticated;
      }
	  
	  self.login = function() {
	      auth.authenticate(self.credentials, function(authenticated) {
	        if (authenticated) {
	        	self.error = false;
	        } else {
	        	self.error = true;
	        }
	      });
	  };
	  
	  self.logout = function() {
		  auth.clear();
	  };
});