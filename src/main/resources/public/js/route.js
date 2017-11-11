angular.module('app')
.config( function ($stateProvider, $urlRouterProvider, $httpProvider, $locationProvider) {
	$urlRouterProvider.otherwise('/page-not-found');

	$stateProvider.state('navigation', {
        abstract: true,
        url: '',
        views: {
            'navigation@': {
                templateUrl: 'navigation.html',
                controller: 'NavController'
            }
        }
    }).state('login', {
        parent: 'navigation',
        url: '/login',
        views: {
            'content@': {
                templateUrl: 'login.html',
                controller: 'LoginController'
            }
        }
    }).state('schedule', {
        parent: 'navigation',
        url: '/schedule',
        data: {
            roles: ['ROLE_USER', 'ROLE_ADMIN']
        },
        views: {
            'content@': {
                templateUrl: 'schedule.html',
                controller: 'ScheduleController',
            }
        }
    }).state('home', {
        parent: 'navigation',
        url: '/',
        views: {
            'content@': {
                templateUrl: 'home.html',
                controller: 'HomeController'
            }
        }
    }).state('page-not-found', {
        parent: 'navigation',
        url: '/page-not-found',
        views: {
            'content@': {
                templateUrl: 'page-not-found.html',
                controller: 'PageNotFoundController'
            }
        }
    }).state('access-denied', {
        parent: 'navigation',
        url: '/access-denied',
        views: {
            'content@': {
                templateUrl: 'access-denied.html',
                controller: 'AccessDeniedController'
            }
        }
    }).state('register', {
        parent: 'navigation',
        url: '/register',
        data: {
            roles: ['ROLE_ADMIN']
        },
        views: {
            'content@': {
                templateUrl: 'register.html',
                controller: 'RegistrationController'
            }
        }
    });

    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';	
    $locationProvider.html5Mode(true);
})