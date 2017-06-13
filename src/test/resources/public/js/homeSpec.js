describe("home", function() {

	beforeEach(module('home'));

	describe("Given user is authenticated", function() {
		beforeEach(module(function($provide) {
			$provide.value('auth', {
				authenticated : true
			});
		}));
		
	    var $controller;
		beforeEach(inject(function(_$controller_) {
			$controller = _$controller_('home', {});
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
		
	    var $controller;
		beforeEach(inject(function(_$controller_) {
			$controller = _$controller_('home', {});
		}));
		
		it("authenticated variable should be false", function() {
			expect($controller.authenticated()).toEqual(false);
		});
	});
});