var module = angular.module('scorekeep');
module.service('GameCollection', function($http, GameService, api) {
  var collection = {};

  collection.createGame = function(sessionid, gamename, gamerules) {
    var game = new GameService();
    return game.$save( { sessionid: sessionid })
    .then( function() {
      game.session = sessionid;
      game.name = gamename;
      game.rules = gamerules;
      return game.$update({ sessionid: sessionid, id: game.id }, function() {
        return angular.copy(game);
      })
    })
  }

  collection.setField = function(sessionid, gameid, fieldname, value) {
    return $http.put( api + 'game/' + sessionid + '/' + gameid + '/' + fieldname + '/' + value);
  }

  collection.setUsers = function(sessionid, gameid, users){
    return $http.post( api + 'game/' + sessionid + '/' + gameid + '/users', users)
  }

  collection.deleteGame = function(sessionid, gameid) {
    return $http.delete( api + 'game/' + sessionid + '/' + gameid);
  }

  collection.move = function(sessionid, gameid, userid, move) {
    return $http.post( api + 'move/' + sessionid + '/' + gameid + '/' + userid, move)
  }
  return collection;
})
