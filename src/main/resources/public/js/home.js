angular.module('home', [])
.controller('home', function($http, auth) {
    var self = this;
    
    self.foundTrips = [{
    	price : '230',
		hours : '11:14 - 21:35',
		duration : '3h54m',
		trip : 'BSL-KRK',
		stops : '1'
    }];
    
});