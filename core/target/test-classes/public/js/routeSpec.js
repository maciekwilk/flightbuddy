describe('route', function() {

    var $locationProvider, $httpProvider, $urlRouterProvider, $stateProvider, $state;

    beforeEach(function () {
        angular.module('mockAppConfig', ['ui.router'])
            .config(function(_$locationProvider_, _$httpProvider_, _$stateProvider_, _$urlRouterProvider_) {
            $locationProvider = _$locationProvider_;
            $httpProvider = _$httpProvider_;
            $urlRouterProvider = _$urlRouterProvider_;
            $stateProvider = _$stateProvider_;
            spyOn($locationProvider, 'html5Mode');
            spyOn($urlRouterProvider, 'otherwise');
        });
        module('mockAppConfig');
        module('app');
        inject();
    });

    beforeEach(inject(function(_$state_, $httpBackend) {
        $state = _$state_;
    }))

    it('should set html5 mode', function() {
        expect($locationProvider.html5Mode)
            .toHaveBeenCalledWith(true);
    });

    it('X-Requested-With Header set to XMLHttpRequest', function() {
        expect($httpProvider.defaults.headers.common["X-Requested-With"])
            .toEqual('XMLHttpRequest');
    });

    it('default route to /page-not-found', function() {
        expect($urlRouterProvider.otherwise)
            .toHaveBeenCalledWith('/page-not-found');
    });

    describe('Login route', function() {
        it('should load the login page on successful load of /login', function() {
            var config = $state.get('login');
            expect(config.url).toBe('/login');
            expect(config.parent).toBe('navigation');
            expect(config.views['content@'].templateUrl).toBe('login.html');
            expect(config.views['content@'].controller).toBe('LoginController');
        });
    });

    describe('Registration route', function() {
        it('should load the registration page on successful load of /register', function() {
            var config = $state.get('register');
            expect(config.url).toBe('/register');
            expect(config.parent).toBe('navigation');
            expect(config.data.roles).toEqual(['ROLE_ADMIN']);
            expect(config.views['content@'].templateUrl).toBe('register.html');
            expect(config.views['content@'].controller).toBe('RegistrationController');
        });
    });

    describe('Home route', function() {
        it('should load the home page on successful load of /', function() {
            var config = $state.get('home');
            expect(config.url).toBe('/');
            expect(config.parent).toBe('navigation');
            expect(config.views['content@'].templateUrl).toBe('home.html');
            expect(config.views['content@'].controller).toBe('HomeController');
        });
    });

    describe('Navigation route', function() {
        it('should load the home page on successful load of ""', function() {
            var config = $state.get('navigation');
            expect(config.url).toBe('');
            expect(config.abstract).toBe(true);
            expect(config.views['navigation@'].templateUrl).toBe('navigation.html');
            expect(config.views['navigation@'].controller).toBe('NavController');
        });
    });

    describe('Schedule route', function() {
        it('should load the home page on successful load of /schedule', function() {
            var config = $state.get('schedule');
            expect(config.url).toBe('/schedule');
            expect(config.parent).toBe('navigation');
            expect(config.data.roles).toEqual(['ROLE_USER', 'ROLE_ADMIN']);
            expect(config.views['content@'].templateUrl).toBe('schedule.html');
            expect(config.views['content@'].controller).toBe('ScheduleController');
        });
    });

    describe('Page-not-found route', function() {
        it('should load the home page on successful load of /page-not-found', function() {
            var config = $state.get('page-not-found');
            expect(config.url).toBe('/page-not-found');
            expect(config.parent).toBe('navigation');
            expect(config.views['content@'].templateUrl).toBe('page-not-found.html');
            expect(config.views['content@'].controller).toBe('PageNotFoundController');
        });
    });

    describe('Access-denied route', function() {
        it('should load the home page on successful load of /access-denied', function() {
            var config = $state.get('access-denied');
            expect(config.url).toBe('/access-denied');
            expect(config.parent).toBe('navigation');
            expect(config.views['content@'].templateUrl).toBe('access-denied.html');
            expect(config.views['content@'].controller).toBe('AccessDeniedController');
        });
    });
});