var module = angular.module('scorekeep');
module.service('UserCollection', function(UserService, $http, api, cognitoUserPoolId, cognitoClientId, cognitoRegion) {
  var collection = {};
  // Cognito stuff
  AWSCognito.config.region = cognitoRegion;
  var poolData = {
    UserPoolId : cognitoUserPoolId,
    ClientId : cognitoClientId
  };
  var userPool = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserPool(poolData);

  collection.getUser = function(username, password, userid) {
    var promise = new Promise(function(resolve, reject){
      var out = {};
      /*
      if ( userid != null ) {
        out.user = UserService.get({ id: userid })
      }
      */
      // Password provided
      if ( password != null ) {
        console.log('signing in to cognito');
        SignInResult = collection.cognitoSignIn(username, password);
        SignInResult.then(function(result){
          console.log('signed in, resolving');
          resolve(result);
        }, function(error){
          console.log('log in FAILED, signing up for cognito');
          if ( userid == null ) {
            GetSUserResult = collection.createUser(username, null)
          } else {
            GetSUserResult = UserService.get({ id: userid })
          }
          SignUpResult = GetSUserResult.then(function(user){
            userid = user.id;
            console.log("signing up user (id: " + userid + ") for Cognito");
            return collection.cognitoSignUp(username, password, userid);
          });
          SignUpResult.then(function(result){
            console.log('signing in to cognito');
            SignInResult = collection.cognitoSignIn(username, password);
            SignInResult.then(function(result){
              console.log('signed in, resolving');
              resolve(result);
            }, function(error){
              console.log('signin after signup FAILED');
            });
          }, function(error){
            console.log('sign up failed: ' + error);
            reject();
          })
        })
      }
      //Password is null
      // Create scorekeep user
      else {
        console.log("creating cognito user");
        CreateUser = collection.createUser(username, null);
        CreateUser.then(function(user){
          out.userid = user.id
          resolve(out);
        })
      }
    });
    return promise;
  }
  collection.cognitoSignUp = function(username, password, userid) {
    promise = new Promise( function(resolve,reject){
      var out = {};
      var dataUserId = {
        Name : 'custom:userid',
        Value : userid
      };
      var attributeUserId = new AWSCognito.CognitoIdentityServiceProvider.CognitoUserAttribute(dataUserId);
      var userData = {
        Username : username,
        Pool : userPool
      };
      var attributeList = [];
      attributeList.push(attributeUserId);
      var cognitoUser;
      userPool.signUp(username, password, attributeList, null, function(err, result){
        if (err) {
          // Delete scorekeep user if cognito signup fails
          $http.delete( api + 'user/' + userid);
          out.errormessage = err.message;
          out.user = {};
          out.cognitoUser = {};
          reject(out);
        } else {
          cognitoUser = result.user;
          console.log('user name is ' + cognitoUser.getUsername());
          out.cognitoUser = angular.copy(cognitoUser);
          resolve(out);
        }
      });
    })
    return promise;
  }
  collection.cognitoSignIn = function(username, password) {
    promise = new Promise( function(resolve,reject){

      var out = {};
      out.errormessage = "";

      var authenticationData = {
        Username : username,
        Password : password,
      };

      var userData = {
        Username : username,
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
            out.userid = userid;
            out.cognitoUser = angular.copy(cognitoUser);
            /* TODO: Use stored credentials to access AWS */
            // sessionStorage.setItem("cognitoAccessToken", cognitoUser.signInUserSession.accessToken.jwtToken);
            // sessionStorage.setItem("cognitoIdToken", cognitoUser.signInUserSession.idToken.jwtToken);
            // sessionStorage.setItem("cognitoRefreshToken", cognitoUser.signInUserSession.refreshToken.token);

            resolve(out);
          });
        },
        onFailure: function(err) {
          out.errormessage = err.message;
          reject(out);
        }
      });
    });
    return promise;
  }
  // why does createUser take a userid?
  collection.createUser = function(username, userid) {
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
