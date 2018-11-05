var module = angular.module('scorekeep');
module.controller('RolitGameController', Rolit);
function Rolit($q, $scope, $http, $interval, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
    $scope.game = new GameService;
    $scope.state = new StateService; // game state object
    $scope.gamestate = []; // game state as Array
    $scope.color = []; // cell colors
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
                if ( $scope.gamestate[1] === '0' ) {
                    $scope.winner = "red wins!";
                } else if ( $scope.gamestate[1] === '1') {
                    $scope.winner = "green wins!";
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
    }, 500);

  //  $scope.red = false;
  //  $scope.green = false;
  //  $scope.rolit = true;

  //  $scope.move = function(){
  //      $scope.red = true;
  //     $scope.rolit = false;

  //  }

    for (i = 0; i<65; i++) {
        $scope.color[i] = 2;
    }

    $scope.color_id = function(id) {
        if ($scope.color[id]=== 1){
            return "green"} else
        if ($scope.color[id]=== 0){
            return "red"} else
            return "square_rolit";
    }

    $scope.move = function(cellid){
         if ( $scope.moving === 1 || $scope.winner !== '' ) {
             return;
         }
    
         $scope.moving = 1;
    
         $scope.promise.then(function(){
             $scope.promise = $q(function(resolve,reject){
                 console.log("MOVE on cell " + cellid);
                 $scope.gamestate = $scope.state.state.split('');
                 move = ""
                 // move is invalid
                 if ( $scope.gamestate[cellid] !== " " ) {
                     return;
                 }
                 // temporarily update game board and determine move
             //    if ($scope.color[0] === "0") {
             //        $scope.color[cellid] = "0";
             //        $scope.color[0] = "1";
             //        move = "0" + cellid;
             //    } else {
             //        $scope.color[cellid] = "1";
             //        $scope.color[0] = "0";
             //        move = "1" + cellid;
             //    }
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
                     //Update color
                     if ($scope.gamestate[cellid] === "0")
                        $scope.color[cellid] = 0;
                        else
                            $scope.color[cellid] = 1;

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
