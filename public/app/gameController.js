var module = angular.module('scorekeep');
module.controller('GameController', Game);
function Game($scope, $http, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
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
      console.log(result);
      $scope.gamestate = $scope.state.state.split('');
      console.log("state string: '" + $scope.state.state + "'");
      console.log("state array: " + $scope.gamestate);
    });
  }
  $scope.playgame();

  $scope.move = function(cellid){
    console.log("MOVE on cell " + cellid);
    $scope.gamestate = $scope.state.state.split('');
    console.log("state id: " + $scope.state.id);
    console.log("state string: '" + $scope.state.state + "'");
    move = ""
    if ( $scope.gamestate[cellid] != " " ) {
      return;
    }
    if ($scope.gamestate[0] == "X") {
      $scope.gamestate[cellid] = "X";
      $scope.gamestate[0] = "O";
      move = "X" + cellid;
      console.log(move);
    } else {
      $scope.gamestate[cellid] = "O";
      $scope.gamestate[0] = "X";
      move = "O" + cellid;
      console.log(move);
    }
    console.log("new state string: '" + $scope.gamestate.join('') + "'");
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
