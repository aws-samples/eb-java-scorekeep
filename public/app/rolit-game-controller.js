var module = angular.module('scorekeep');
//Game matrix
//var gameMatrix: string[][];
//var oldchar: string[];
module.controller('RolitGameController', Rolit);

function Rolit($q, $scope, $http, $interval, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
    $scope.game = new GameService;
    $scope.state = new StateService; // game state object
    $scope.gamestate = []; // game state as Array
    $scope.moving = 0;
    $scope.user = UserService.get({id: $routeParams.userid});
    $scope.winner = '';
    $scope.error_message = "";
    $scope.loading = true;

    // for (i = 0; i < 66; i++){
    //     if (i === 0){
    //         $scope.gamestate[i] = "0";
    //     } else if (i === 28){
    //         $scope.gamestate[i] = "0";
    //     } else if (i === 29){
    //         $scope.gamestate[i] = "1";
    //     } else if (i === 36){
    //         $scope.gamestate[i] = "1";
    //     } else if (i === 37){
    //         $scope.gamestate[i] = "0";
    //     } else {
    //         $scope.gamestate[i] = " ";
    //     }
    // }

    //make matrix
 //   $scope.makeMatrix = function(){
 //    for (int i = 0; i < 8; i++) {
 //           for (int j = 0; j < 8; j++) {
 //               gameMatrix[i][j] = gamestate[(i * 8) + j + 2];
 //           }
 //       }
 //   }

 //make char array from matrix

//    $scope.makeCharArrayFromMatrix = function(c1, c2) {
//        oldchar[0] = c1;
//        oldchar[1] = c2;
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                oldchar[(i * 8) + j + 2] = gameMatrix[i][j];
//            }
//        }
//    }

//    $scope.isMoveAllowed = function(i, j) {
//        length = 66;
//        if ($scope.isInRange1(i - 1, 66) && matrix[i - 1][j] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange1(j - 1, 66) && matrix[i][j - 1] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange1(i + 1, 66) && matrix[i + 1][j] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange1(j + 1, 66) && matrix[i][j + 1] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange(i - 1, j - 1, 66) && matrix[i - 1][j - 1] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange(i + 1, j + 1, 66) && matrix[i + 1][j + 1] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange(i - 1, j + 1, 66) && matrix[i - 1][j + 1] != ' ') {
//            return true;
//        }
//        if ($scope.isInRange(i + 1, j - 1, 66) && matrix[i + 1][j - 1] != ' ') {
//            return true;
//        }
//        return false;
//    }

//      $scope.isInRange = function(i, j, length) {
//        if(i >= 0 && i < length && j >= 0 && j < length)
//        return true;
//        else
//        return false;
//        }
    

    $scope.playgame = function () {
        return $q(function (resolve, reject) {
            GetGame = $scope.game.$get({sessionid: $routeParams.sessionid, id: $routeParams.gameid});
            GetState = GetGame.then(function () {
                currentstate = $scope.game.states[$scope.game.states.length - 1];
                return $scope.state.$get({
                    sessionid: $routeParams.sessionid,
                    gameid: $routeParams.gameid,
                    id: currentstate
                });
            });
            SetState = GetState.then(function (result) {
                $scope.gamestate = $scope.state.state.split('');
                $scope.loading = false;
                if ($scope.gamestate[1] === '0') {
                    $scope.winner = "red wins!";
                } else if ($scope.gamestate[1] === '1') {
                    $scope.winner = "green wins!";
                }
                resolve();
            });
        });
    };
    $scope.promise = $scope.playgame();
    $scope.interval = $interval(function () {
        $scope.promise.then(function () {
            $scope.promise = $scope.playgame();
        })
    }, 5000);

    $scope.color_id = function (id) {
        if ($scope.gamestate[id] === "1") {
            return "green"
        }
        else if ($scope.gamestate[id] === "0") {
            return "red"
        } else
            return "square_rolit";
    };

    $scope.gs = function () {
        if ($scope.gamestate[0] === "1") {
            return "green"
        }
        else if ($scope.gamestate[0] === "0") {
            return "red"
        }
    };

    $scope.move = function (cellid) {
        if ($scope.moving === 1 || $scope.winner !== '') {
            return;
        }

        $scope.moving = 1;

        $scope.promise.then(function () {
            $scope.promise = $q(function (resolve, reject) {
                console.log("MOVE on cell " + cellid);
                $scope.gamestate = $scope.state.state.split('');
                move = "";
                // move is invalid
                if ($scope.gamestate[cellid] !== " ") {
                    $scope.error_message = "Error: This cell is already occupied";
                    return;
                }
                // temporarily update game board and determine move
                if ($scope.gamestate[0] === "0") {
                    move = "0" + cellid;
                } else {
                    move = "1" + cellid;
                }
                // send move
                PostMove = $http.post(api + 'move/' + $routeParams.sessionid + "/" + $routeParams.gameid + "/" + $routeParams.userid, move);
                // get new game state
                GetGame = PostMove.then(function () {
                    return $scope.game.$get({sessionid: $routeParams.sessionid, id: $routeParams.gameid});
                });
                GetState = GetGame.then(function (GetGameResult) {
                    stateid = $scope.game.states[$scope.game.states.length - 1];
                    return $scope.state.$get({
                        sessionid: $routeParams.sessionid,
                        gameid: $routeParams.gameid,
                        id: stateid
                    });
                });
                // update game board
                GetState.then(function () {
                    $scope.gamestate = $scope.state.state.split('');
                    if ($scope.gamestate[cellid] === " ")
                        $scope.error_message = "Error: You can not move here";
                    else
                        $scope.error_message = "";
                    $scope.moving = 0;
                    resolve();
                });

            });

        });
    };

    $scope.$on('$destroy', function () {
        if ($scope.interval)
            $interval.cancel($scope.interval);
    });
}
