describe('app', function() {
    beforeEach(module('app'));

    var $location, $route, $rootScope;

    beforeEach(inject(
        function( _$location_, _$route_, _$rootScope_) {
            $location = _$location_;
            $route = _$route_;
            $rootScope = _$rootScope_;
    }));

    describe('Login route', function() {
        beforeEach(inject(
            function($httpBackend) {
                $httpBackend.expectGET('login')
                .respond(200);
            }));

        it('should load the login page on successful load of /login', function() {
            $location.path('/login');
            $rootScope.$digest();
            expect($route.current.controller).toBe('navigation')
            expect($route.current.templateUrl).toEqual('login.html');
        });
    });
    
    describe('Registration route', function() {
        beforeEach(inject(
            function($httpBackend) {
                $httpBackend.expectGET('register')
                .respond(200);
            }));

        it('should load the registration page on successful load of /register', function() {
            $location.path('/register');
            $rootScope.$digest();
            expect($route.current.controller).toBe('registration')
            expect($route.current.templateUrl).toEqual('register.html');
        });
    });    
    
    describe('Home route', function() {
        beforeEach(inject(
            function($httpBackend) {
                $httpBackend.expectGET('home')
                .respond(200);
            }));

        it('should load the home page on successful load of /home or others', function() {
            $location.path('/home');
            $rootScope.$digest();
            expect($route.current.controller).toBe('home')
            expect($route.current.templateUrl).toEqual('home.html');
            expect($route.routes[null].redirectTo).toEqual('/home');
        });
    });
});