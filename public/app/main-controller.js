var module = angular.module('scorekeep');

module.controller('MainController', Main);
function Main($scope, $http, $location, SessionService, SessionCollection, UserService, UserCollection, api) {
  $scope.sessions = SessionService.query();
  $scope.sessionname = "games";
  $scope.sessionid = "";
  $scope.username = "random";

  $scope.createSession = function (sessionname, username) {
    var sessionid;
    var userid;
    CreateUser = UserCollection.createUser(username, null);
    CreateSession = CreateUser.then(function(createUserResult){
      userid = createUserResult.id;
      return SessionCollection.createSession(sessionname, createUserResult.id);
    });
    JoinSession = CreateSession.then(function(createSessionResult) {
      console.log("session id: " + createSessionResult.id);
      sessionid = createSessionResult.id;
      return SessionCollection.joinSession(createSessionResult.id, createSessionResult.owner);
    });
    JoinSession.then(function(){
      $location.path('/session/'+ sessionid+ '/' + userid);
    });
  };

  $scope.joinSession = function(sessionid, username) {
    var userid;
    CreateUser = UserCollection.createUser(username, null);
    JoinSession = CreateUser.then(function(createUserResult) {
      userid = createUserResult.id;
      return SessionCollection.joinSession(sessionid, createUserResult.id);
    });
    JoinSession.then(function(joinSessionResult){
      $location.path('/session/'+ sessionid+ '/' + userid);
    });
  };

}
