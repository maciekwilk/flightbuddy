angular.module('app', ['ngRoute', 'ui.bootstrap', 'auth', 'home', 'navigation'])
.config( function($routeProvider, $httpProvider, $locationProvider) {
    $routeProvider
    	.when('/home', {
            templateUrl : 'home.html',
            controller  : 'home',
            controllerAs : 'home'
        })
        .when('/login', {
            templateUrl : 'login.html',
            controller  : 'navigation',
            controllerAs : 'nav'
        })
        .otherwise({
    		redirectTo : '/home'
        });

    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';	
    $locationProvider.html5Mode(true);
}).run(function(auth) {
	auth.init('/home', '/login', '/logout');
});