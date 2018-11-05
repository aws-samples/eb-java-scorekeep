var module = angular.module('scorekeep');
module.controller('RolitGameController', Rolit);
function Rolit($q, $scope, $http, $interval, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
    $scope.game = new GameService;
    $scope.state = new StateService; // game state object
    $scope.color = []; // game state as Array
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
                $scope.color = $scope.state.state.split('');
                if ( $scope.color[1] === '1' ) {
                    $scope.winner = "black color wins!";
                } else if ( $scope.color[1] === '2') {
                    $scope.winner = "white color wins!";
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

    $scope.red = false;
    $scope.green = false;
    $scope.rolit = true;

    $scope.move = function(){
        $scope.red = true;
        $scope.rolit = false;

    }
    // $scope.move = function(cellid){
    //     if ( $scope.moving === 1 || $scope.winner !== '' ) {
    //         return;
    //     }
    //
    //     $scope.moving = 1;
    //
    //     $scope.promise.then(function(){
    //         $scope.promise = $q(function(resolve,reject){
    //             console.log("MOVE on cell " + cellid);
    //             $scope.color = $scope.state.state.split('');
    //             move = ""
    //             // move is invalid
    //             if ( $scope.color[cellid] !== " " ) {
    //                 return;
    //             }
    //             // temporarily update game board and determine move
    //             if ($scope.color[0] === "X") {
    //                 $scope.color[cellid] = "X";
    //                 $scope.color[0] = "O";
    //                 move = "X" + cellid;
    //             } else {
    //                 $scope.color[cellid] = "O";
    //                 $scope.color[0] = "X";
    //                 move = "O" + cellid;
    //             }
    //             // send move
    //             PostMove = $http.post(api + 'move/' + $routeParams.sessionid + "/" + $routeParams.gameid + "/" + $routeParams.userid, move);
    //             // get new game state
    //             GetGame = PostMove.then(function(){
    //                 return $scope.game.$get({ sessionid: $routeParams.sessionid, id: $routeParams.gameid });
    //             })
    //             GetState = GetGame.then(function(GetGameResult){
    //                 stateid = $scope.game.states[$scope.game.states.length-1];
    //                 return $scope.state.$get({ sessionid: $routeParams.sessionid, gameid: $routeParams.gameid, id: stateid});
    //             });
    //             // update game board
    //             GetState.then(function(){
    //                 $scope.color = $scope.state.state.split('');
    //                 $scope.moving = 0;
    //                 resolve();
    //             });
    //
    //         });
    //
    //     });
    // }



    $scope.$on('$destroy',function(){
        if($scope.interval)
            $interval.cancel($scope.interval);
    });
}
