var module = angular.module('scorekeep');
module.controller('CognitoController', Cognito);
function Cognito($scope, $http, UserService, api, cognitoUserPoolId, cognitoClientId, cognitoRegion) {
  // Scope
  $scope.username = "myname";
  $scope.password = "testpassword";
  $scope.user = {};
  $scope.cognitoUser = {};
  $scope.errormessage = "";

  // Cognito stuff
  AWSCognito.config.region = cognitoRegion;
  var poolData = {
    UserPoolId : cognitoUserPoolId,
    ClientId : cognitoClientId
  };
  var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);

  $scope.createUser = function () {
    $scope.errormessage = "";
    user = new UserService();
    // Create scorekeep user
    CreateScorekeepUser = user.$save(function() {
      user.name = $scope.username;
      return user.$update({ id: user.id }, function() {
        $scope.user = angular.copy(user);
        return user;
      })
    })
    // Create cognito user
    CreateCognitoUser = CreateScorekeepUser.then( function(user) {
      var dataUserId = {
          Name : 'custom:userid',
          Value : user.id
      };
      var attributeUserId = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataUserId);
      var userData = {
        Username : $scope.username,
        Pool : userPool
      };
      var attributeList = [];
      attributeList.push(attributeUserId);
      var cognitoUser;
      return userPool.signUp($scope.username, $scope.password, attributeList, null, function(err, result){
        if (err) {
          // Delete scorekeep user if cognito signup fails
          $http.delete( api + 'user/' + user.id);
          $scope.errormessage = err.message;
          $scope.user = {};
          $scope.cognitoUser = {};
          return;
        }
        cognitoUser = result.user;
        console.log('user name is ' + cognitoUser.getUsername());
        $scope.cognitoUser = angular.copy(cognitoUser);
        return cognitoUser;
      });
    })
    CreateCognitoUser.then( function(user) {
      $scope.login();
    })
  };

  $scope.login = function() {
    $scope.errormessage = "";
    var authenticationData = {
      Username : $scope.username,
      Password : $scope.password,
    };

    var userData = {
      Username : $scope.username,
      Pool : userPool
    };

    var authenticationDetails = new AWSCognito.CognitoIdentityServiceProvider.AuthenticationDetails(authenticationData);

    var cognitoUser = new AWSCognito.CognitoIdentityServiceProvider.CognitoUser(userData);
    cognitoUser.authenticateUser(authenticationDetails, {
      onSuccess: function (result) {
        console.log('access token = ' + result.getAccessToken().getJwtToken());
        cognitoUser.getUserAttributes(function(err, attributes){
          console.log(attributes);
          var userid;
          for (i=0; i < attributes.length ; i++) {
            console.log(attributes[i]);
            if (attributes[i].Name == "custom:userid") {
              userid = attributes[i].Value;
              break;
            }
          }
          console.log('userid = ' + userid);
          $scope.user = UserService.get({ id: userid });
          $scope.cognitoUser = angular.copy(cognitoUser);
        });
      },
      onFailure: function(err) {
        $scope.errormessage = err.message;
      }
    });

  }
  $scope.logout = function() {
    $scope.errormessage = "";
    $scope.cognitoUser.signOut();
    console.log('Signed out from Cognito.');
    $scope.user = {};
    $scope.cognitoUser = {};
    $scope.errormessage = "";
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
