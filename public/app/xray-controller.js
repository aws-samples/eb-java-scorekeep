var module = angular.module('scorekeep');

module.controller('XRayController', XRayController);
function XRayController($scope, $http, $location, SessionCollection, UserCollection, GameCollection, GameHistoryModel, api, $sce) {
  var ddbOutput = "";
  var rdsDefaultOutput = "";
  var rdsRunningOutput = "Populating table...";
  var ddbRunning = false;
  var rdsRunning = false;
  var shouldRunDdbDemo = false;
  var shouldRunRdsDemo = false;
  $scope.gameHistory = [];

  $scope.isRdsConfigured = false;
  var isRdsConfigured = function() {
    GameHistoryModel.get().then(
        function(success) {
            $scope.isRdsConfigured = true;
        },
        function(error) {
            $scope.isRdsConfigured = false;
        }
    );
  };
  isRdsConfigured();

  var runDdbDemo = function() {
    ddbRunning = true;
    var user1, user2, session, game;
    ddbOutput = "Creating users...<br/>";

    // Sick chaining
    UserCollection.createUser("random", null)
        .then(function(result) {
            console.log(result);
            user1 = result;
            ddbOutput += "Created user " + user1.name + ".<br/>";
            return UserCollection.createUser("random", null);
        })
        .then(function(result) {
            console.log(result);
            user2 = result;
            ddbOutput += "Created user " + user2.name + ".<br/>";
            ddbOutput += "Initializing session...<br/>";
            return SessionCollection.createSession(null, null);
        })
        .then(function(result) {
            console.log(result);
            session = result;
            ddbOutput += "Creating tic-tac-toe game...<br/>";
            return GameCollection.createGame(session.id, "tic-tac-toe", "TicTacToe");
        })
        .then(function(result) {
            console.log(result);
            game = result;
            ddbOutput += "Game is about to begin...<br/>";
            return GameCollection.setUsers(session.id, game.id, [user1.id, user2.id]);
        })
        .then(function(result) {
            console.log(result);
            // Avoid NPE in service
            return GameCollection.setField(session.id, game.id, "rules", "TicTacToe");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += "Playing game<br/>";
            ddbOutput += user1.name + " made move X1<br/>";
            return GameCollection.move(session.id, game.id, user1.id, "X1");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user2.name + " made move O2<br/>";
            return GameCollection.move(session.id, game.id, user2.id, "O2");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user1.name + " made move X3<br/>";
            return GameCollection.move(session.id, game.id, user1.id, "X3");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user2.name + " made move O4<br/>";
            return GameCollection.move(session.id, game.id, user2.id, "O4");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user1.name + " made move X5<br/>";
            return GameCollection.move(session.id, game.id, user1.id, "X5");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user2.name + " made move O6<br/>";
            return GameCollection.move(session.id, game.id, user2.id, "O6");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user1.name + " made move X7<br/>";
            return GameCollection.move(session.id, game.id, user1.id, "X7");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user2.name + " made move O8<br/>";
            return GameCollection.move(session.id, game.id, user2.id, "O8");
        })
        .then(function(result) {
            console.log(result);
            ddbOutput += user1.name + " made move X9<br/>";
            return GameCollection.move(session.id, game.id, user1.id, "X9");
        })
        .then(function(result) {
            ddbOutput += "Game Over!<br/>";
            // Keep repeating
            if (shouldRunDdbDemo) {
                runDdbDemo();
            } else {
                ddbRunning = false;
            }
        });
  };

  var runRdsDemo = function() {
    rdsRunning = true;
    GameHistoryModel.create()
        .then(function(result) {
            console.log(result);
            return GameHistoryModel.get();
        })
        .then(function(result) {
            console.log(result);
            $scope.gameHistory = result;
            if (shouldRunRdsDemo) {
                runRdsDemo();
            } else {
                rdsRunning = false;
            }
        });
  }

  $scope.getDdbOutput = function() {
    return $sce.trustAsHtml(ddbOutput);
  };

  $scope.getRdsOutput = function() {
    var output = rdsRunning ? rdsRunningOutput : rdsDefaultOutput;
    return $sce.trustAsHtml(output);
  };

  $scope.getDdbDemoPrompt = function() {
    if (shouldRunDdbDemo) {
        return "Stop";
    } else if (ddbRunning) {
        return "Finishing game";
    } else {
        return "Trace game sessions";
    }
  };

  $scope.getRdsDemoPrompt = function() {
    if (shouldRunRdsDemo) {
        return "Stop";
    } else if (rdsRunning) {
        return "Finishing game";
    } else {
        return "Trace SQL queries";
    }
  };

  $scope.toggleDdbDemo = function() {
    shouldRunDdbDemo = !shouldRunDdbDemo;
    if (shouldRunDdbDemo && !ddbRunning) {
        runDdbDemo();
    }
  };

  $scope.toggleRdsDemo = function() {
    shouldRunRdsDemo = !shouldRunRdsDemo;
    if (shouldRunRdsDemo && !rdsRunning) {
        runRdsDemo();
    }
  };
}