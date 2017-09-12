var module = angular.module('scorekeep');

module.controller('MainController', Main);
function Main($window, $scope, $http, $location, SessionService, SessionCollection, UserService, UserCollection, api) {
  $scope.sessionname = "games";
  $scope.sessionid = "";
  $scope.username = "random";
  $scope.password = null;
  $scope.user = {};
  $scope.cognitoUser = {};
  $scope.errormessage = "";
  $scope.signedin = sessionStorage['signedin'];
  $scope.showpassword = false;
  if ( sessionStorage['username'] ) {
    $scope.username = sessionStorage['username'];
  }
  var userPool;
  GetUserPool = $http.get( api + 'userpool');
  GetUserPool.then( function(userpool){
    // configure region and get poolData for Cognito
    var poolData = UserCollection.configureAWSClients(userpool);
    // create userPool
    userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);
    // get credentials
    if ( sessionStorage['JWTToken'] ) {
      AWS.config.credentials = UserCollection.getAWSCredentials(sessionStorage['JWTToken'], userpool.data);
    };
    // call Scorekeep
    $scope.sessions = SessionService.query();
  })
  $scope.signin = function() {
    if ( $scope.showpassword == false ) {
      $scope.showpassword = true;
      if ($scope.username == "random") {
        $scope.username = "";
      }
      return;
    }
    var sessionid;
    var userid;
    CreateUserResult = UserCollection.getUser($scope.username, $scope.password, null);
    CreateUserResult.then(function(result){
      $scope.cognitoUser = result.cognitoUser;
      userid = result.userid;
      $scope.user = UserService.get({ id: userid });
      $scope.errormessage = result.errormessage;
      $scope.signedin = "true";
      sessionStorage['signedin'] = "true";
      sessionStorage['username'] = $scope.username;
      $scope.showpassword = false;
    })
  }
  $scope.signup = function() {
    if ( $scope.showpassword == false ) {
      $scope.showpassword = true;
      return;
    }
    $scope.signin();
  }
  $scope.createSession = function (sessionname, username) {
    var sessionid;
    var userid;
    CreateUserResult = UserCollection.getUser($scope.username, $scope.password, null);
    CreateSession = CreateUserResult.then(function(result){
      $scope.cognitoUser = result.cognitoUser;
      userid = result.userid;
      $scope.user = UserService.get({ id: userid });
      $scope.errormessage = result.errormessage;
      return SessionCollection.createSession(sessionname, userid);
    })

    JoinSession = CreateSession.then(function(createSessionResult) {
      console.log("session id: " + createSessionResult.id);
      sessionid = createSessionResult.id;
      return SessionCollection.joinSession(createSessionResult.id, createSessionResult.owner);
    });

    JoinSession.then(function(joinSessionResult){
      console.log('redirecting to /session/'+ sessionid + '/' + userid);
      $window.location.assign('/#/session/'+ sessionid + '/' + userid);
      // or?
      //$location.path('/session/'+ sessionid + '/' + userid);
      //$scope.$apply();
    });
  };
  $scope.joinSession = function(sessionid, username) {
    var userid;
    CreateUser = UserCollection.getUser($scope.username, $scope.password, null);
    JoinSession = CreateUser.then(function(result) {
      $scope.cognitoUser = result.cognitoUser;
      userid = result.userid;
      $scope.user = UserService.get({ id: userid });
      $scope.errormessage = result.errormessage;
      return SessionCollection.joinSession(sessionid, userid);
    });
    JoinSession.then(function(joinSessionResult){
      console.log('redirecting to /session/'+ sessionid + '/' + userid);
      $window.location.assign('/#/session/'+ sessionid + '/' + userid);
    });
  };
  $scope.logout = function() {
    $scope.errormessage = "";
    $scope.user = {};
    $scope.errormessage = "";
    sessionStorage.clear();
    $scope.username = "random";
    $scope.signedin = false;
    $scope.cognitoUser.signOut();
    console.log('Signed out from Cognito.');
    $scope.cognitoUser = {};
  }
  $scope.deleteUser = function () {
    $scope.errormessage = "";
    var userId = $scope.user.id;
    if ( $scope.cognitoUser.deleteUser == null ) {
      $scope.errormessage = "Not signed in to Cognito."
      return;
    }
    $scope.cognitoUser.deleteUser(function(err, result) {
      if (err) {
        $scope.errormessage = err.message;
        return;
      }
      console.log('Cognito delete result: ' + result);
      $scope.user = {};
      $scope.cognitoUser = {};
      $http.delete( api + 'user/' + userId);
    });
  }
}
