describe("navigation", function() {

	beforeEach(module('navigation'));

	describe("", function() {

	    var $controller, auth;
	    
		beforeEach(inject(function(_$controller_, _auth_) {
			$controller = _$controller_('navigation', {});
			auth = _auth_;
		}));
		
		describe("Given login function is called", function() {
			it("auth.authenticate should be called with credentials", function() {
				var credentials = {
					username : 'username',
					password : 'password'
				}
				$controller.credentials = credentials;
				spyOn(auth, 'authenticate');
				$controller.login()
				expect(auth.authenticate).toHaveBeenCalled();
				expect(auth.authenticate.calls.argsFor(0)).toContain(credentials);
			});
			
			describe('Given callback param is false', function() {
				it("error variable is true", function() {
					$controller.setErrorIfNotAuthenticated(false);
					expect($controller.error).toEqual(true);
				});
			});
			
			describe('Given callback param is true', function() {
				it("error variable is false", function() {
					$controller.setErrorIfNotAuthenticated(true);
					expect($controller.error).toEqual(false);
				});
			});
		})
		
		describe("Given logout function is called", function() {
			it("auth.clear should be called", function() {
				spyOn(auth, 'clear');
				$controller.logout()
				expect(auth.clear).toHaveBeenCalled();
			});
		})
	});
	
	describe("Given user is authenticated", function() {

	    var $controller;
	    beforeEach(module(function($provide) {
			$provide.value('auth', {
				authenticated : true
			});
		}));
		
		beforeEach(inject(function(_$controller_) {
			$controller = _$controller_('navigation', {});
		}));
		
		it("authenticated variable should be true", function() {
			expect($controller.authenticated()).toEqual(true);
		});
	});
	
	describe("Given user is not authenticated", function() {
		beforeEach(module(function($provide) {
			$provide.value('auth', {
				authenticated : false
			});
		}));
		
	    var $controller, auth;
		beforeEach(inject(function(_$controller_) {
			$controller = _$controller_('navigation', {});
		}));
		
		it("authenticated variable should be false", function() {
			expect($controller.authenticated()).toEqual(false);
		});
	});

});