var module = angular.module('scorekeep');
module.controller('GameController', Game);
function Game($scope, $http, $interval, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
  $scope.game = new GameService;
  $scope.state = new StateService; // game state object
  $scope.gamestate = []; // game state as Array

  $scope.playgame = function(){
    GetGame = $scope.game.$get({ sessionid: $routeParams.sessionid, id: $routeParams.gameid });
    GetState = GetGame.then(function(){
      currentstate = $scope.game.states[$scope.game.states.length-1];
      return $scope.state.$get({ sessionid: $routeParams.sessionid, gameid: $routeParams.gameid, id: currentstate});
    })
    GetState.then(function(result){
      $scope.gamestate = $scope.state.state.split('');
    });
  }
  $scope.playgame();
  $scope.interval = $interval(function(){
    $scope.playgame();
  }, 5000);

  $scope.move = function(cellid){
    $scope.gamestate = $scope.state.state.split('');
    move = ""
    if ( $scope.gamestate[cellid] != " " ) {
      return;
    }
    if ($scope.gamestate[0] == "X") {
      $scope.gamestate[cellid] = "X";
      $scope.gamestate[0] = "O";
      move = "X" + cellid;
    } else {
      $scope.gamestate[cellid] = "O";
      $scope.gamestate[0] = "X";
      move = "O" + cellid;
    }
    PostMove = $http.post(api + 'move/' + $routeParams.sessionid + "/" + $routeParams.gameid + "/" + $routeParams.userid, move);
    GetGame = PostMove.then(function(){
      return $scope.game.$get({ sessionid: $routeParams.sessionid, id: $routeParams.gameid });
    })
    GetState = GetGame.then(function(GetGameResult){
      stateid = $scope.game.states[$scope.game.states.length-1];
      return $scope.state.$get({ sessionid: $routeParams.sessionid, gameid: $routeParams.gameid, id: stateid});
    });
    GetState.then(function(){
      $scope.gamestate = $scope.state.state.split('');
    });
  }

}
