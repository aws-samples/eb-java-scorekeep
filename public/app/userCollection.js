var module = angular.module('scorekeep');
module.service('UserCollection', function(UserService, api) {
  var collection = {};
  collection.createUser = function(username, userid) {
    if ( userid != null ) {
      return UserService.get({ id: userid })
    }
    var user = new UserService();
    if ( username === "random") {
      return user.$save();
    } else {
      user.name = username;
      return user.$save();
    }
  }
  return collection;
})
