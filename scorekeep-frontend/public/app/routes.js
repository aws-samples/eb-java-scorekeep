var module = angular.module("scorekeep");
module.config(function($routeProvider) {
  $routeProvider
  .when("/", {
    templateUrl : "main.html",
    controller : "MainController"
  })
  .when("/sessions", {
    templateUrl : "sessions.html",
    controller : "SessionsController"
  })
  .when("/session/:sessionid/:userid", {
    templateUrl : "session.html",
    controller : "SessionController"
  })
  .when("/users", {
    templateUrl : "users.html",
    controller : "UserController"
  })
  .when("/games/:sessionid", {
    templateUrl : "games.html",
    controller : "GamesController"
  })
  .when("/game/:sessionid/:gameid/:userid", {
    templateUrl : "game.html",
    controller : "GameController"
  });
});
