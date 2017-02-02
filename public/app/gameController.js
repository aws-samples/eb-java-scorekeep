var module = angular.module('scorekeep');
module.controller('GameController', Game);
function Game($q, $scope, $http, $interval, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
  $scope.game = new GameService;
  $scope.state = new StateService; // game state object
  $scope.gamestate = []; // game state as Array
  $scope.moving = 0;
  $scope.user = UserService.get({ id: $routeParams.userid });
  $scope.winner = '';

  $scope.playgame = function(){
    return $q(function(resolve, reject) {
      GetGame = $scope.game.$get({ sessionid: $routeParams.sessionid, id: $routeParams.gameid });
      GetState = GetGame.then(function(){
        currentstate = $scope.game.states[$scope.game.states.length-1];
        return $scope.state.$get({ sessionid: $routeParams.sessionid, gameid: $routeParams.gameid, id: currentstate});
      })
      SetState = GetState.then(function(result){
        $scope.gamestate = $scope.state.state.split('');
        if ( $scope.gamestate[0] == 'A' ) {
          $scope.winner = "X wins!";
        } else if ( $scope.gamestate[0] == 'B') {
          $scope.winner = "O wins!";
        }
        resolve();
      });
    });
  }
  $scope.promise = $scope.playgame();
  $scope.interval = $interval(function(){
    $scope.promise.then(function() {
      $scope.promise = $scope.playgame();
    })
  }, 5000);

  $scope.move = function(cellid){
    if ( $scope.moving == 1 || $scope.winner != '' ) {
      return;
    }
    $scope.moving = 1;
    $scope.promise.then(function(){
      $scope.promise = $q(function(resolve,reject){
        console.log("MOVE on cell " + cellid);
        $scope.gamestate = $scope.state.state.split('');
        move = ""
        // move is invalid
        if ( $scope.gamestate[cellid] != " " ) {
          return;
        }
        // temporarily update game board and determine move
        if ($scope.gamestate[0] == "X") {
          $scope.gamestate[cellid] = "X";
          $scope.gamestate[0] = "O";
          move = "X" + cellid;
        } else {
          $scope.gamestate[cellid] = "O";
          $scope.gamestate[0] = "X";
          move = "O" + cellid;
        }
        // send move
        PostMove = $http.post(api + 'move/' + $routeParams.sessionid + "/" + $routeParams.gameid + "/" + $routeParams.userid, move);
        // get new game state
        GetGame = PostMove.then(function(){
          return $scope.game.$get({ sessionid: $routeParams.sessionid, id: $routeParams.gameid });
        })
        GetState = GetGame.then(function(GetGameResult){
          stateid = $scope.game.states[$scope.game.states.length-1];
          return $scope.state.$get({ sessionid: $routeParams.sessionid, gameid: $routeParams.gameid, id: stateid});
        });
        // update game board
        GetState.then(function(){
          $scope.gamestate = $scope.state.state.split('');
          $scope.moving = 0;
          resolve();
        });

      });

    });
  }
  $scope.$on('$destroy',function(){
    if($scope.interval)
        $interval.cancel($scope.interval);
  });
}
