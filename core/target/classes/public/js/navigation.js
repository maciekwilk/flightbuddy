angular.module('app')
.controller('NavController', function ($http, AuthService, $state, $scope, $rootScope, $cookies) {
    var self = this;
    
    var token = $cookies.get('JWT-TOKEN');
	if (token) {
		$http.post('/user/authenticate/token', {
   	 		token : token
		}).then(
	        function(response) {
	            if (response.data.user) {
	                self.user = response.data.user;
	            } 
	        }, function (error) {});
	}
    
	$scope.$on('LoginSuccessful', function () {
        self.user = AuthService.user;
    });
    $scope.$on('LogoutSuccessful', function () {
        self.user = null;
    });
    self.logout = function () {
        AuthService.user = null;
        $cookies.remove('JWT-TOKEN');
        $http.defaults.headers.common['Authorization'] = '';
        $rootScope.$broadcast('LogoutSuccessful');
        $state.go('home');
    };
    
    self.isAdmin = function() {
    	var user = AuthService.user;
    	if (user) {
            var roles = user.roles;
            if (roles) {
                for (var i = 0; i < roles.length; i++) {
                    if (roles[i] === 'ROLE_ADMIN')
                        return true;
                    }
                }
        }
		return false;
    }
});