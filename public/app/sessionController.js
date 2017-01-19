var module = angular.module('scorekeep');
module.controller('SessionController', Session);
function Session($scope, $http, $location, $interval, $routeParams, SessionService, RulesService, GameCollection, UserService, GameService, api) {
  $scope.games = GameService.query({ sessionid: $routeParams.sessionid });
  $scope.session = new SessionService;
  $scope.user = UserService.get({ id: $routeParams.userid });
  $scope.allrules = RulesService.query();

  $scope.loadSession = function() {
    GetSession = $scope.games.$promise.then(function(result) {
      return $scope.session.$get({ id: $routeParams.sessionid });
    });
    GetSession.then(function() {
      // identify new games
      gameids = []
      if ( $scope.games == null ) {
        $scope.games = [];
      }
      for (var i = 0; i < $scope.games.length; i++) {
        // if the game has been removed from the session
        if ( !$scope.session.games.includes($scope.games[i].id) ) {
          $scope.games.splice(i, 1);
        } else {
          gameids.push($scope.games[i].id);
        }
      }
      if ( $scope.session.games == null ) {
        $scope.session.games = [];
      }
      for (var i = 0; i < $scope.session.games.length; i++) {
        if ( !gameids.includes($scope.session.games[i]) ) {
          console.log("new game id: " + $scope.session.games[i]);
          game = new GameService;
          game.$get({ id: $scope.session.games[i], sessionid: $routeParams.sessionid });
          $scope.games.push(game);
        }
      }
    })
  }
  $scope.loadSession();
  $scope.interval = $interval(function(){
    $scope.loadSession();
  }, 5000);
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
  $scope.$on('$destroy',function(){
    if($scope.interval)
        $interval.cancel($scope.interval);
  });
}
