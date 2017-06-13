angular.module('navigation', ['auth'])
.controller('navigation', function (auth) {

	  var self = this;
	  
	  self.credentials = {};
	  
	  self.authenticated = function() {
          return auth.authenticated;
      }
	  
	  self.isAdmin = function() {
		  var roles = auth.roles;
		  for (var i = 0; i < roles.length; i++) {
			  if (roles[i].authority === 'ROLE_ADMIN')
				  return true;
		  }
		  return false;
	  }
	  
	  self.login = function() {
	      auth.authenticate(self.credentials, function(authenticated) {
	    	  self.setErrorIfNotAuthenticated(authenticated);
	      });
	  };
	  
	  self.logout = function() {
		  auth.clear();
	  };
	  
	  self.setErrorIfNotAuthenticated = function(authenticated) {
		  if (authenticated) {
			  self.error = false;
		  } else {
			  self.error = true;
		  } 
	  };
});