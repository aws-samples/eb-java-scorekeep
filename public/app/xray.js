var crypto = window.crypto;
var module = angular.module('scorekeep');
module.service('XRay', function(api) {

  var service = {};

  service.getHexId = function(length) {
    var bytes = new Uint8Array(length);
    crypto.getRandomValues(bytes);
    hex = "";
    for (var i = 0; i < bytes.length; i++) {
      hex += bytes[i].toString(16);
    }
    return hex.substring(0,length);
  }
  service.getHexTime = function() {
    return Math.round(new Date().getTime() / 1000).toString(16);
  }
  service.getEpochTime = function() {
    return new Date().getTime()/1000;
  }
  service.getTraceHeader = function(segment) {
    return "Root=" + segment.trace_id + ";Parent=" + segment.id + ";Sampled=1";
  }
  service.beginSegment = function() {
    var segment = {};
    var traceId = '1-' + service.getHexTime() + '-' + service.getHexId(24);

    var id = service.getHexId(16);
    var startTime = service.getEpochTime();

    segment.trace_id = traceId;
    segment.id = id;
    segment.start_time = startTime;
    segment.name = 'Scorekeep-client';
    segment.in_progress = true;

    var documents = [];
    documents[0] = JSON.stringify(segment);
    service.putDocuments(documents);
    return segment;
  }

  service.endSegment = function(segment) {
    var endTime = service.getEpochTime();
    segment.end_time = endTime;
    segment.in_progress = false;
    var documents = [];
    documents[0] = JSON.stringify(segment);
    service.putDocuments(documents);
  }

  service.putDocuments = function(documents) {
    var xray = new AWS.XRay();
    var params = {
      TraceSegmentDocuments: documents
    };
    xray.putTraceSegments(params, function(err, data) {
      if (err) {
        console.log(err, err.stack);
      } else {
        console.log(data);
      }
    })
  }

  service.getServiceGraph = function() {
    var xray = new AWS.XRay();
    var params = {
      EndTime: Date.now()/1000,
      StartTime: Date.now()/1000 - 600
    };
    xray.getServiceGraph(params, function(err, data) {
      if (err) {
        console.log(err, err.stack);
      } else {
        console.log(data);
        var servicegraph = JSON.stringify(data, null, 2);
      }
    })
  }
  return service;
})
