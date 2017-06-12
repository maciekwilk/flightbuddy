describe("home", function() {

	beforeEach(module('home'));

	describe("Given user is authenticated", function() {
		beforeEach(module(function($provide) {
			$provide.value('auth', {
				authenticated : true
			});
		}));
		
	    var $httpBackend, $controller;
		beforeEach(inject(function(_$httpBackend_, _$controller_) {
			$httpBackend = _$httpBackend_;
			$controller = _$controller_('home', {});
		}));
		
		afterEach(function() {
		    $httpBackend.verifyNoOutstandingExpectation();
		    $httpBackend.verifyNoOutstandingRequest();
	    });
		
		it("authenticated variable should be true", function() {
			$httpBackend.expectGET('/user/authenticate').respond(200, {
			      username : 'username'
		    });
			$httpBackend.flush();
			expect($controller.authenticated()).toEqual(true);
		});
	});
	
	describe("Given user is not authenticated", function() {
		beforeEach(module(function($provide) {
			$provide.value('auth', {
				authenticated : false
			});
		}));
		
	    var $httpBackend, $controller;
		beforeEach(inject(function(_$httpBackend_, _$controller_) {
			$httpBackend = _$httpBackend_;
			$controller = _$controller_('home', {});
		}));
		
		afterEach(function() {
		    $httpBackend.verifyNoOutstandingExpectation();
		    $httpBackend.verifyNoOutstandingRequest();
	    });
		
		it("authenticated variable should be false", function() {
			$httpBackend.expectGET('/user/authenticate').respond(200, {
			      username : 'username'
		    });
			$httpBackend.flush();
			expect($controller.authenticated()).toEqual(false);
		});
		

		describe("Given user authentication returns username", function() {
			it("user variable should be equal to username", function() {
			    $httpBackend.expectGET('/user/authenticate').respond(200, {
			      username : 'username'
			    });
				$httpBackend.flush();
			    expect($controller.user).toEqual('username');
			});
		});
		
		describe("Given user authentication returns empty object", function() {
			it("user variable should be undefined", function() {
			    $httpBackend.expectGET('/user/authenticate').respond(200, {});
				$httpBackend.flush();
			    expect($controller.user).not.toBeDefined();
			});
		});
	});
});