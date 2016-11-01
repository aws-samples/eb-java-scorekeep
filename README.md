# Scorekeep
Scorekeep is a RESTful web API implemented in Java that uses Spring to provide an HTTP interface for creating and managing game sessions and users. This project includes the scorekeep API and a frontend web app that consumes it. The frontend and API can run on the same server and domain or separately, with the API running in Elastic Beanstalk and the frontend served statically by a CDN.

The project shows the use of Spring, Angular, nginx, the AWS SDK for Java, DynamoDB, Gradle, CORS, and Elastic Beanstalk features that let you run both components on the same EC2 instance, create required DynamoDB tables as part of the Elastic Beanstalk environment, and build the API from source on-instance during deployment.

## Create an AWS Elastic Beanstalk environment
Create a Java 8 SE environment in Elastic Beanstalk to host the application.

1. Open the AWS Elastic Beanstalk Management Console with this preconfigured link: [console.aws.amazon.com/elasticbeanstalk/#/newApplication?applicationName=scorekeep...](https://console.aws.amazon.com/elasticbeanstalk/#/newApplication?applicationName=scorekeep&solutionStackName=Java)
2. Click **Create application** to create an application with an environment running the Java 8 SE platform. 
3. When your environment is ready, the console redirects you to the environment Dashboard.
4. Click the URL at the top of the page to open the site.

## Configure the application and create a source bundle
The application includes a frontend web app and a backend web API. In order for the frontend to send requests to the backend, it needs to know the URL of your environment.

1. Clone this repository.
2. Open [eb-java-scorekeep/public/app/scorekeep.js](https://github.com/awslabs/eb-java-scorekeep/blob/master/public/app/scorekeep.js).
3. Replace the placeholder value with the domain name of your environment.

        module.value('api', 'http://scorekeep.XXXXXXXX.elasticbeanstalk.com/api/');

4. Save the file and commit your changes.

        eb-java-scorekeep$ git commit -am "Update API domain"

5. Create an archive of the updated source code.

        eb-java-scorekeep$ git archive -o scorekeep-v1.zip HEAD

## Deploy the application
Deploy the source bundle that you created in the previous section to your environment.

1. Open the [Elastic Beanstalk Management Console](console.aws.amazon.com/elasticbeanstalk/home).
2. Click your environment's name to open the Dashboard.
3. Click **Upload and Deploy**.
4. Upload **scorekeep-v1.zip** and click **Deploy**.
5. Open the environment URL. 

![Scorekeep front page](/img/scorekeep-frontpage.png)

Click through the app to explore its functionality

# How it works

## Frontend
The frontend is an Angular 1.5 web app that uses `$resource` objects to perform CRUD operations on resources defined by the API. Users first encounter the [main view](https://github.com/awslabs/eb-java-scorekeep/blob/master/public/main.html) and [controller](https://github.com/awslabs/eb-java-scorekeep/blob/master/public/app/mainController.js) and progress through session and game views at routes that include the IDs of resources that the user creates.

The frontend is served statically by the nginx proxy at the root path. The [nginx.conf](https://github.com/awslabs/eb-java-scorekeep/blob/master/.ebextensions/nginx/nginx.conf) file in the source code overwrites the default configuration provided by the Elastic Beanstalk Java platform to serve the frontend statically, and forward requests to paths starting with /api to the API running on port 5000.

## Backend
The API runs at paths under /api that provide access to user, session, game, state, and move resources stored as JSON documents in DynamoDB. The API is RESTful, so you can create resources by sending HTTP POST requests to the resource path, for example /api/session. See the [test script](https://github.com/awslabs/eb-java-scorekeep/blob/master/bin/test-api.sh) for example requests with cURL.

The application includes a [configuration file](https://github.com/awslabs/eb-java-scorekeep/blob/master/.ebextensions/dynamodb-tables.config) that creates a DynamoDB table for each resource type.

The [Buildfile](https://github.com/awslabs/eb-java-scorekeep/blob/master/Buildfile) tells Elastic Beanstalk to run `gradle build` during deployment to create an executable JAR file. The [Procfile](https://github.com/awslabs/eb-java-scorekeep/blob/master/Procfile) tells Elastic Beanstalk to run that JAR on port 5000.

# Local testing
You can run both the API and frontend locally with Gradle and the Spring Boot CLI.

## API
Use [Gradle](https://gradle.org/) to build the API and run it locally on port 5000.
```
eb-java-scorekeep$ gradle bootrun
```

**Note:** The API requires DynamoDB tables to exist in AWS to run locally. These tables are created when you launch the application in Elastic Beanstalk.

Use the test script to verify that the API works.
```
eb-java-scorekeep$ ./bin/test-api.sh
```

## Frontend
Use the [Spring Boot CLI](http://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html) to run the frontend at [localhost:8080](http://localhost:8080)
```
eb-java-scorekeep$ spring run app.groovy
```

## CORS
The API includes a [CORS (cross-origin resource sharing) filter](https://github.com/awslabs/eb-java-scorekeep/blob/master/src/main/java/scorekeep/SimpleCORSFilter.java) that allows traffic from localhost:8080.
```java
  private static UrlBasedCorsConfigurationSource configurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    // Modify allowed origins if you run the client at a different endpoint
    config.addAllowedOrigin("http://localhost:8080");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
```
This lets requests originating from a frontend hosted locally on port 8080 to send requests to the API hosted on a different local port (i.e. localhost:5000) or on Elastic Beanstalk. This lets you work on the frontend locally and see changes immediately without needing to deploy the source to your environment.

When you run both the API and frontend in Elastic Beanstalk, CORS is not required because the scripts that contact the API and the API itself are hosted on the same domain. If you want to run the frontend on a different port locally, or even host it on a completely different domain, add an allowed origin to the filter to whitelist it in the API.


