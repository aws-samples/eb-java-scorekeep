var AWS = require('aws-sdk');
AWS.config.update({region: process.env.REGION});
var Chance = require('chance');

var myFunction = function(event, context, callback) {
  var sns = new AWS.SNS();
  var chance = new Chance();
  var userid = event.userid;

  var name = chance.first();

  // Notify
  var params = {
    Message: 'Created random name "' + name + '" for user ' + userid + '.',
    Subject: 'Scorekeep user created',
    TopicArn: process.env.TOPIC_ARN
  };
  sns.publish(params, function(err, data) {
    if (err) {
      console.log(err, err.stack);
      callback(err);
    }
    else {
      console.log(data);
      callback(null, {"name": name});
    }
  });
};

exports.handler = myFunction;