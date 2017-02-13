var module = angular.module('scorekeep');
module.controller('UserController', User);
function User($scope, $http, UserService, api) {
  $scope.users = UserService.query();

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
