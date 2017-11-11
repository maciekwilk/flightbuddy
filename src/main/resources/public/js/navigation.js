angular.module('app')
.controller('NavController', function ($http, $scope, AuthService, $state, $rootScope) {
    var self = this;
    
	$scope.$on('LoginSuccessful', function () {
        $scope.user = AuthService.user;
    });
    $scope.$on('LogoutSuccessful', function () {
        $scope.user = null;
    });
    self.logout = function () {
        AuthService.user = null;
        $rootScope.$broadcast('LogoutSuccessful');
        $state.go('home');
    };
    
    self.isAdmin = function() {
    	var user = AuthService.user;
    	var roles = user.roles;
		if (roles) {
		    for (var i = 0; i < roles.length; i++) {
				if (roles[i] === 'ROLE_ADMIN')
					return true;
			  	}
		  	}
		return false;
    }
});