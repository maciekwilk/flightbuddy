angular.module('auth', [])
.factory('auth', function ($http, $location) {

	var auth = {
		authenticated : false,
		
		loginPath : '/login',
        logoutPath : '/logout',
        homePath : '/home',

        authenticate : function(credentials, callback) {
	
		    var headers = credentials && credentials.username ? {
		    	authorization : "Basic " + btoa(credentials.username + ":" + credentials.password)
		    } : {};
		
		    $http.get('user', {headers : headers}).then(
    		function(response) {
		    	if (response.data.name) {
		    		auth.authenticated = true;
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
        	  $location.path(auth.loginPath);
        	  $http.post(auth.logoutPath, {});
    	},

        init : function(homePath, loginPath, logoutPath) {
        	  auth.homePath = homePath;
        	  auth.loginPath = loginPath;
        	  auth.logoutPath = logoutPath;
    	},
	};
	
	return auth;
});