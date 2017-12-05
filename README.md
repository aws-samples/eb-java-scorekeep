# Scorekeep
Scorekeep is a RESTful web API implemented in Java that uses Spring to provide an HTTP interface for creating and managing game sessions and users. This project includes the Scorekeep API and a front-end web app that consumes it. The front end and API can run on the same server and domain or separately, with the API running in Elastic Beanstalk and the front end served statically by a CDN.

The `fargate` branch shows the use of Spring, Angular, nginx, the [AWS SDK for Java](http://aws.amazon.com/sdkforjava), [Amazon DynamoDB](http://aws.amazon.com/dynamodb), Gradle, and [AWS ECS Fargate](http://aws.amazon.com/ecs) features that enable you to:

- Run both components in the same [Amazon ECS](http://aws.amazon.com/ecs) task definition behind an [Amazon Application Load Balancer](https://aws.amazon.com/elasticloadbalancing/)
- Create required DynamoDB and [Amazon SNS](http://aws.amazon.com/sns) resources through Cloudformation
- Publishes container logs to [Amazon Cloudwatch Logs](https://aws.amazon.com/cloudwatch)

Other branches extend the application's functionality and show the use of other AWS services. See the readme in each branch for details about the integration and instructions for use.

**Branches**
- [`master`](https://github.com/awslabs/eb-java-scorekeep/tree/master) - Original [AWS Elastic Beanstalk](https://aws.amazon.com/elasticbeanstalk/) scorekeep application.
- [`cognito`](https://github.com/awslabs/eb-java-scorekeep/tree/cognito) - Support login and store users in an [Amazon Cognito](http://aws.amazon.com/cognito) user pool. Get AWS SDK credentials and make service calls with a Cognito identity pool.
- [`cognito-basic`](https://github.com/awslabs/eb-java-scorekeep/tree/cognito-basic) - Use Cognito for user ID storage. User pool only, no identity pool.
- [`lambda`](https://github.com/awslabs/eb-java-scorekeep/tree/lambda) - Call an [AWS Lambda](http://aws.amazon.com/lambda) function to generate random names.
- [`lambda-worker`](https://github.com/awslabs/eb-java-scorekeep/tree/lambda-worker) - Run a Lambda function periodically to process game records and store the output in Amazon S3.
- [`sql`](https://github.com/awslabs/eb-java-scorekeep/tree/sql) - Use JDBC to store game histories in an attached PostgreSQL database instance.
- [`xray`](https://github.com/awslabs/eb-java-scorekeep/tree/xray) - Use the [AWS X-Ray SDK for Java](http://docs.aws.amazon.com/xray-sdk-for-java/latest/javadoc/) to instrument incoming requests, functions, SDK clients, SQL queries, HTTP clients, startup code, and AWS Lambda functions.
- [`xray-cognito`](https://github.com/awslabs/eb-java-scorekeep/tree/xray-cognito) - Use AWS credentials obtained with Amazon Cognito to upload trace data to X-Ray from the browser.
- [`xray-gettingstarted`](https://github.com/awslabs/eb-java-scorekeep/tree/xray-gettingstarted) ([tutorial](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html)) - Use the AWS X-Ray SDK for Java to instrument incoming requests and SDK clients (no additional configuration required).
- [`xray-worker`](https://github.com/awslabs/eb-java-scorekeep/tree/xray-worker) - Instrumented Python Lambda worker function from the `lambda-worker` branch.

Use the procedures in the following sections to run the project on Fargate and configure it for local testing and development.

**Sections**
- [Prerequisites](#prerequisites)
- [Repository Layout](#repository-layout)
- [Cloudformation Setup](#cloudformation-setup)
- [Building the Java application](#building-the-java-application)
- [How it works](#how-it-works)
- [Running the project locally](#running-the-project-locally)
- [Contributing](#contributing)

# Prerequisites
- Docker installed locally
- AWS CLI installed locally with permission for: IAM, DynamoDB, SNS, ECS, Cloudwatch Logs, and ECR

# Repository Layout
The project contains two independent applications:

- An HTML and JavaScript front end in Angular 1.5 to be ran with Nginx
- A Java backend that uses Spring to provide a public API

The backend and frontend are both built using `docker` and `make`. Docker images are published to Amazon ECR.

| Directory | Contents                                        | Build           | Package         | Publish        | Clean         |
|-----------|-------------------------------------------------|-----------------|-----------------|----------------|---------------|
| `/`       | Contains the Java Backend (aka `scorekeep-api`) | `make build`   | `make package`  | `make publish` | `make clean`   |
| `/scorekeep-frontend` | Contains the Angular+Nginx frontend |  N/A            | `make package`  | `make publish`  |  N/A         |
| `/task-definition` |  Contains template to generata a Task Definition | `./generate-task-definition` | N/A | aws ecs register-task-definition --cli-input-json file://scorekeep-task-definition.json | N/A |
| `/cloudformation` | Contains the Cloudformation template for creating the dependant resources (i.e. DynamoDB, SNS, CWL, ECR, and IAM) | N/A | N/A | `make stack` | `make clean` |

# Cloudformation setup

The pre-requisite resources can be setup using Cloudformation. 

The Cloudformation template requires no paramaters and can be ran by executing `make publish` from the directory. It will use the `AWS_REGION` configured in `aws.env` in the root of the package, and the default credentials from the AWS CLI. IAM permissions are needed for the Cloudformation stack to run successfully.

# Building the Java application

The Java application is built using the gradle Docker container so it does not rely on your local Java or Gradle setup. The output of the build process appears in the `build/` folder of the project. After you build the application you will want to package it into a docker container so it can be executed. The docker container packaging takes the JAR produced from the build step and adds it to a Java base image. It then configures the environment, ports, and entry point. The docker can be ran locally with valid AWS credentials, or ran on ECS.

# Deploying the application

To deploy the containers to your AWS Account,

1. Setup the Cloudformation stack to create the prerequisite resources by executing `make stack` in the `cloudformation/` folder
2. Build and Publish your API container to the ECR repository created by Cloudformation executing `make publish` in the root folder
3. Build and Publish your Frontend container to the ECR repository created by Cloudformation executing `make publish` in the `scorekeep-frontend/` folder
4. Populate your Task Definition with the correct region and account id using the `generate-task-definition` script in the `task-definition` folder
5. Register your Task Definition to ECS with `aws ecs register-task-definition --cli-input-json file://scorekeep-task-definition.json`
6. Launch your Service or Task using the AWS CLI, ECS CLI, or AWS Console


![Scorekeep flow](/img/scorekeep-flow.png)

Click through the app to explore its functionality. Use the network console in your browser to see the HTTP requests that it sends to the API to read and write users, sessions, games, moves, and game state to DynamoDB via the API.

# Configuring notifications
The API uses SNS to send a notification email when a game ends. To enable e-mail notifications, to an email address to your Task Definition in environment variable **NOTIFICATION_EMAIL**. To enable other notifications add the subscription to the topic through the SNS console.

# How it works

## Backend
The API runs at paths under /api that provide access to user, session, game, state, and move resources stored as JSON documents in DynamoDB. The API is RESTful, so you can create resources by sending HTTP POST requests to the resource path, for example /api/session. See the [test script](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/bin/test-api.sh) for example requests with cURL.

The Cloudformation template creates a DynamoDB table for each resource type.

## Front end
The front end is an Angular 1.5 web app that uses `$resource` objects to perform CRUD operations on resources defined by the API. Users first encounter the [main view](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/scorekeep-frontend/public/main.html) and [controller](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/scorekeep-frontend/public/app/mainController.js) and progress through session and game views at routes that include the IDs of resources that the user creates.

The front end is served statically by an Nginx container. The [nginx.conf](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/scorekeep-frontend/nginx.conf) file in the source code sets up Nginx to serve the frontend html pages from root, and forward requests starting with /api to the API backend running on port 5000. 

# Running the project locally
You can run both the API and front-end locally with Docker
To get started, clone this repository.

## Run the Scorekeep API with Docker

The API requires DynamoDB tables and SNS topic to exist in AWS to run locally. To create the tables and SNS topic run the Cloudformation template with `make stack` after setting the desired region in `aws.env`

After the Cloudformation stack has finished creating execute `make run-local` in the root directory to start the API container. You can later do `docker attach` to follow log output or `docker ps` to view the current state. When running local we use `net=host` networking mode in Docker as this is most simialr to the `awsvpc` network mode on ECS.

The application needs AWS credentials to communicate with DynamoDB. In ECS, Scorekeep gets credentials from the task role. When you run the application locally, we will mount in ~/.aws/ to the container where the AWS SDK for Java will retrieve credentials. You can also configure your credentials as environment variables if you prefer.

Follow the instructions in the *AWS SDK for Java Developer Guide* to provide access keys to the application: [Set up AWS Credentials for Development](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).

Use the test script to verify that the API works.

    ~/eb-java-scorekeep$ ./bin/test-api.sh

The script targets `localhost:5000`. However, you can point it at the API running on any hostname or IP by modifying the API variable at the top of the file.

## Run the Scorekeep Frontend with Docker

You can run the frontend container locally by executing `make run-local` from the `scorekeep-frontend` directory. As with the API container this runs with `net=host` as the networkmode. You should be able to hit it locally with a web browser or curl on [localhost:8080](http://localhost:8080)

# Contributing

This sample application could be better with your help!

- Add a new game!
  - Implement game logic in the game class. See [TicTacToe.java](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/src/main/java/scorekeep/TicTacToe.java).
  - Add the class to [RulesFactory.java](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/src/main/java/scorekeep/RulesFactory.java).
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
  - Accessibility with [Amazon Polly](https://aws.amazon.com/documentation/polly/)
- Write tests!
  - Unit tests
  - Integration tests
  - Functional tests
  - Load tests
- File an [issue](https://github.com/awslabs/eb-java-scorekeep/issues) to report a bug or request new features.
