describe('app', function() {

    var AuthService, scope, $cookies, $httpBackend, $rootScope, $state, $http;

    describe("Given JWT-TOKEN cookie is not set", function() {

            beforeEach(function () {
                module('app', function ($provide) {
                    $provide.value('AuthService', {
                        user: jasmine.createSpy()
                    });

                    $provide.value('$cookies', {
                        get: function() {
                            return 'token';
                        }
                    });
                });

                inject(function (_AuthService_, _$cookies_, _$http_, _$rootScope_, _$state_) {
                    AuthService = _AuthService_;
                    $cookies = _$cookies_;
                    $http = _$http_;
                    $rootScope = _$rootScope_;
                    $state = _$state_;
                });

                AuthService.user = null;
            });

        it("user not set", function() {
            expect(AuthService.user).toEqual(null);
            expect($http.defaults.headers.common['Authorization']).toBeUndefined();
        });

        describe("Given stateChangeStart is broadcast without user", function() {
            describe("with toState schedule", function() {
                it("state should go to login", function() {
                    spyOn($state, 'go');
                    $rootScope.$broadcast('$stateChangeStart', {name : 'schedule'}, null, null, null);
                    expect($state.go).toHaveBeenCalledWith('login');
                });
            })

            describe("with toState login", function() {
                it("nothing should happen", function() {
                    spyOn($state, 'go');
                    $rootScope.$broadcast('$stateChangeStart', {name : 'login'}, null, null, null);
                    expect($state.go).not.toHaveBeenCalledWith('login');
                });
            })

            describe("with toState home", function() {
                it("nothing should happen", function() {
                    spyOn($state, 'go');
                    $rootScope.$broadcast('$stateChangeStart', {name : 'home'}, null, null, null);
                    expect($state.go).not.toHaveBeenCalledWith('login');
                });
            })

            describe("with toState navigation", function() {
                it("nothing should happen", function() {
                    spyOn($state, 'go');
                    $rootScope.$broadcast('$stateChangeStart', {name : 'navigation'}, null, null, null);
                    expect($state.go).not.toHaveBeenCalledWith('login');
                });
            })
        })

        describe("Given stateChangeStart is broadcast with user", function() {

            describe("without roles", function() {
                it("nothing should happen", function() {
                    spyOn($state, 'go');
                    $rootScope.$broadcast('$stateChangeStart', {data : {roles : null}}, null, null, null);
                    expect($state.go).not.toHaveBeenCalledWith('access-denied');
                });
            })

            describe("with roles", function() {
                describe("that are requested", function() {
                    it("nothing should happen", function() {
                        spyOn($state, 'go');
                        AuthService.user = {roles : ['user']};
                        $rootScope.$broadcast('$stateChangeStart', {data : {roles : ['user']}}, null, null, null);
                        expect($state.go).not.toHaveBeenCalledWith('access-denied');
                    });

                    it("nothing should happen", function() {
                        spyOn($state, 'go');
                        AuthService.user = {roles : ['user', 'admin']};
                        $rootScope.$broadcast('$stateChangeStart', {data : {roles : ['user']}}, null, null, null);
                        expect($state.go).not.toHaveBeenCalledWith('access-denied');
                    });
                })

                describe("that are not requested", function() {
                    it("state should go to access-denied", function() {
                        spyOn($state, 'go');
                        AuthService.user = {roles : ['user', 'actuator']};
                        $rootScope.$broadcast('$stateChangeStart', {data : {roles : ['admin']}}, null, null, null);
                        expect($state.go).toHaveBeenCalledWith('access-denied');
                    });

                    it("state should go to access-denied", function() {
                        spyOn($state, 'go');
                        AuthService.user = {roles : []};
                        $rootScope.$broadcast('$stateChangeStart', {data : {roles : ['admin']}}, null, null, null);
                        expect($state.go).toHaveBeenCalledWith('access-denied');
                    });
                })
            })

        })
    })

    describe("Given JWT-TOKEN cookie is set", function() {

        beforeEach(function () {
            module('app', function ($provide) {
                $provide.value('AuthService', {
                    user: jasmine.createSpy()
                });

                $provide.value('$cookies', {
                    get: function() {
                        return 'token';
                    }
                });
            });

            inject(function (_AuthService_, _$cookies_, _$http_, _$httpBackend_) {
                AuthService = _AuthService_;
                $cookies = _$cookies_;
                $httpBackend = _$httpBackend_;
                $http = _$http_;
            });

            AuthService.user = null;
        });

        describe("Given the JWT-TOKEN is invalid", function() {
            it("user not set", function() {
                $httpBackend.expect('POST', '/user/authenticate/token').respond(200, {});
                $httpBackend.expectGET('navigation.html').respond(200);
                $httpBackend.expectGET('home.html').respond(200);
                $httpBackend.flush();
                expect(AuthService.user).toEqual(null);
                expect($http.defaults.headers.common['Authorization']).toBeUndefined();
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
                expect(AuthService.user).toEqual(user);
                expect($http.defaults.headers.common['Authorization']).toBe('Bearer token');
            });
        });
    });
});