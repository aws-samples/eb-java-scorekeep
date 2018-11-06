var module = angular.module('scorekeep');
module.controller('RolitGameController', Rolit);

function Rolit($q, $scope, $http, $interval, $routeParams, SessionService, UserService, GameService, GameCollection, RulesService, StateService, api) {
    $scope.game = new GameService;
    $scope.state = new StateService; // game state object
    $scope.gamestate = []; // game state as Array
    $scope.moving = 0;
    $scope.user = UserService.get({id: $routeParams.userid});
    $scope.winner = '';
    $scope.users = [];
    $scope.error_message = "";
    $scope.loading = false;
    $scope.color = '';

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
                $scope.users = $scope.game.users;
                $scope.set_color();
                $scope.loading = false;
                if ($scope.gamestate[1] === '0') {
                    $scope.winner = "red wins!";
                } else if ($scope.gamestate[1] === '1') {
                    $scope.winner = "green wins!";
                }else if ($scope.gamestate[1] === '2') {
                    $scope.winner = "yellow wins!";
                }else if ($scope.gamestate[1] === '3') {
                    $scope.winner = "blue wins!";
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
    }, 1500);

    $scope.set_color = function () {
        for (i = 0; i < $scope.users.length; i++) {
            console.log($scope.users[i]);
            if ($scope.users[i] === $scope.user.id) {
                $scope.color = i;
                console.log("Your color is " + i);
            }
        }
        $scope.gs();
    };

    $scope.color_id = function (id) {
        if ($scope.gamestate[id + 1] === "1") {
            return "green"
        } else if ($scope.gamestate[id + 1] === "0") {
            return "red"
        } else if ($scope.gamestate[id + 1] === "2") {
            return "yellow"
        } else if ($scope.gamestate[id + 1] === "3") {
            return "blue"
        } else
            return "square_rolit";
    };

    $scope.gs = function () {
        if ($scope.color === 1) {
            return "green"
        }
        else if ($scope.color === 0) {
            return "red"
        }
        else if ($scope.color === 2) {
            return "yellow"
        }
        else if ($scope.color === 3) {
            return "blue"
        }
    };
    $scope.now = function () {
        if ($scope.gamestate[0] === '1') {
            return "green"
        }
        else if ($scope.gamestate[0] === '0') {
            return "red"
        }
        else if ($scope.gamestate[0] === '2') {
            return "yellow"
        }
        else if ($scope.gamestate[0] === '3') {
            return "blue"
        }
    };



    $scope.move = function (cellid) {
        if ($scope.moving === 1 || $scope.winner !== '') {
            return;
        }

        if ($scope.gamestate[cellid + 1] !== " ") {
            $scope.error_message = "Error: This cell is already occupied";
            return;
        }

        if ($scope.gamestate[0] !== $scope.color.toString()) {
            $scope.error_message = "Error: This is not your turn";
            return;
        }

        $scope.moving = 1;

        $scope.promise.then(function () {
            $scope.promise = $q(function (resolve, reject) {
                console.log("MOVE on cell " + cellid);
                $scope.gamestate = $scope.state.state.split('');
                move = "";

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
                    if ($scope.gamestate[cellid + 1] === " ")
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
