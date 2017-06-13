describe("auth", function() {

	beforeEach(module('auth'));
	
	var homePath = "/homePath";
	var logoutPath = "/logoutPath";
	
    var $factory, $httpBackend, $location;
	beforeEach(inject(function(_$httpBackend_, _$location_, auth) {
		$factory = auth;
		$httpBackend = _$httpBackend_;
		$location = _$location_;
	}));

	beforeEach(function() {
		$factory.init(homePath, logoutPath);
	});
	
	afterEach(function() {
	    $httpBackend.verifyNoOutstandingExpectation();
	    $httpBackend.verifyNoOutstandingRequest();
    });

	describe('Given factory was initialized', function() {
		
		it("homePath and logoutPath should be set", function() {
			expect($factory.homePath).toEqual(homePath);
			expect($factory.logoutPath).toEqual(logoutPath);
		})
	
		it("user should be not authenticated", function() {
		    expect($factory.authenticated).toEqual(false);
		});
		
		describe('Given authentication request is successful', function() {
			
			it("credentials are passed in the header", function() {
				var credentials = {
						username : 'username',
						password : 'password'
				};
				var authorizationHeader = "Basic " + btoa(credentials.username + ":" + credentials.password);
				var success;
				$httpBackend.expect('GET', '/user/authenticate', undefined, function(headers) {
				      return headers.authorization === authorizationHeader;
			    }).respond(200, {});
				$factory.authenticate(credentials, function() {
					success = true;
				});
				$httpBackend.flush();
				expect(success).toEqual(true);
			})
			
			describe('Given response has username and authorities', function() {
				it("authenticated variable and callableParam are true, path is homePath and roles is 'roles'", function() {
					var credentials = {};
					var callableParam;
					$httpBackend.expect('GET', '/user/authenticate', undefined, function(headers) {
					      return true;
				    }).respond(200, {
				    	name : 'username',
				    	authorities : 'roles'
				    });
					$factory.authenticate(credentials, function(param) {
				        callableParam = param;
				    });
					$httpBackend.flush();
					expect($factory.authenticated).toEqual(true);
					expect($factory.roles).toEqual('roles');
					expect(callableParam).toEqual(true);
					expect($location.path()).toBe(homePath);
				})
			})
			
			describe('Given response has no username', function() {
				it("authenticated and callableParam are false, roles undefined, path is homePath", function() {
					var credentials = {};
					var callableParam;
					$httpBackend.expect('GET', '/user/authenticate', undefined, function(headers) {
					      return true;
				    }).respond(200, {});
					$factory.authenticate(credentials, function(param) {
				        callableParam = param;
				    });
					$httpBackend.flush();
					expect($factory.authenticated).toEqual(false);
					expect($factory.roles).toBeUndefined();
					expect(callableParam).toEqual(false);
					expect($location.path()).toBe(homePath);
				})
			})
		})
		
		describe('Given authentication request fails', function() {
			it("authenticated and callableParam should be false and roles undefined", function() {
				var credentials = {};
				var callableParam;
				$httpBackend.expect('GET', '/user/authenticate', undefined, function(headers) {
				      return true;
			    }).respond(401, {});
				$factory.authenticate(credentials, function(param) {
			        callableParam = param;
			    });
				$httpBackend.flush();
				expect($factory.authenticated).toEqual(false);
				expect($factory.roles).toBeUndefined();
				expect(callableParam).toEqual(false);
			})
		})
		
		describe('Given clear function was called', function() {
			it("authenticated variable is false, roles undefined and path is set to homePath", function() {		
				$httpBackend.expectPOST(logoutPath, {}).respond(200, {});
				$factory.clear();
				$httpBackend.flush();
				expect($factory.authenticated).toEqual(false);
				expect($factory.roles).toBeUndefined();
				expect($location.path()).toBe(homePath);
			})
		})
	});

});