describe("registration", function() {

	beforeEach(module('registration'));

	var $controller, $httpBackend;
    
	beforeEach(inject(function(_$controller_, _$httpBackend_) {
		$controller = _$controller_('registration', {});
		$httpBackend = _$httpBackend_;
	}));
	
	afterEach(function() {
	    $httpBackend.verifyNoOutstandingExpectation();
	    $httpBackend.verifyNoOutstandingRequest();
    });
	
	it("showMessage variable should be false", function() {
		expect($controller.showMessage).toEqual(false);
	})
	
	describe('Given register function was called', function() {
		
		describe('when response is successful with error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = 'error message';
				var formData = {
					  username : '',
			  		  password : '',
			  		  repeatedPassword : ''
				};
				$httpBackend.expect('POST', '/user/register', formData).respond(200, {
			    	error : errorMessage
			    });
				$controller.register();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
			})
		});
		
		describe('when response is successful with no error message', function() {
			it("error variable should have the message", function() {
				var message = 'success message';
				var formData = {
					  username : '',
			  		  password : '',
			  		  repeatedPassword : ''
				};
				$httpBackend.expect('POST', '/user/register', formData).respond(200, {
			    	message : message
			    });
				$controller.register();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.message).toEqual(message);
			})
		});
		
		describe('when response fails with error message', function() {
			it("error variable should have the message", function() {
				var errorMessage = 'error message';
				var formData = {
					  username : '',
			  		  password : '',
			  		  repeatedPassword : ''
				};
				$httpBackend.expect('POST', '/user/register', formData).respond(401, {
			    	message : errorMessage
			    });
				$controller.register();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
			})
		});
	});
});