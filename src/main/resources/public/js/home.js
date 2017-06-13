angular.module('home', [])
.controller('home', function($http, auth) {
    var self = this;
    
    self.authenticated = function() {
        return auth.authenticated;
    }
});