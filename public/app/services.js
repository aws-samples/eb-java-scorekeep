var module = angular.module('scorekeep');
module.factory('SessionService', function($resource, api, XRay) {
  return $resource(api + 'session/:id', { id: '@_id' }, {
    segment: {},
    get: {
      method: 'GET',
      headers: {
        'X-Amzn-Trace-Id': function(config) {
          segment = XRay.beginSegment();
          return XRay.getTraceHeader(segment);
        }
      },
      transformResponse: function(data) {
        XRay.endSegment(segment);
        return angular.fromJson(data);
      },
    },
    query: {
      method: 'GET',
      isArray: true,
      headers: {
        'X-Amzn-Trace-Id': function(config) {
          segment = XRay.beginSegment();
          return XRay.getTraceHeader(segment);
        }
      },
      transformResponse: function(data) {
        XRay.endSegment(segment);
        return angular.fromJson(data);
      },
    },
    save: {
      method: 'POST',
      headers: {
        'X-Amzn-Trace-Id': function(config) {
          segment = XRay.beginSegment();
          return XRay.getTraceHeader(segment);
        }
      },
      transformResponse: function(data) {
        XRay.endSegment(segment);
        return angular.fromJson(data);
      },
    },
    update: {
      method: 'PUT',
      headers: {
        'X-Amzn-Trace-Id': function(config) {
          segment = XRay.beginSegment();
          return XRay.getTraceHeader(segment);
        }
      },
      transformResponse: function(data) {
        XRay.endSegment(segment);
        return angular.fromJson(data);
      },
    },
    remove: {
      method: 'DELETE',
      headers: {
        'X-Amzn-Trace-Id': function(config) {
          segment = XRay.beginSegment();
          return XRay.getTraceHeader(segment);
        }
      },
      transformResponse: function(data) {
        XRay.endSegment(segment);
        return angular.fromJson(data);
      },
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
module.factory('GameHistoryService', function($resource, api) {
    return $resource(api + 'history', {}, {
    });
});