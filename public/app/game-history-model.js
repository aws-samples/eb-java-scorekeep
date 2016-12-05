var module = angular.module('scorekeep');
module.service('GameHistoryModel', function($http, GameHistoryService, api) {
    var model = {};

    model.get = function() {
        return GameHistoryService.query().$promise;
    };

    model.create = function() {
        var service = new GameHistoryService();
        return service.$save();
    };
    return model;
});