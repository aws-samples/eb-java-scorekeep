var module = angular.module('scorekeep');
module.controller('UserController', User);
function User($scope, $http, UserService, UserCollection, api) {
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
    $scope.users = UserService.query();
  })

  $scope.username = "my name";
  $scope.createUser = function () {
    user = new UserService();
    user.$save(function() {
      user.name = $scope.username;
      user.$update({ id: user.id }, function() {
        $scope.users.push(angular.copy(user));
      })
    }
  )};
  $scope.deleteUser = function (index) {
    var userId = $scope.users[index].id;
    $http.delete( api + 'user/' + userId);
    $scope.users.splice(index, 1);
  }
}
