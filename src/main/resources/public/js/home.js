angular.module('home', [])
.controller('home', function($http, auth) {
    var self = this;
    $http.get('/user/authenticate').then(function(response) {
        self.user = response.data.username;
    });
    
    self.authenticated = function() {
        return auth.authenticated;
    }
});