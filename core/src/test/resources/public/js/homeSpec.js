describe("home", function() {

	beforeEach(module('home'));

	var $controller, $httpBackend;
    
	beforeEach(inject(function(_$controller_, _$httpBackend_) {
		$controller = _$controller_('HomeController', {});
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

	it("message variable should be undefined", function() {
		$httpBackend.expect('GET', '/airport/all').respond(200, {});
		$httpBackend.flush();
		expect($controller.message).toBeUndefined();
	})
	
	describe('Given save function was called', function() {
		
		var searchData = {
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
			it("error variable should have the message and search results undefined", function() {
				var errorMessage = 'error message';
				$httpBackend.expect('POST', '/search/perform', searchData).respond(401, {
			    	message : errorMessage
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.message).toBeUndefined();
				expect($controller.error).toEqual(errorMessage);
				expect($controller.searchResults).toBeUndefined();
			})
		});
		
		describe('when response fails without error message', function() {
			it("error variable should have the message and search results undefined", function() {
				var errorMessage = '401 ';
				$httpBackend.expect('POST', '/search/perform', searchData).respond(401, {});
				$controller.search();
				$httpBackend.flush();
				expect($controller.message).toBeUndefined();
				expect($controller.error).toEqual(errorMessage);
				expect($controller.searchResults).toBeUndefined();
			})
		});
		
		describe('when response is successful with error message', function() {
			it("error variable should have the message and search results undefined", function() {
				var errorMessage = 'error message';
				$httpBackend.expect('POST', '/search/perform', searchData).respond(200, {
			    	error : errorMessage
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.message).toBeUndefined();
				expect($controller.error).toEqual(errorMessage);
				expect($controller.searchResults).toBeUndefined();
			})
		});
		
		describe('when response is successful with message and no search results', function() {
			it("message variable should have the message and search results array empty", function() {
				var message = 'success message';
				var searchResults = [];
				$httpBackend.expect('POST', '/search/perform', searchData).respond(200, {
			    	message : message,
			    	searchResults : searchResults
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.error).toBeUndefined();
				expect($controller.message).toEqual(message);
				expect($controller.searchResults).toEqual(searchResults);
			})
		});
		
		describe('when response is successful with message and search results', function() {
			it("message variable should have the message and search results", function() {
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
				$httpBackend.expect('POST', '/search/perform', searchData).respond(200, {
			    	message : message,
			    	searchResults :  searchResults
			    });
				$controller.search();
				$httpBackend.flush();
				expect($controller.error).toBeUndefined();
				expect($controller.message).toEqual(message);
				expect($controller.searchResults).toEqual(searchResults);
			})
		});
	});
	
	describe('Given selectTableRow function was called', function() {
		
		beforeEach(function() {
			$httpBackend.expect('GET', '/airport/all').respond(200, {
				data : ''
		    });
			$httpBackend.flush();
			$controller.searchResults = ['first', 'second', 'third'];
		});
		
		it("searchResultRowCollapse is initalized properly", function() {
			var searchResultRowCollapse = [false, false, false]
			$controller.tableRowIndexExpandedCurr = '1';
			$controller.selectTableRow();
			expect($controller.searchResultRowCollapse).toEqual(searchResultRowCollapse);
		})
		
		it("expanding a row is working", function() {
			var searchResultRowCollapse = [false, true, false];
			var rowId = 'rowId';
			var index = 1;
			
			$controller.selectTableRow(index, rowId);
			
			expect($controller.searchResultRowCollapse).toEqual(searchResultRowCollapse);
			expect($controller.tableRowIndexExpandedPrev).toEqual("");
            expect($controller.tableRowExpanded).toEqual(true);
            expect($controller.tableRowIndexExpandedCurr).toEqual(index);
            expect($controller.searchResultIdExpanded).toEqual(rowId);
		})
		
		it("collapsing already expanded is working", function() {
			$controller.searchResultRowCollapse = [false, true, false];
			var searchResultRowCollapse = [false, false, false];
			var rowId = 'rowId';
			var index = 1;
			$controller.tableRowIndexExpandedCurr = index;
			$controller.searchResultIdExpanded = rowId;
			$controller.tableRowExpanded = true;
			
			$controller.selectTableRow(index, rowId);
			
			expect($controller.searchResultRowCollapse).toEqual(searchResultRowCollapse);
            expect($controller.tableRowExpanded).toEqual(false);
            expect($controller.tableRowIndexExpandedCurr).toEqual("");
            expect($controller.searchResultIdExpanded).toEqual("");
		})
		
		it("collapsing already expanded row and expanding a new one is working", function() {
			$controller.searchResultRowCollapse = [false, true, false];
			var searchResultRowCollapse = [false, false, true];
			var rowId = 'rowId';
			var index = 2;
			var prevIndex = 1;
			$controller.tableRowIndexExpandedCurr = prevIndex;
			$controller.searchResultIdExpanded = 'rowId2';
			$controller.tableRowExpanded = true;
			
			$controller.selectTableRow(index, rowId);
			
			expect($controller.searchResultRowCollapse).toEqual(searchResultRowCollapse);
			expect($controller.tableRowIndexExpandedPrev).toEqual(prevIndex);
            expect($controller.tableRowExpanded).toEqual(true);
            expect($controller.tableRowIndexExpandedCurr).toEqual(index);
            expect($controller.searchResultIdExpanded).toEqual(rowId);
		})
	});
});