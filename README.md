# Scorekeep
Scorekeep is a RESTful web API implemented in Java that uses Spring to provide an HTTP interface for creating and managing game sessions and users. This project includes the Scorekeep API and a front-end web app that consumes it. The front end and API can run on the same server and domain or separately, with the API running in Elastic Beanstalk and the front end served statically by a CDN.

The `master` branch shows the use of Spring, Angular, nginx, the [AWS SDK for Java](http://aws.amazon.com/sdkforjava), [Amazon DynamoDB](http://aws.amazon.com/dynamodb), Gradle, CORS, and [AWS Elastic Beanstalk](http://aws.amazon.com/elasticbeanstalk) features that enable you to:

- Run both components on the same [Amazon EC2](http://aws.amazon.com/ec2) instance.
- Create required DynamoDB and [Amazon SNS](http://aws.amazon.com/sns) resources as part of the Elastic Beanstalk environment.
- Build the API from source on instance during deployment.

Other branches extend the application's functionality and show the use of other AWS services. See the readme in each branch for details about the integration and instructions for use.

**Branches**
- [`cognito`](https://github.com/aws-samples/eb-java-scorekeep/tree/cognito) - Support login and store users in an [Amazon Cognito](http://aws.amazon.com/cognito) user pool. Get AWS SDK credentials and make service calls with a Cognito identity pool.
- [`cognito-basic`](https://github.com/aws-samples/eb-java-scorekeep/tree/cognito-basic) - Use Cognito for user ID storage. User pool only, no identity pool.
- [`ecs`](https://github.com/aws-samples/eb-java-scorekeep/tree/ecs) - Run the frontend and API in separate Docker containers in Amazon EC2 Container Service (ECS). This branch is adapted from the `fargate` branch and uses the Elastic Beanstalk Multicontainer Docker platform to create an ECS cluster and deploy the task definition. Dynamo DB tables and other dependencies are created with a CloudFormation template instead of using configuration files.
- [`fargate`](https://github.com/aws-samples/eb-java-scorekeep/tree/fargate) - Use AWS Fargate to run Scorekeep in serverless containers. Build Docker images for API and frontend components, upload to Elastic Container Registry, and generate Elastic Container Service task definitions with included scripts. Use Fargate to run containers without provisioning EC2 instances.
- [`lambda`](https://github.com/aws-samples/eb-java-scorekeep/tree/lambda) - Call an [AWS Lambda](http://aws.amazon.com/lambda) function to generate random names.
- [`lambda-worker`](https://github.com/aws-samples/eb-java-scorekeep/tree/lambda-worker) - Run a Lambda function periodically to process game records and store the output in Amazon S3.
- [`sql`](https://github.com/aws-samples/eb-java-scorekeep/tree/sql) - Use JDBC to store game histories in an attached PostgreSQL database instance.
- [`xray`](https://github.com/aws-samples/eb-java-scorekeep/tree/xray) - Use the [AWS X-Ray SDK for Java](http://docs.aws.amazon.com/xray-sdk-for-java/latest/javadoc/) to instrument incoming requests, functions, SDK clients, SQL queries, HTTP clients, startup code, and AWS Lambda functions.
- [`xray-cognito`](https://github.com/aws-samples/eb-java-scorekeep/tree/xray-cognito) - Use AWS credentials obtained with Amazon Cognito to upload trace data to X-Ray from the browser.
- [`xray-ecs`](https://github.com/aws-samples/eb-java-scorekeep/tree/xray-ecs) - Instrumented version of the `ecs` branch. Run the X-Ray daemon in a docker container. Configure networking between containers both locally and on ECS.
- [`xray-gettingstarted`](https://github.com/aws-samples/eb-java-scorekeep/tree/xray-gettingstarted) ([tutorial](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html)) - Use the AWS X-Ray SDK for Java to instrument incoming requests and SDK clients (no additional configuration required).
- [`xray-worker`](https://github.com/aws-samples/eb-java-scorekeep/tree/xray-worker) - Instrumented Python Lambda worker function from the `lambda-worker` branch.

Use the procedures in the following sections to run the project on Elastic Beanstalk and configure it for local testing and development.

**Sections**
- [Requirements](#requirements)
- [Setup](#setup)
- [Deploy](#deploy)
- [Test](#test)
- [Configure notifications](#configure-notifications)
- [Cleanup](#cleanup)
- [How it works](#how-it-works)
- [Running the project locally](#running-the-project-locally)
- [Contributing](#contributing)

# Requirements

To deploy the sample application, you need the following:

- User permissions - AWS Elastic Beanstalk, AWS CloudFormation, Amazon DynamoDB, Amazon SNS, IAM
- The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
- [The AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).

To run and test the project API locally, you also need Java and Gradle.

- [Java 8 runtime environment (SE JRE)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Gradle 5](https://gradle.org/releases/)

## Get permission to use Elastic Beanstalk
If you're using an IAM user with limited permissions, good work! Add Elastic Beanstalk permissions to your user account to get started.

**To add Elastic Beanstalk permissions to an IAM user**

1. Sign in to a user or role with administrator permissions.
2. Open the [users page](https://console.aws.amazon.com/iam/home#/users) of the IAM console.
3. Choose a user.
4. Choose **Add permissions**.
5. Add the  **AWSElasticBeanstalkFullAccess** managed policy.

The CloudFormation template for this project creates an IAM role that allows the Beanstalk environment to access its DynamoDB tables and SNS topic. If you don't have IAM permissions, check with your account owner about getting temporary access with an IAM role.

# Setup
Download or clone this repository.

    $ git clone git@github.com:aws-samples/eb-java-scorekeep.git
    $ cd eb-java-scorekeep

To create a new bucket for deployment artifacts, run `1-create-bucket.sh`. Or, if you already have a bucket, create a file named `bucket-name.txt` that contains the name of your bucket.

    eb-java-scorekeep$ ./1-create-bucket.sh
    make_bucket: beanstalk-artifacts-a5e491dbb5b22e0d

# Deploy
To deploy the application, run `2-deploy.sh`.

    eb-java-scorekeep$ ./2-deploy.sh
    Uploading to 38403831f48ce35031975951e796a859  171897 / 171897.0  (100.00%)
    Successfully packaged artifacts and wrote output template to file out.yml.
    Waiting for changeset to be created..
    Waiting for stack create/update to complete
    Successfully created/updated stack - scorekeep

This script uses AWS CloudFormation to deploy the Elastic Beanstalk environment and an IAM role. If the AWS CloudFormation stack that contains the resources already exists, the script updates it with any changes to the template or code.

# Test
To get the website's URL, run `3-open-website.sh`.

    eb-java-scorekeep$ ./3-open-website.sh
    http://awseb-e-p-AWSEBLoa-N0ZJXMPLF97Z-260056452.us-east-2.elb.amazonaws.com

Use the website to create a session and play a game. The Elastic Beanstalk environment serves the website statically at the root path, and runs the API under `/api`. The website is stateless and calls the API to manage resources.

To run the API locally, run `4-run-local.sh`. The API is a Spring application that accesses the AWS resources (tables and topic) in your Elastic Beanstalk environment. It uses the permissions in the AWS SDK credentials that you've configured for use with the AWS CLI.

    eb-java-scorekeep$ ./4-run-local.sh
    :compileJava UP-TO-DATE
    :processResources UP-TO-DATE
    :classes UP-TO-DATE
    :findMainClass
    :bootRun
      .   ____          _            __ _ _
     /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
    ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
     \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
      '  |____| .__|_| |_|_| |_\__, | / / / /
     =========|_|==============|___/=/_/_/_/
     :: Spring Boot ::        (v1.4.0.RELEASE)
    2020-04-09 17:42:34.242  INFO 2381 --- [           main] scorekeep.Application                    : Starting Application on localhost with PID 2381 (eb-java-scorekeep/build/classes/main started by user in eb-java-scorekeep)
    2020-04-09 17:42:34.246  INFO 2381 --- [           main] scorekeep.Application                    : No active profile set, falling back to default profiles: default

To test the API, run `5-test-local.sh` in another shell terminal.

    eb-java-scorekeep$ ./5-test-local.sh

This script uses cURL to send HTTP requests to the local API endpoint (`localhost:5000`).

# Configure notifications
The API uses SNS to send a notification email when a game ends. To enable notifications, configure your email address in an environment variable.

**To enable notifications**

1. Open the [Elastic Beanstalk console](https://console.aws.amazon.com/elasticbeanstalk/home#/environments).
2. Choose the **BETA** environment.
2. Choose **Configuration**.
3. Next to **Software**, choose **Modify**.
4. Under **Environment properties**, set **NOTIFICATION_EMAIL** to your email address.
5. Check your email for a subscription confirmation.
6. Complete a game to trigger a notification.

# Cleanup
To delete the application, run `6-cleanup.sh`.

    eb-java-scorekeep$ ./6-cleanup.sh

# How it works
The project includes two independent components

- An HTML and JavaScript front end in Angular 1.5 
- A Java backend that uses Spring to provide a public API

## Backend
The API runs at paths under /api that provide access to user, session, game, state, and move resources stored as JSON documents in DynamoDB. The API is RESTful, so you can create resources by sending HTTP POST requests to the resource path, for example /api/session. See the [test script](https://github.com/aws-samples/eb-java-scorekeep/blob/master/bin/test-api.sh) for example requests with cURL.

The [Buildfile](https://github.com/aws-samples/eb-java-scorekeep/blob/master/Buildfile) tells Elastic Beanstalk to run `./gradlew build` during deployment to create an executable JAR file. The [Procfile](https://github.com/aws-samples/eb-java-scorekeep/blob/master/Procfile) tells Elastic Beanstalk to run that JAR on port 5000.

## Front end
The front end is an Angular 1.5 web app that uses `$resource` objects to perform CRUD operations on resources defined by the API. Users first encounter the [main view](https://github.com/aws-samples/eb-java-scorekeep/blob/master/public/main.html) and [controller](https://github.com/aws-samples/eb-java-scorekeep/blob/master/public/app/mainController.js) and progress through session and game views at routes that include the IDs of resources that the user creates.

The front end is served statically by the nginx proxy at the root path. The [nginx.conf](https://github.com/aws-samples/eb-java-scorekeep/blob/master/.ebextensions/nginx/nginx.conf) file in the source code overwrites the default configuration provided by the Elastic Beanstalk Java platform to serve the front end statically, and forward requests to paths starting with /api to the API running on port 5000.

# Running the project locally
You can run both the API and front end locally with Gradle and the Spring Boot CLI.
To get started, clone this repository or extract the contents of the source bundle that you downloaded earlier.

## Run the Scorekeep API with Gradle
The API requires DynamoDB tables to exist in AWS to run locally. These tables are created by [the application template](https://github.com/aws-samples/eb-java-scorekeep/blob/master/template.yml) when you deploy it. When you delete the application stack, the tables are deleted as well.

The application reads environment variables to get the region and table names for calls to DynamoDB. The application template sets these on the Elastic Beanstalk environment. When you run the application locally, the `4-run-local.sh` script reads the resource names from the CloudFormation stack and gets the region from the AWS CLI:

    #!/bin/bash
    set -eo pipefail
    export AWS_REGION=$(aws configure get region)
    export NOTIFICATION_TOPIC=$(aws cloudformation describe-stack-resource --stack-name scorekeep --logical-resource-id notificationTopic --query 'StackResourceDetail.PhysicalResourceId' --output text)
    export GAME_TABLE=$(aws cloudformation describe-stack-resource --stack-name scorekeep --logical-resource-id gameTable --query 'StackResourceDetail.PhysicalResourceId' --output text)
    ...
    ./gradlew bootrun

The application needs AWS credentials to communicate with DynamoDB. In Elastic Beanstalk, Scorekeep gets credentials from the instance profile, which is the IAM role that is attached to the EC2 instance that runs the code. When you run the application locally, the AWS SDK for Java can retrieve credentials from files in `~/.aws/` or environment variables.

Follow the instructions in the *AWS SDK for Java Developer Guide* to provide access keys to the application: [Set up AWS Credentials for Development](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).

Use the test script to verify that the API works.

    ~/eb-java-scorekeep$ ./bin/test-api.sh

The script targets `localhost:5000`. However, you can point it at the API running on any host by modifying the API variable at the top of the file.

## Run the front end and configure it for local testing
Use the [Spring Boot CLI](http://docs.spring.io/spring-boot/docs/current/reference/html/getting-started-installing-spring-boot.html) to run the front end on port 8080.

    ~/eb-java-scorekeep$ spring run app.groovy

Open the app in a browser: [localhost:8080](http://localhost:8080)

The app loads but can't hit the API, because it's trying to call paths relative to its own root, `localhost:8080`, but the API is running on `localhost:5000` (or in Elastic Beanstalk). To fix this, configure the app with the full URL of the API.

**To configure the web app with an absolute path to the API**

1. Open [eb-java-scorekeep/public/app/scorekeep.js](https://github.com/aws-samples/eb-java-scorekeep/blob/master/public/app/scorekeep.js).
2. Set the value of the api module to the full URL of the API.
   * Use the domain name of your environment to test changes to the front end without running the backend locally.

            module.value('api', 'http://scorekeep.XXXXXXXX.elasticbeanstalk.com/api/');

   * Use localhost:5000 to test both front end and backend changes when running both locally.

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

This lets requests originating from a front end hosted locally on port 8080 to send requests to the API hosted on a different local port (i.e., `localhost:5000`) or on Elastic Beanstalk. This enables you to work on the front end locally and see changes immediately, without needing to deploy the source to your environment. When you make changes to the HTML or CSS in the app, simply refresh the browser.

When you run both the API and front end in the same Elastic Beanstalk environment, CORS is not required because the scripts that contact the API and the API itself are hosted on the same domain. To run the front end on a different port locally, or even host it on a completely different domain, add an allowed origin to the filter to whitelist it in the API.

**To extend the CORS configuration to allow cross-origin requests from specific domains**

1. Open [src/main/java/scorekeepSimpleCORSFilter.java](https://github.com/aws-samples/eb-java-scorekeep/blob/master/src/main/java/scorekeep/SimpleCORSFilter.java).
2. Add an allowed origin with the URL serving the front end.

        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://scorekeep.XXXXXXXX.elasticbeanstalk.com");

3. Save the files and commit your changes.

        ~/eb-java-scorekeep$ git commit -am "Update API domain"

4. Deploy the change.

        ~/eb-java-scorekeep$ ./2-deploy.sh

# Contributing

This sample application could be better with your help!

- Add a new game!
  - Implement game logic in the game class. See [TicTacToe.java](https://github.com/aws-samples/eb-java-scorekeep/blob/master/src/main/java/scorekeep/TicTacToe.java).
  - Add the class to [RulesFactory.java](https://github.com/aws-samples/eb-java-scorekeep/blob/master/src/main/java/scorekeep/RulesFactory.java).
- Create your own client front end!
  - Web frameworks - Angular 2, React, ember, etc.
  - Mobile app
  - Desktop application
- Integrate with other AWS services!
  - CICD with [AWS CodeCommit](http://aws.amazon.com/codecommit), [AWS CodePipeline](http://aws.amazon.com/codepipeline), [AWS CodeBuild](http://aws.amazon.com/codebuild), and [AWS CodeDeploy](http://aws.amazon.com/codedeploy)
  - Analytics with [Amazon Kinesis](https://aws.amazon.com/kinesis), [Amazon Athena](http://aws.amazon.com/athena), [Amazon EMR](http://aws.amazon.com/emr), or [Amazon QuickSight](https://quicksight.aws/)
  - Security with [Amazon VPC](http://aws.amazon.com/vpc)
  - Performance with [Amazon ElastiCache](http://aws.amazon.com/elasticache)
  - Scalability with [Amazon CloudFront](http://aws.amazon.com/cloudfront)
  - Portability with [Amazon ECS](http://aws.amazon.com/ecs) or CloudFormation
  - Accessibility with [Amazon Polly](https://aws.amazon.com/documentation/polly/)
- Write tests!
  - Unit tests
  - Integration tests
  - Functional tests
  - Load tests
- File an [issue](https://github.com/aws-samples/eb-java-scorekeep/issues) to report a bug or request new features.
