angular.module('login', ['ui.router'])
 .controller('LoginController', function ($http, $state, $rootScope, AuthService) {
	 var self = this;
     self.login = function () {
         $http.post('/user/authenticate', {
        	 username: self.username,
             password: self.password
         }).then(
         function(response) {
             self.password = null;
             if (response.data.token) {
                 $http.defaults.headers.common['Authorization'] = 'Bearer ' + response.data.token;
                 AuthService.user = response.data.user;
                 $rootScope.$broadcast('LoginSuccessful');
                 $state.go('home');
             } else {
                 self.error = true;
             }
         }, function (error) {
             self.error = true;
         });
     };
});