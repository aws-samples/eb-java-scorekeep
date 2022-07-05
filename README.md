# Scorekeep
Scorekeep is a RESTful web API implemented in Java that uses Spring to provide an HTTP interface for creating and managing game sessions and users. This project includes the Scorekeep API and a front-end web app that consumes it. The frontend and API can run on the same server and domain or separately, with the API deployed through Amazon Elastic Container Service (ECS) via the EC2 launch type along with the frontend that is served statically by a CDN.

The `xray-gettingstarted` branch shows the use of Spring, Angular, nginx, the [AWS SDK for Java](http://aws.amazon.com/sdkforjava), [Amazon DynamoDB](http://aws.amazon.com/dynamodb), Gradle, Docker, and [AWS ECS](http://aws.amazon.com/ecs) features that enable you to:

- Run both components in the same [Amazon ECS](http://aws.amazon.com/ecs) task definition on an EC2 instance within an ECS cluster
- Create required ECS, DynamoDB, and [Amazon SNS](http://aws.amazon.com/sns) resources through Cloudformation
- Build and use the Scorekeep API and Frontend container images.

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

Use the procedures in the following sections to run the project on ECS through Cloudformation or configure it for local testing and development.

**Sections**
- [Requirements](#Requirements)
- [Repository Layout](#repository-layout)
- [Setup](#setup)
- [Building the Java application and Frontend](#building-the-java-application-and-frontend)
- [Deploying the application](#deploying-the-application)
- [Configuring notifications](#configuring-notifications)
- [Cleanup](#cleanup)
- [How it works](#how-it-works)
- [Building your own Docker Images of Scorekeep](#building-your-own-docker-images-of-scorekeep)
- [Running the project locally](#running-the-project-locally)
- [Contributing](#contributing)

# Requirements

To deploy the sample application, you will need the following:

- AWS user with permissions - IAMFullAccess, AmazonEC2FullAccess, AmazonEC2ContainerRegistryFullAccess, AmazonDynamoDBFullAccess, AmazonECS_FullAccess, AmazonSSMReadOnlyAccess, AmazonSNSFullAccess, AWSCloudFormationFullAccess
- The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
- [The AWS CLI (v2.7.9+)](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).
- Docker (to create docker images and upload them to ECR)

## Get permission to use AWS Cloudformation
If you're using an IAM user with limited permissions, good work! Add permissions to your user account to get started.

**To add permissions to an IAM user**

1. Sign in to a user or role with administrator permissions.
2. Open the [users page](https://console.aws.amazon.com/iam/home#/users) of the IAM console.
3. Choose a user.
4. Choose **Add permissions**.
5. Add the following policies: **IAMFullAccess**, **AmazonEC2FullAccess**, **AmazonEC2ContainerRegistryFullAccess**, **AmazonDynamoDBFullAccess**, **AmazonECS_FullAccess**, **AmazonSSMReadOnlyAccess**, **AmazonSNSFullAccess**, **AWSCloudFormationFullAccess**.

The CloudFormation template for this project creates resources that requires these policies. If you don't have IAM permissions, check with your account owner about getting temporary access with an IAM role.

# Repository Layout
The project contains two independent applications:

- An HTML and JavaScript frontend in Angular 1.5 to be ran with Nginx
- A Java backend that uses Spring to provide a public API

The backend and frontend are both built using `docker` and `make`. Docker images are published to Amazon ECR.

| Directory | Contents                                        | Build           | Package         | Publish        | Clean         |
|-----------|-------------------------------------------------|-----------------|-----------------|----------------|---------------|
| `/`       | Contains the Java Backend (aka `scorekeep-api`) | `make build`   | `make package`  | `make publish` | `make clean`   |
| `/scorekeep-frontend` | Contains the Angular+Nginx frontend |  N/A            | `make package`  | `make publish`  |  N/A         |
| `/cloudformation` | Contains the Cloudformation template for creating the dependant resources (i.e. DynamoDB, SNS, ECS, EC2, AutoScaling, ElasticLoadBalancingV2, and IAM) | N/A | N/A | `make stack` | `make clean` |
| `/run-local-resources` | Contains the Cloudformation template for creating local dependent resources (i.e. DynamoDB and SNS) | N/A | N/A | N/A | N/A |

# Setup
Download or clone this repository.

    $ git clone git@github.com:aws-samples/eb-java-scorekeep.git
    $ cd eb-java-scorekeep

# Building the Java application and Frontend

Building the project is not necessary to setup the project on Cloudformation. The Cloudformation uses container images of both the frontend and api of scorekeep from a public container repository.
If you would like to modify the project, you can [build and publish new container images of the frontend and API](#building-your-own-docker-images-of-scorekeep), and replace the image repository urls in the parameter section of the Cloudformation template.

The Java application is built using the gradle Docker container so it does not rely on your local Java or Gradle setup. The output of the build process appears in the `build/` folder of the project. After you build the application you will want to package it into a docker container so it can be executed. The docker container packaging takes the JAR produced from the build step and adds it to a Java base image. It then configures the environment, ports, and entry point. The docker can be ran locally with valid AWS credentials, or ran on ECS.

# Deploying the application

## Cloudformation setup

The pre-requisite resources are setup using Cloudformation. The included Cloudformation template cloudformation/cf-resources.yaml can create and deploy resources for the scorekeep application.

The Cloudformation template requires no paramaters and can be ran by executing the AWS command in the following section. Keep in mind the name of your AWS region. Use the command `aws configure get region` to get your region. IAM permissions are needed for the Cloudformation stack to run successfully.

## Creating the Cloudformation stack

Login into the AWS CLI with a user with permissions listed in the [requirements section](#Requirements).
In the `/cloudformation` directory, run the following command (replacing `<AWS_REGION>` with your AWS Region):

    aws --region <AWS_REGION> cloudformation create-stack --stack-name scorekeep --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources.yam

This command will create a Cloudformation stack that creates the resources for the scorekeep application to run on ECS via the EC2 launch type.

## Opening the application

Go to the [Cloudformation console](https://console.aws.amazon.com/cloudformation/home) to view the Cloudformation stack. It may take a few minutes for all the resources to finish creating.
Once the creation of the stack is complete, click on the `scorekeep` stack, and click on the `Outputs` tab.
Click on the load balancer URL to open the scorekeep application.

![Scorekeep flow](/img/scorekeep-flow.png)

Click through the app to explore its functionality. Use the network console in your browser to see the HTTP requests that it sends to the API to read and write users, sessions, games, moves, and game state to DynamoDB via the API.

# Configuring notifications
The API uses SNS to send a notification email when a game ends. To enable e-mail notifications, replace the `UPDATE_ME` text under the `Email` property inside the cf-resources.yaml file with a valid email. This will set the environment variable **NOTIFICATION_EMAIL** of the Task Definition in environment variable. To enable other notifications, add the subscription to the topic through the SNS console.

Updating the email through cf-resources.yaml will require the entire Cloudformation stack to be created from scratch.

## Configuring notifications via Cloudformation without redeploying the entire Cloudformation stack
Open the [cloud formation console](https://console.aws.amazon.com/cloudformation/home), select the `scorekeep` stack and click update.

**Update Stack Steps**

1. Select `Use current template` and click **Next**
2. Find the `Email` paramater and replace the `UPDATE_ME` text with a valid email. Click **Next**
3. Click **Next** at the bottom of the page.
4. At the bottom of the page, check the acknowledgement and then click **Update stack**.

Once the stack has been updated, run the following the command to restart the service with notifications sent to the provided email:

    aws ecs update-service --force-new-deployment --service scorekeep-service --cluster scorekeep-cluster --task-definition scorekeep

# Cleanup
To delete the application and resources setup through Cloudformation, run the following command (replacing `<AWS_REGION>` with your AWS Region):

    aws --region <AWS_REGION> cloudformation delete-stack --stack-name scorekeep

Alternatively, open the [cloud formation console](https://console.aws.amazon.com/cloudformation/home), select the `scorekeep` stack and click delete.

# How it works

## Backend
The API runs at paths under /api that provide access to user, session, game, state, and move resources stored as JSON documents in DynamoDB. The API is RESTful, so you can create resources by sending HTTP POST requests to the resource path, for example /api/session. See the [test script](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/bin/test-api.sh) for example requests with cURL.

The Cloudformation template creates a DynamoDB table for each resource type and an ECS cluster long with associated services.

## Frontend
The frontend is an Angular 1.5 web app that uses `$resource` objects to perform CRUD operations on resources defined by the API. Users first encounter the [main view](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/scorekeep-frontend/public/main.html) and [controller](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/scorekeep-frontend/public/app/mainController.js) and progress through session and game views at routes that include the IDs of resources that the user creates.

The frontend is served statically by an Nginx container. The [nginx.conf](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/scorekeep-frontend/nginx.conf) file in the source code sets up Nginx to serve the frontend html pages from root, and forward requests starting with /api to the API backend running on port 5000. 

# Building your own Docker Images of Scorekeep

If you would like to modify the project or test locally, you must build the docker images of scorekeep.
To test your changes on ECS, you will need to upload the images to a container image repository, and replace the image repository urls in the parameter section of the Cloudformation template.

Build your API container by executing `make package` in the root folder

Build your Frontend container by executing `make package` in the `scorekeep-frontend/` folder

## Using your own Docker Images of Scorekeep on ECS in the [`Deploying the application`](#Deploying-the-application) section
To test the scorekeep application with your own docker images, replace the image repository URLs in the parameter section of the Cloudformation template with your docker image repository URLs.
Follow the instructions in the [`Deploying the application`](#Deploying-the-application) section to test your changes on ECS.

# Running the project locally
You can run both the API and front-end locally with Docker.
To get started, clone this repository.

## Requirements

The `make` scripts will use the AWS_REGION and ACCOUNT_ID that must be configured in aws.env in the root of the package, and the default credentials from the AWS CLI.

## Run the Scorekeep API with Docker

The API requires DynamoDB tables and SNS topic to exist in AWS to run locally. If the Cloudformation resources from the [`Deploying the application`](#Deploying-the-application) section is still running, there is no need to re-create the DynamoDB and SNS resources.
Otherwise, you can use the Cloudformation template within the `run-local-resources/` directory and run the following command to create the DynamoDB and SNS resources (replacing `<AWS_REGION>` with your AWS Region):

    aws --region <AWS_REGION> cloudformation create-stack --stack-name scorekeep-local --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources-local.yaml

After the Cloudformation stack has finished creating execute `make run-local` in the root directory to start the API container. You can later do `docker attach` to follow log output or `docker ps` to view the current state.

The application needs AWS credentials to communicate with DynamoDB. In ECS, Scorekeep gets credentials from the task role. When you run the application locally, we will mount in ~/.aws/ to the container where the AWS SDK for Java will retrieve credentials. You can also configure your credentials as environment variables if you prefer.

Follow the instructions in the *AWS SDK for Java Developer Guide* to provide access keys to the application: [Set up AWS Credentials for Development](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).

Use the test script to verify that the API works.

    ~/eb-java-scorekeep$ ./bin/test-api.sh

The script targets `localhost:5000`. However, you can point it at the API running on any hostname or IP by modifying the API variable at the top of the file.

## Run the Scorekeep Frontend with Docker

In the scorekeep-frontend/public/app/scorekeep.js file, comment out the `module.value('api', '/api/');` line and uncomment the `// module.value('api', 'http://localhost:5000/api/');` line.

In the scorekeep-frontend/nginx.conf file, change the port that the server listens to from 80 to 8080.

You can run the frontend container locally by executing `make run-local` from the `scorekeep-frontend` directory. You should be able to hit it locally with a web browser or curl on [localhost:8080](http://localhost:8080)

## Run the X-Ray Daemon locally

### Prerequisites
Add the the `AWSXrayFullAccess` policy for the AWS user that is logged into the CLI to allow the X-Ray daemonc to export trace data.

Add the following environment variable to the Dockerfile in the root folder to allow the Scorekeep API to send data to the X-Ray daemon on macOS and Windows:

`ENV AWS_XRAY_DAEMON_ADDRESS="host.docker.internal:2000"`

Stop the docker container that is running the API and re-run it with the modified Dockerfile.

[Follow this guide to run the X-Ray daemon locally.](https://docs.aws.amazon.com/xray/latest/devguide/xray-daemon-local.html)

# Contributing

This sample application could be better with your help!

- Add a new game!
  - Implement game logic in the game class. See [TicTacToe.java](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/src/main/java/scorekeep/TicTacToe.java).
  - Add the class to [RulesFactory.java](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/src/main/java/scorekeep/RulesFactory.java).
- Create your own client frontend!
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
- File an [issue](https://github.com/aws-samples/eb-java-scorekeep/issues) to report a bug or request new features.
