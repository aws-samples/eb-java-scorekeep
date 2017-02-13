var module = angular.module('scorekeep');
module.service('SessionCollection', function(SessionService, UserService, api) {
  var collection = {};
  collection.createSession = function(sessionname, userid) {
    var session = new SessionService();
    return session.$save()
    .then(function() {
      session.name = sessionname;
      session.owner = userid;
      return session.$update({ id: session.id })
      .then(function() {
        console.log("Created session: " + session.id);
        return session;
      })
    })
  }
  collection.joinSession = function(sessionid, userid) {
    var session = new SessionService();
    return session.$get({ id: sessionid })
    .then(function(){
      if ( session.users == null ) {
        session.users = [ userid ];
      } else {
        session.users.push(userid);
      }
      return session.$update({ id: session.id })
      .then(function(){
        console.log("added user to session");
        return session;
      });
    });
  }
  collection.deleteSession = function(sessionid) {
    return $http.delete( api + 'session/' + sessionId);
  }
  return collection;
})
