describe("home", function() {

	beforeEach(module('home'));

	var $controller, $httpBackend;
    
	beforeEach(inject(function(_$controller_, _$httpBackend_) {
		$controller = _$controller_('home', {});
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
			it("error variable should have the message and search results undefined", function() {
				var errorMessage = 'error message';
				var searchData = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
				$httpBackend.expect('POST', '/search/perform', searchData).respond(401, {
			    	message : errorMessage
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
				expect($controller.searchResults).toBeUndefined();
			})
		});
		
		describe('when response is successful with error message', function() {
			it("error variable should have the message and search results undefined", function() {
				var errorMessage = 'error message';
				var searchData = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
				$httpBackend.expect('POST', '/search/perform', searchData).respond(200, {
			    	error : errorMessage
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.error).toEqual(errorMessage);
				expect($controller.searchResults).toBeUndefined();
			})
		});
		
		describe('when response is successful with message and no search results', function() {
			it("error variable should have the message and search results array empty", function() {
				var message = 'success message';
				var searchResults = [];
				var searchData = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
				$httpBackend.expect('POST', '/search/perform', searchData).respond(200, {
			    	message : message,
			    	searchResults : searchResults
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.message).toEqual(message);
				expect($controller.searchResults).toEqual(searchResults);
			})
		});
		
		describe('when response is successful with message and search results', function() {
			it("error variable should have the message and search results", function() {
				var message = 'success message';
				var searchResults = [{
			    	price : '230',
					hours : '11:14 - 21:35',
					duration : '3h54m',
					trip : 'BSL-KRK',
					stops : '1'
			    }, {
			    	price : '250',
					hours : '12:14 - 18:35',
					duration : '6h54m',
					trip : 'BSL-KRK',
					stops : '1'
			    }];
				var searchData = {
						from : '',
						to : '',
						price : '',
						dates : [],
						withReturn : false
				};
				$httpBackend.expect('POST', '/search/perform', searchData).respond(200, {
			    	message : message,
			    	searchResults :  searchResults
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.showMessage).toEqual(true);
				expect($controller.message).toEqual(message);
				expect($controller.searchResults).toEqual(searchResults);
			})
		});
	});
});