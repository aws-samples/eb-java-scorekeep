var module = angular.module('scorekeep');
module.controller('SessionController', Session);
function Session($scope, $http, $location, $routeParams, SessionService, RulesService, GameCollection, UserService, GameService, api) {
  $scope.games = GameService.query({ sessionid: $routeParams.sessionid });
  $scope.session = SessionService.get({ id: $routeParams.sessionid });
  $scope.user = UserService.get({ id: $routeParams.userid });
  $scope.allrules = RulesService.query();

  $scope.createGame = function (gamename, gamerules) {
    var sessionid = $routeParams.sessionid;

    CreateGame = GameCollection.createGame(sessionid, gamename, gamerules);
    CreateGame.then(function(game) {
      $scope.games.push(game);
    });
  };

  $scope.setGameRules = function(gameid, rulesid){
    return GameCollection.setField($scope.session.id, gameid, "rules", rulesid);
  }
  $scope.startGame = function(index){
    // refresh session before setting users?
    users = $scope.games[index].users = $scope.session.users;
    gameid = $scope.games[index].id;
    rulesid = $scope.games[index].rules;
    sessionid = $scope.session.id;
    SetUsers = GameCollection.setUsers(sessionid, gameid, users);
    /* PUT /user cannot handle fast requests
    for (user in users) {
      GameCollection.setField($scope.session.id, $scope.games[index].id, "user", users[user]);
    }*/
    SetRules = SetUsers.then(function(setUsersResult) {
      return $scope.setGameRules(gameid, rulesid);
    });
    SetStartTime = SetRules.then(function(setRulesResult) {
      time = $scope.games[index].startTime = Date.now();
      return GameCollection.setField($scope.session.id, $scope.games[index].id, "starttime", time);
    });
    SetStartTime.then(function(setStartTimeResult) {
      $location.path('/game/' + $scope.session.id + '/' + $scope.games[index].id + '/' + $scope.user.id);
    });
  }
  $scope.endGame = function(index){
    time = $scope.games[index].endTime = Date.now();
    GameCollection.setField($scope.session.id, $scope.games[index].id, "endtime", time);
  }
  $scope.deleteGame = function (index) {
    var sessionid = $routeParams.sessionid;
    var gameid = $scope.games[index].id;
    DeleteGame = GameCollection.deleteGame(sessionid, gameid);
    DeleteGame.then(function () {
      $scope.games.splice(index, 1);
    });
  };
  $scope.deleteSession = function (index) {
    var sessionId = $scope.session.id;
    DeleteSession = SessionCollection.deleteSession(sessionid);
    DeleteSession.then(function() {
      $location.path('/');
    });
  };
}
