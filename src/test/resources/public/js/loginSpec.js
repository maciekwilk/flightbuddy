describe("login", function() {

	beforeEach(function() {
	    module('login');
	    module('app');
	});

	var $controller, $httpBackend, $state, $http, $rootScope, AuthService;

    beforeEach(inject(function(_$controller_, _$httpBackend_, _AuthService_, _$state_, _$http_, _$rootScope_) {
        $httpBackend = _$httpBackend_;
        AuthService = _AuthService_;
        $controller = _$controller_('LoginController', {AuthService});
        $state = _$state_;
        $http = _$http_;
        $rootScope = _$rootScope_;
    }));

	afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it("user in AuthService should be null", function() {
        $httpBackend.expectGET('navigation.html').respond(200);
        $httpBackend.expectGET('home.html').respond(200);
        $httpBackend.flush();
        expect(AuthService.user).toEqual(null);
    })

    describe('Given login function was called', function() {

        beforeEach(function() {
            spyOn($state, 'go');
            spyOn($rootScope, '$broadcast').and.returnValue({preventDefault: true});
        });

        describe('when response fails', function() {
            it("error variable should be true and user is null", function() {
                $httpBackend.expect('POST', '/user/authenticate').respond(401, {});
                $controller.login();
                $httpBackend.flush();
                expect($state.go.calls.any()).toEqual(false);
                expect($controller.error).toEqual(true);
                expect(AuthService.user).toEqual(null);
                expect($http.defaults.headers.common['Authorization']).toBeUndefined();
            })
        });

        describe('when response is successful without token', function() {
            it("error variable should true and user should be null", function() {
                $httpBackend.expect('POST', '/user/authenticate').respond(200, {});
                $controller.login();
                $httpBackend.flush();
                expect($state.go.calls.any()).toEqual(false);
                expect($controller.error).toEqual(true);
                expect(AuthService.user).toEqual(null);
                expect($http.defaults.headers.common['Authorization']).toBeUndefined();
            })
        });

        describe('when response is successful with token', function() {
            it("error variable should be false and user should be set", function() {
                var user = 'user';
                var token = 'sampleToken'
                $httpBackend.expect('POST', '/user/authenticate').respond(200, {
                    token : token,
                    user : user
                });
                $controller.login();
                $httpBackend.flush();
                expect($state.go).toHaveBeenCalledWith('schedule');
                expect($rootScope.$broadcast).toHaveBeenCalledWith('LoginSuccessful');
                expect($controller.error).toBeUndefined();
                expect(AuthService.user).toEqual(user);
                expect($http.defaults.headers.common['Authorization']).toBe('Bearer ' + token);
            })
        });
    });

});