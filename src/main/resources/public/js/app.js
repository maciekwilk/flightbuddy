angular.module('app', ['ngCookies', 'ui.bootstrap', 'ui.router', 'home', 'login', 'registration', 'schedule'])
.run(function(AuthService, $rootScope, $state, $cookies, $http) {

	var token = $cookies.get('JWT-TOKEN');
	if (token) {
		$http.post('/user/authenticate/token', {
   	 		token : token
		}).then(
	        function(response) {
	            if (response.data.user) {
	                $http.defaults.headers.common['Authorization'] = 'Bearer ' + token;
	                AuthService.user = response.data.user;
	            } 
	        }, function (error) {});
	}
	
	$rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
        if (!AuthService.user) {
            if (toState.name != 'login' && toState.name != 'home' && toState.name != 'navigation' && toState.name != 'page-not-found') {
                event.preventDefault();
                $state.go('login');
            }
        } else {
            if (toState.data && toState.data.roles) {
            	var acceptedRoles = toState.data.roles;
            	var userRoles = AuthService.user.roles;
                if (!userContainsAcceptedRole(userRoles, acceptedRoles)) {
	                event.preventDefault();
	                $state.go('access-denied');
                }
            }
        }
	});
	
	var userContainsAcceptedRole = function(userRoles, acceptedRoles) {
		for (var i = 0; i < userRoles.length; i++) {
            var userRole = userRoles[i];
            for (var j = 0; j < acceptedRoles.length; j++) {
            	var acceptedRole = acceptedRoles[j];
                if (acceptedRole == userRole) {
                    return true;
                }
            }
        }
		return false;
	}
	
});