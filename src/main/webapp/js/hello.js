var app = angular.module('app', ['ngRoute', 'ui.bootstrap']);

app.config( function($routeProvider, $httpProvider, $locationProvider, $qProvider) {
    $routeProvider
    	.when('/home', {
            templateUrl : 'home.html',
            controller  : 'homeController',
            controllerAs : 'home'
        })
        .when('/login', {
            templateUrl : 'login.html',
            controller  : 'navigation',
            controllerAs : 'navigation'
        })
        .otherwise({
    		redirectTo : '/home'
        });

    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';	
    $locationProvider.html5Mode(true);
});
    
app.controller('navigation', 
	['$rootScope', '$http', '$location', function ($rootScope, $http, $location) {
	  
	  $rootScope.navClass = function (page) {
		  var currentRoute = $location.path().substring(1) || 'home';
		  return page === currentRoute ? 'active' : '';
	  };
	  
	  $rootScope.loadHome = function () {
		  $location.url('/home');
	  };
	    
      $rootScope.loadLogin = function () {
    	  $location.url('/login');
      };
      
      var self = this;
  	
	  var authenticate = function(credentials, callback) {
	
	    var headers = credentials ? {authorization : "Basic "
	        + btoa(credentials.username + ":" + credentials.password)
	    } : {};
	
	    $http.get('user', {headers : headers}).then(function(response) {
	      if (response.data.name) {
	        $rootScope.authenticated = true;
	      } else {
	        $rootScope.authenticated = false;
	      }
	      callback && callback();
	    }, function() {
	      $rootScope.authenticated = false;
	      callback && callback();
	    });
	
	  }
	
	  authenticate();
	  self.credentials = {};
	  self.login = function() {
	      authenticate(self.credentials, function() {
	        if ($rootScope.authenticated) {
	          $location.path("/");
	          self.error = false;
	        } else {
	          $location.path("/login");
	          self.error = true;
	        }
	      });
	  };
	  self.logout = function() {
		  $http.post('logout', {}).finally(function() {
		    $rootScope.authenticated = false;
		    $location.path("/");
		  });
	  };
      
}]);

app.controller('homeController', function($http) {
    this.message = 'Everyone come and see how good I look!';
    var self = this;
    $http.get('/resource/').then(function(response) {
      self.greeting = response.data;
    })
});