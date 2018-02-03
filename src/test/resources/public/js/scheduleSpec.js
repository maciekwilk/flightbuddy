describe("schedule", function() {

	beforeEach(module('schedule'));

	var $controller, $httpBackend;
    
	beforeEach(inject(function(_$controller_, _$httpBackend_) {
		$controller = _$controller_('ScheduleController', {});
		$httpBackend = _$httpBackend_;
	}));
	
	afterEach(function() {
	    $httpBackend.verifyNoOutstandingExpectation();
	    $httpBackend.verifyNoOutstandingRequest();
    });
	
	it("airports should be initialized", function() {
		var airports = '{some airports}';
		$httpBackend.expect('GET', '/airport/all').respond(200, {
			data : airports
	    });
		$httpBackend.flush();
		expect($controller.airports.data).toEqual(airports);
	})
	
	it("showMessage variable should be false", function() {
		$httpBackend.expect('GET', '/airport/all').respond(200, {});
		$httpBackend.flush();
		expect($controller.showMessage).toEqual(false);
	})
	
	describe('Given save function was called', function() {
		
		var searchSchedule = {
				from : '',
				to : '',
				minPrice : 0,
				maxPrice : 400,
				dates : [],
				withReturn : false,
				passengers : {
					adultCount : 1,
					childCount : 0,
					infantInLapCount : 0,
					infantInSeatCount : 0,
					seniorCount : 0
				}
		};
		
		beforeEach(function() {
			$httpBackend.expect('GET', '/airport/all').respond(200, {
				data : ''
		    });
		})
		
		describe('when response fails with error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = 'error message';
				$httpBackend.expect('POST', '/search/schedule/save', searchSchedule).respond(401, {
			    	message : errorMessage
			    });
				$controller.save();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
			})
		});
		
		describe('when response fails without error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = '401 ';
				$httpBackend.expect('POST', '/search/schedule/save', searchSchedule).respond(401, {});
				$controller.save();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
			})
		});
		
		describe('when response is successful with error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = 'error message';
				$httpBackend.expect('POST', '/search/schedule/save', searchSchedule).respond(200, {
			    	error : errorMessage
			    });
				$controller.save();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
			})
		});
		
		describe('when response is successful with no error message', function() {
			it("error variable should have the message", function() {
				var message = 'success message';
				$httpBackend.expect('POST', '/search/schedule/save', searchSchedule).respond(200, {
			    	message : message
			    });
				$controller.save();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.message).toEqual(message);
			})
		});
	});
});