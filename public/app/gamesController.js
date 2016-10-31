var module = angular.module('scorekeep');
module.controller('GamesController', Games);
function Games($scope, $http, $routeParams, SessionService, UserService, GameService, api) {
  $scope.games = GameService.query({ sessionid: $routeParams.sessionid });
  $scope.session = SessionService.get({ id: $routeParams.sessionid });
  $scope.gamename = "my game";

  $scope.createGame = function () {
    var game = new GameService();
    game.$save( { sessionid: $routeParams.sessionid }, function() {
      game.session = $routeParams.sessionid;
      game.name = $scope.gamename;
      game.$update({ sessionid: $routeParams.sessionid, id: game.id }, function() {
        $scope.games.push(angular.copy(game));
      })
    }
  )};
  $scope.deleteGame = function (index) {
    var gameId = $scope.games[index].id;
    $http.delete( api + 'game/' + $routeParams.sessionid + "/" + gameId);
    $scope.games.splice(index, 1);
  };
}
