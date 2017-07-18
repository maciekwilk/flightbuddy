describe("schedule", function() {

	beforeEach(module('schedule'));

	var $controller, $httpBackend;
    
	beforeEach(inject(function(_$controller_, _$httpBackend_) {
		$controller = _$controller_('schedule', {});
		$httpBackend = _$httpBackend_;
	}));
	
	afterEach(function() {
	    $httpBackend.verifyNoOutstandingExpectation();
	    $httpBackend.verifyNoOutstandingRequest();
    });
	
	it("showMessage variable should be false", function() {
		expect($controller.showMessage).toEqual(false);
	})
	
	describe('Given save function was called', function() {
		
		describe('when response fails with error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = 'error message';
				var searchSchedule = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
				$httpBackend.expect('POST', '/search/schedule/save', searchSchedule).respond(401, {
			    	message : errorMessage
			    });
				$controller.save();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
			})
		});
		
		describe('when response is successful with error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = 'error message';
				var searchSchedule = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
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
				var searchSchedule = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
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