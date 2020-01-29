describe("auth", function() {

	beforeEach(module('app'));
	
    var $service;
	beforeEach(inject(function(_$httpBackend_, _$location_, AuthService) {
		$service = AuthService;
	}));

	describe('Given service was never used', function() {
		
		it("user is null", function() {
			expect($service.user).toEqual(null);
		})
	});

});