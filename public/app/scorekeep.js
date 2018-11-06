var module = angular.module("scorekeep", ["ngRoute", "ngResource"]);
/* API running at /api on the same domain name and port (no CORS) */
  module.value('api', '/api/');

/* API running on Elastic Beanstalk, frontend running locally or
   on a different domain (CORS) */
 //module.value('api', 'http://Scorekeep-env.sdap22vsfv.us-east-1.elasticbeanstalk.com/api/');

/* API running locally on port 5000, frontend on port 8080 (CORS) */
module.value('api', 'http://loccdсalhost:5000/api/');

