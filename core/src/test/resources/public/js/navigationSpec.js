describe("navigation", function() {

	beforeEach(module('app'));

    var $controller, authService, scope, $cookies, $httpBackend, $rootScope, $state, $http;

    describe("Given JWT-TOKEN cookie is not set", function() {
        beforeEach(inject(function(_$controller_, _AuthService_, _$rootScope_, _$cookies_) {
            authService = _AuthService_;
            scope = _$rootScope_.$new();
            $cookies = _$cookies_;
            $rootScope = _$rootScope_;
            $controller = _$controller_('NavController', {$scope: scope, $cookies: $cookies});
        }));

        it("user not set", function() {
            expect($controller.user).toBeUndefined();
        });

        describe("Given LoginSuccessful is broadcast", function() {
            it("user should be set", function() {
                var user = "user";
                authService.user = user;
                $rootScope.$broadcast('LoginSuccessful');
                expect($controller.user).toEqual(user);
            });
        })
    })

    describe("Given JWT-TOKEN cookie is set", function() {
        beforeEach(inject(function(_$controller_, _AuthService_, _$rootScope_, _$cookies_, _$httpBackend_, _$state_, _$http_) {
            authService = _AuthService_;
            scope = _$rootScope_.$new();
            $cookies = _$cookies_;
            $httpBackend = _$httpBackend_;
            $rootScope = _$rootScope_;
            $state = _$state_;
            $http = _$http_;
            $cookies.put('JWT-TOKEN', "token");
            $controller = _$controller_('NavController', {$scope: scope, $cookies: $cookies});
        }));

        afterEach(function() {
            $cookies.remove('JWT-TOKEN');
        });

        describe("Given the JWT-TOKEN is invalid", function() {
            it("user not set", function() {
                $httpBackend.expect('POST', '/user/authenticate/token').respond(200, {});
                $httpBackend.expectGET('navigation.html').respond(200);
                $httpBackend.expectGET('home.html').respond(200);
                $httpBackend.flush();
                expect($controller.user).toBeUndefined();
            });
        });

        describe("Given the JWT-TOKEN is valid", function() {
            it("user is set", function() {
                var user = "user object";
                $httpBackend.expect('POST', '/user/authenticate/token').respond(200, {
                    user : user
                });
                $httpBackend.expectGET('navigation.html').respond(200);
                $httpBackend.expectGET('home.html').respond(200);
                $httpBackend.flush();
                expect($controller.user).toEqual(user);
            });
        });

        describe("Given logout method was called", function() {
           it("user and cookie are null", function() {
                authService.user = "user";
                spyOn($state, 'go');
                spyOn($rootScope, '$broadcast').and.returnValue({preventDefault: true});
                $controller.logout();
                expect(authService.user).toEqual(null);
                expect($cookies.get('JWT-TOKEN')).toBeUndefined();
                expect($state.go).toHaveBeenCalledWith('home');
                expect($rootScope.$broadcast).toHaveBeenCalledWith('LogoutSuccessful');
                expect($http.defaults.headers.common['Authorization']).toBe('');
            });
        });

        describe("Given user has no roles", function() {
            it("isAdmin should return false", function() {
                authService.user = {
                    roles : undefined
                };
                expect($controller.isAdmin()).toEqual(false);
            });
        })

        describe("Given user has ROLE_USER", function() {
            it("isAdmin should return false", function() {
                authService.user = {
                    roles : ['ROLE_USER']
                }
                expect($controller.isAdmin()).toEqual(false);
            });
        })

        describe("Given user has ROLE_ADMIN", function() {
            it("isAdmin should return true", function() {
                authService.user = {
                    roles : ['ROLE_ADMIN']
                }
                expect($controller.isAdmin()).toEqual(true);
            });
        })

        describe("Given user has two ROLE_USER", function() {
            it("isAdmin should return false", function() {
                authService.user = {
                    roles : ['ROLE_USER','ROLE_USER']
                }
                expect($controller.isAdmin()).toEqual(false);
            });
        })

        describe("Given user has ROLE_USER and ROLE_ADMIN", function() {
            it("isAdmin should return true", function() {
                authService.user = {
                    roles : ['ROLE_USER','ROLE_ADMIN']
                }
                expect($controller.isAdmin()).toEqual(true);
            });
        })

        describe("Given user is set and LogoutSuccessful is broadcast", function() {
            it("user should be null", function() {
                $controller.user = "user";
                $rootScope.$broadcast('LogoutSuccessful');
                expect($controller.user).toEqual(null);
            });
        })
    });
});