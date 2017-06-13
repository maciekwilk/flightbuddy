angular.module('auth', [])
.factory('auth', function ($http, $location) {

	var auth = {
		authenticated : false,

        authenticate : function(credentials, callback) {
	
		    var headers = credentials && credentials.username ? {
		    	authorization : "Basic " + btoa(credentials.username + ":" + credentials.password)
		    } : {};
		
		    $http.get('/user/authenticate', {headers : headers}).then(
    		function(response) {
		    	if (response.data.name) {
		    		auth.authenticated = true;
		    		auth.roles = response.data.authorities;
		    	} else {
		    		auth.authenticated = false;
		    	}
		    	$location.path(auth.homePath);
		    	callback && callback(auth.authenticated);
		    }, function() {
		    	auth.authenticated = false;
	            callback && callback(false);
		    });
	
        },
        
        clear : function() {
        	  auth.authenticated = false;
        	  auth.roles = undefined;
        	  $location.path(auth.homePath);
        	  $http.post(auth.logoutPath, {});
    	},

        init : function(homePath, logoutPath) {
        	  auth.homePath = homePath;
        	  auth.logoutPath = logoutPath;
    	}
	};
	
	return auth;
});