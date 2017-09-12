var module = angular.module('scorekeep');
module.factory('SessionService', function($resource, api, XRay) {
  return $resource(api + 'session/:id', { id: '@_id' }, {
    segment: {},
    save: {
      method: 'POST',
      headers: {
        'X-Amzn-Trace-Id': function(config) {
          segment = XRay.beginSegment();
          return "Root=" + segment.trace_id + ";Parent=" + segment.id + ";Sampled=1";
        }
      },
      transformResponse: function(data) {
        XRay.endSegment(segment);
        return angular.fromJson(data);
      },
    },
    update: {
      method: 'PUT'
    }
  });
});
module.factory('UserService', function($resource, api) {
  return $resource(api + 'user/:id', { id: '@_id' }, {
    update: {
      method: 'PUT'
    }
  });
});
module.factory('GameService', function($resource, api) {
  return $resource(api + 'game/:sessionid/:id', { sessionid: '@_sessionid', id: '@_id' }, {
    update: {
      method: 'PUT'
    }
  });
});
module.factory('RulesService', function($resource, api) {
  return $resource(api + 'rules/:id', { id: '@_id' }, {
  });
});
module.factory('StateService', function($resource, api) {
  return $resource(api + 'state/:sessionid/:gameid/:id', { sessionid: '@_sessionid', gameid: '@_gameid', id: '@_id' }, {
  });
});