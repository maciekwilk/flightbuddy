describe("registration", function() {

	beforeEach(module('registration'));

	var $controller, $httpBackend;
    
	beforeEach(inject(function(_$controller_, _$httpBackend_) {
		$controller = _$controller_('RegistrationController', {});
		$httpBackend = _$httpBackend_;
	}));
	
	afterEach(function() {
	    $httpBackend.verifyNoOutstandingExpectation();
	    $httpBackend.verifyNoOutstandingRequest();
    });
	
	it("message variable should be undefined", function() {
		expect($controller.message).toBeUndefined();
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
				expect($controller.message).toBeUndefined();
				expect($controller.error).toEqual(errorMessage);
			})
		});
		
		describe('when response is successful with no error message', function() {
			it("message variable should have the message", function() {
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
				expect($controller.error).toBeUndefined();
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
				expect($controller.message).toBeUndefined();
				expect($controller.error).toEqual(errorMessage);
			})
		});
	});
});