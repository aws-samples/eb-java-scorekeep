# AWS X-Ray
Documentation: [X-Ray SDK for Java Sample Application](http://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java-sample.html)

If you haven't used X-Ray with Scorekeep yet, try the [`xray-gettingstarted`](https://github.com/awslabs/eb-java-scorekeep/tree/xray-gettingstarted) branch first.

This branch shows advanced instrumentation with the AWS X-Ray SDK and includes features from other branches. Deploy this branch to see additional trace data in the X-Ray console. Then, follow the instructions below to add an instrumented AWS Lambda function and PostgreSQL database to the application.

## AWS Lambda
From branch: [`lambda`](https://github.com/awslabs/eb-java-scorekeep/tree/lambda)

In the [`UserFactory`](https://github.com/awslabs/eb-java-scorekeep/tree/xray/src/main/java/scorekeep/UserFactory.java) class, Scorekeep calls a Node.js AWS Lambda function to generate random usernames.  If the call to Lambda fails, Scorekeep falls back on a public API to generate names. 

Run the script in the `_lambda` folder to create the AWS Lambda function that Scorekeep calls to generate random names:
    eb-java-scorekeep/_lambda$ ./create-lambda-role.sh

The script uses a CloudFormation template and the AWS CLI to create the function and its execution role:
- `_lambda/lambda-role.yml`       - Template that defines the role
- `_lambda/create-lambda-role.sh` - Script to create the role
- `_lambda/delete-lambda-role.sh` - Script to delete the role

If you don't have the AWS CLI, [install it](http://docs.aws.amazon.com/cli/latest/userguide/installing.html) or use the CloudFormation console to create a stack with the template.

Next, add Lambda permission to your instance profile ([aws-elasticbeanstalk-ec2-role](https://console.aws.amazon.com/iam/home#/roles/aws-elasticbeanstalk-ec2-role)) to let the environment update the Lambda function:
- AWSLambdaFullAccess

Note: In the `lambda` branch, Scorekeep creates the Lambda function with a configuration file. In this branch, you create the function independently with the same template that creates the role. This lets the `xray` branch work even if the Lambda function and role have not been created, whereas in the `lambda` branch, the deployment fails if you haven't created the required role.

## Amazon RDS
From branch: [`sql`](https://github.com/awslabs/eb-java-scorekeep/tree/sql)
Documentation: [Instrumenting Calls to a PostgreSQL Database](https://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java-sample.html#xray-sdk-java-sample-postgresql)

In [`application-pgsql.properties`](https://github.com/awslabs/eb-java-scorekeep/tree/xray/src/main/resources/application-pgsql.properties), Scorekeep adds the X-Ray SDK tracing interceptor to the JDBC data source used by Hibernate.

[Add a PostgreSQL database](http://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.managing.db.html) to your Elastic Beanstalk environment to enable RDS tracing on the X-Ray demo page.

Hibernate also calls the database during application startup. No segment is available to the X-Ray SDK during startup, so we create one manually in [`RdsWebConfig.java`](https://github.com/awslabs/eb-java-scorekeep/blob/xray/src/main/java/scorekeep/RdsWebConfig.java#L83) by overriding Hibernate's `SchemaExport` class.

# Scorekeep
Scorekeep is a RESTful web API implemented in Java that uses Spring to provide an HTTP interface for creating and managing game sessions and users. This project includes the Scorekeep API and a frontend web app that consumes it. The frontend and API can run on the same server and domain or separately, with the API running in Elastic Beanstalk and the frontend served statically by a CDN.

The `master` branch shows the use of Spring, Angular, nginx, the AWS SDK for Java, DynamoDB, Gradle, CORS, and Elastic Beanstalk features that let you run both components on the same EC2 instance, create required DynamoDB and SNS resources as part of the Elastic Beanstalk environment, and build the API from source on-instance during deployment.

Other branches extend the application's functionality and show the use of other AWS services. See the readme in each branch for details about the integration, and instructions for use.

**Branches**
- [`cognito`](https://github.com/awslabs/eb-java-scorekeep/tree/cognito) - Support login and store users in a Cognito user pool.
- [`lambda`](https://github.com/awslabs/eb-java-scorekeep/tree/lambda) - Call a Lambda function to generate random names.
- [`sql`](https://github.com/awslabs/eb-java-scorekeep/tree/sql) - Use JDBC to store game histories in an attached PostgreSQL database instance.
- [`xray`](https://github.com/awslabs/eb-java-scorekeep/tree/xray) - Use the AWS X-Ray SDK to instrument incoming requests, SDK clients, SQL queries, HTTP clients, and sections of code.
- [`xray-gettingstarted`](https://github.com/awslabs/eb-java-scorekeep/tree/xray-gettingstarted) ([tutorial](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html)) - Use the AWS X-Ray to instrument incoming requests and SDK clients (no additional configuration required).

Use the procedures in the following sections to run the project on AWS Elastic Beanstalk and configure it for local testing and development.

**Sections**
- [Prerequisites](#prerequisites)
- [Deploy the application](#deploy-the-application)
- [Configure notifications](#configure-notifications)
- [How it works](#how-it-works)
- [Running the project locally](#running-the-project-locally)
- [Contributing](#contributing)

# Prerequisites
- User permissions - Elastic Beanstalk, IAM*
- Instance profile with permission to use DynamoDB and SNS
- Elastic Beanstalk environment running Java 8

*If you don't have permission to manage permissions in IAM, get someone who does to add DynamoDB and SNS permissions to the default Elastic Beanstalk instance profile.

## Get permission to use Elastic Beanstalk
If you are using an IAM user with limited permissions, good work! Add Elastic Beanstalk permissions to your user account to get started.

*To add Elastic Beanstalk permissions to an IAM user*

1. Sign in to a user or role with administrator permissions.
2. Open the [users page](https://console.aws.amazon.com/iam/home#/users) of the IAM console.
3. Choose a user.
4. Click **Add permissions**.
5. Add the  **AWSElasticBeanstalkFullAccess** managed policy.

## Create an Elastic Beanstalk environment
Create a Java 8 SE environment in Elastic Beanstalk to host the application.

*To create an Elastic Beanstalk environment running the Java 8 SE platform*

1. Open the AWS Elastic Beanstalk Management Console with this preconfigured link: [console.aws.amazon.com/elasticbeanstalk/#/newApplication?applicationName=scorekeep...](https://console.aws.amazon.com/elasticbeanstalk/#/newApplication?applicationName=scorekeep&solutionStackName=Java)
2. Click **Create application** to create an application with an environment running the Java 8 SE platform. 
3. When your environment is ready, the console redirects you to the environment Dashboard.
4. Click the URL at the top of the page to open the site.

## Give the application permission to use DynamoDB and SNS
When the Scorekeep API runs in AWS Elastic Beanstalk, it uses the permissions of its EC2 instance to call AWS. Elastic Beanstalk provides a default instance profile that you can extend to grant the application the permissions it needs to read from and write to resource tables in DynamoDB, and send notifications with SNS.

*To add DynamoDB and SNS permissions to the instances in your Elastic Beanstalk environment*

1. Open the Elastic Beanstalk instance profile in the IAM console: [aws-elasticbeanstalk-ec2-role](https://console.aws.amazon.com/iam/home#roles/aws-elasticbeanstalk-ec2-role)
2. Click **Attach Policy**.
3. Select [AmazonDynamoDBFullAccess](https://console.aws.amazon.com/iam/home#policies/arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess) and click **Attach Policy**.
3. Select [AmazonSNSFullAccess](https://console.aws.amazon.com/iam/home#policies/arn:aws:iam::aws:policy/AmazonSNSFullAccess) and click **Attach Policy**.

# Deploy the application
Deploy the source code for the project to your Elastic Beanstalk environment.

*To deploy the source code*

1. Download the source bundle: [eb-java-scorekeep-v1.zip](https://github.com/awslabs/eb-java-scorekeep/releases/download/v1.3/eb-java-scorekeep-v1.zip)
2. Open the [Elastic Beanstalk Management Console](console.aws.amazon.com/elasticbeanstalk/home).
3. Click your environment's name to open the Dashboard.
4. Click **Upload and Deploy**.
5. Upload **eb-java-scorekeep-v1.zip** and click **Deploy**.
6. Open the environment URL. 

![Scorekeep flow](/img/scorekeep-flow.png)

Click through the app to explore its functionality. Use the network console in your browser to see the HTTP requests that it sends to the API to read and write users, sessions, games, moves and game state to DynamoDB via the API.

# Configure notifications
The API uses Amazon SNS to send a notification email when a game ends. To enable notifications, configure your email address in an environment variable.

*To enable notifications*
1. Open your environment's page in the [environment management console](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environments-console.html).
2. Choose **Configuration**.
3. Choose **Software Configuration**
4. Under **Environment Properties**, set **NOTIFICATION_TOPIC** to your email address.
5. Check your email for a subscription confirmation.
6. Complete a game to trigger a notification.

# How it works
The project includes two independent components, an HTML and JavaScript frontend in Angular 1.5 and a Java backend that uses Spring to provide a public API.

## Backend
The API runs at paths under /api that provide access to user, session, game, state, and move resources stored as JSON documents in DynamoDB. The API is RESTful, so you can create resources by sending HTTP POST requests to the resource path, for example /api/session. See the [test script](https://github.com/awslabs/eb-java-scorekeep/blob/master/bin/test-api.sh) for example requests with cURL.

The application includes a [configuration file](https://github.com/awslabs/eb-java-scorekeep/blob/master/.ebextensions/dynamodb-tables.config) that creates a DynamoDB table for each resource type.

The [Buildfile](https://github.com/awslabs/eb-java-scorekeep/blob/master/Buildfile) tells Elastic Beanstalk to run `gradle build` during deployment to create an executable JAR file. The [Procfile](https://github.com/awslabs/eb-java-scorekeep/blob/master/Procfile) tells Elastic Beanstalk to run that JAR on port 5000.

## Frontend
The frontend is an Angular 1.5 web app that uses `$resource` objects to perform CRUD operations on resources defined by the API. Users first encounter the [main view](https://github.com/awslabs/eb-java-scorekeep/blob/master/public/main.html) and [controller](https://github.com/awslabs/eb-java-scorekeep/blob/master/public/app/mainController.js) and progress through session and game views at routes that include the IDs of resources that the user creates.

The frontend is served statically by the nginx proxy at the root path. The [nginx.conf](https://github.com/awslabs/eb-java-scorekeep/blob/master/.ebextensions/nginx/nginx.conf) file in the source code overwrites the default configuration provided by the Elastic Beanstalk Java platform to serve the frontend statically, and forward requests to paths starting with /api to the API running on port 5000.

# Running the project locally
You can run both the API and frontend locally with Gradle and the Spring Boot CLI.
Clone this repository or extract the contents of the source bundle that you downloaded earlier to get started.

## Run the Scorekeep API with Gradle
The API requires DynamoDB tables to exist in AWS to run locally. These tables are created by [this configuration file](https://github.com/awslabs/eb-java-scorekeep/blob/master/.ebextensions/dynamodb-tables.config) when you launch the application in Elastic Beanstalk. If you terminate the environment, the tables are deleted. To create the tables without a running environment, you can use the configuration file as a template to [create a CloudFormation stack](console.aws.amazon.com/cloudformation/home).

The application reads an environment variable named AWS_REGION to determine which region to connect to for calls to DynamoDB. In Elastic Beanstalk, this variable is set in a [configuration file](https://github.com/awslabs/eb-java-scorekeep/blob/master/.ebextensions/env.config) that reads the current region from CloudFormation and creates an Elastic Beanstalk environment property.

Set the environment variable and then use [Gradle](https://gradle.org/) to build the API and run it locally on port 5000.

    ~/eb-java-scorekeep$ export AWS_REGION=us-west-2
    ~/eb-java-scorekeep$ gradle bootrun

The application needs AWS credentials in order to communicate with Amazon DynamoDB. In AWS Elastic Beanstalk, Scorekeep gets credentials from the instance profile, the IAM role that is attached to the EC2 instance that runs the code. When you run the application locally, the AWS SDK for Java can retrieve credentials from files in ~/.aws/ or environment variables.

Follow the instructions in the AWS SDK for Java developer guide to provide access keys to the application: [Set up AWS Credentials for Development](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html)

Use the test script to verify that the API works.

    ~/eb-java-scorekeep$ ./bin/test-api.sh

The script targets `localhost:5000`, but can be pointed at the API running on any host by modifying the API variable at the top of the file.

## Run the frontend and configure it for local testing
Use the [Spring Boot CLI](http://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html) to run the frontend on port 8080

    ~/eb-java-scorekeep$ spring run app.groovy

Open the app in a browser: [localhost:8080](http://localhost:8080)

The app loads but can't hit the API, because it's trying to call paths relative to it's own root, `localhost:8080`, but the API is running on `localhost:5000` (or in Elastic Beanstalk). To fix this, configure the app with the full URL of the API.

*To configure the web app with an absolute path to the API*

1. Open [eb-java-scorekeep/public/app/scorekeep.js](https://github.com/awslabs/eb-java-scorekeep/blob/master/public/app/scorekeep.js).
2. Set the value of the api module to the full URL of the API.
   * Use the domain name of your environment to test changes to the frontend without running the backend locally

            module.value('api', 'http://scorekeep.XXXXXXXX.elasticbeanstalk.com/api/');

   * Use localhost:5000 to test both frontend and backend changes when running both locally.

            module.value('api', 'http://localhost:5000/api/');

3. Refresh the app in your browser to load the updated script.

## Configure the API for CORS
The API includes a CORS (cross-origin resource sharing) filter that allows traffic from `localhost:8080`.

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

This lets requests originating from a frontend hosted locally on port 8080 to send requests to the API hosted on a different local port (i.e. `localhost:5000`) or on Elastic Beanstalk. This lets you work on the frontend locally and see changes immediately without needing to deploy the source to your environment. When you make changes to the HTML or CSS in the app, simply refresh the browser.

When you run both the API and frontend in the same Elastic Beanstalk environment, CORS is not required because the scripts that contact the API and the API itself are hosted on the same domain. If you want to run the frontend on a different port locally, or even host it on a completely different domain, add an allowed origin to the filter to whitelist it in the API.

*To extend the CORS configuration to allow cross-origin requests from specific domains*

1. Open [src/main/java/scorekeepSimpleCORSFilter.java](https://github.com/awslabs/eb-java-scorekeep/blob/master/src/main/java/scorekeep/SimpleCORSFilter.java)
2. Add an allowed origin with the URL serving the frontend.

        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://scorekeep.XXXXXXXX.elasticbeanstalk.com");

3. Save the files and commit your changes.

        ~/eb-java-scorekeep$ git commit -am "Update API domain"

4. Create an archive of the updated source code.

        ~/eb-java-scorekeep$ git archive -o scorekeep-v1.zip HEAD

5. Open your environment's Dashboard in the [Elastic Beanstalk Management Console](console.aws.amazon.com/elasticbeanstalk/home) and deploy the updated code.

# Contributing

This sample application could be better with your help!

- Add a new game!
  - Implement game logic in game class. See [TicTacToe.java](https://github.com/awslabs/eb-java-scorekeep/blob/master/src/main/java/scorekeep/TicTacToe.java)
  - Add the class to [RulesFactory.java](https://github.com/awslabs/eb-java-scorekeep/blob/master/src/main/java/scorekeep/RulesFactory.java)
- Create your own client frontend!
  - Web frameworks - Angular 2, React, ember, etc
  - Mobile app
  - Desktop application
- Integrate with other AWS services!
  - CICD with CodeCommit, CodePipeline, CodeBuild, and CodeDeploy
  - Analytics with Kinesis, Athena, EMR, or QuickSight
  - Security with VPC
  - Performance with ElastiCache
  - Scalability with CloudFront
  - Portability with ECS or CloudFormation
  - Accessibility with Polly
- Write tests!
  - Unit tests
  - Integration tests
  - Functional tests
  - Load tests
- File an [issue](https://github.com/awslabs/eb-java-scorekeep/issues) to report a bug or request new features.
