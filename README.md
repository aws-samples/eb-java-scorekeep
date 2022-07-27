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
- [Running the application on ECS via CloudFormation](#Running-the-application-on-ECS-via-CloudFormation)
- [Repository Layout](#repository-layout)
- [Setup](#setup)
- [Requirements](#Requirements)
- [Running the project locally](#Running-the-project-locally)
- [Building your own Docker Images of Scorekeep for ECS](#Building-your-own-Docker-Images-of-Scorekeep-for-ECS)
- [How it works](#how-it-works)
- [Contributing](#contributing)

# Running the application on ECS via CloudFormation

Follow the [Getting started with AWS X-Ray](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html) guide to run this application on Scorekeep on ECS via the cloudformation template included in this project (`cloudformation/cf-resources.yaml`). This guide uses public container images of this project that are hosted on ECR. If you would like to [modify the project and test out the changes on ECS](#Building-your-own-Docker-Images-of-Scorekeep-for-ECS), you will need to build and host your own Docker images of this project, and then replace the container image URLs in the cloudformation template with your new docker image URLs.  

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

    $ git clone https://github.com/aws-samples/eb-java-scorekeep.git -b xray-gettingstarted
    $ cd eb-java-scorekeep

# Requirements

To run the sample application locally, you will need the following:

- AWS user with permissions - AWSCloudFormationFullAccess, AmazonDynamoDBFullAccess, AmazonSNSFullAccess, AWSXrayReadOnlyAccess
- The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
- [The AWS CLI (v2.7.9+)](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html), with the AWS user configured here.
- AWS_REGION and ACCOUNT_ID to be configured in aws.env in the root of the package
- Docker

## Get permission to use AWS Cloudformation
If you're using an IAM user with limited permissions, please, add permissions to your user account to get started.

**To add permissions to an IAM user**

1. Sign in to a user or role with administrator permissions.
2. Open the [users page](https://console.aws.amazon.com/iam/home#/users) of the IAM console.
3. Choose a user.
4. Choose **Add permissions**.
5. Add the following policies: **AWSCloudFormationFullAccess**, **AmazonDynamoDBFullAccess**, **AmazonSNSFullAccess**, **AWSXrayReadOnlyAccess**.

The CloudFormation template for this project creates resources that requires these policies. If you don't have IAM permissions, check with your account owner about getting temporary access with an IAM role.

# Running the project locally
You can run both the API and front-end locally with Docker.
Scripts are provided to create container images of the Frontend and API.

## Requirements

The `make` scripts will use the AWS_REGION (ex. us-east-1) and ACCOUNT_ID (12 digit ID) that must be configured in aws.env in the root of the package, and the default credentials from the AWS CLI.

Please configure the aws.env file:
1. Open the aws.env file in the root directory using a text editor
2. Enter the region for AWS_REGION in which your user is configured to use
3. Enter your AWS 12 digit ID for ACCOUNT_ID

## Run the Scorekeep API with Docker

The API requires DynamoDB tables and SNS topic to exist in AWS to run the API locally. If the Cloudformation resources from the [Getting Started Guide](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html) is still running, there is no need to re-create the DynamoDB and SNS resources.
Otherwise, you can use the Cloudformation template within the `run-local-resources/` directory. Within the `run-local-resources/` directory, run the following command to create the DynamoDB and SNS resources (note: this command will create the stack in the default region configured in the AWS CLI):

    aws cloudformation create-stack --stack-name scorekeep-local --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources-local.yaml

[Click here to check if the CloudFormation stack has finished creating](https://console.aws.amazon.com/cloudformation)

After the Cloudformation stack has finished creating, execute `make run-local` in the root directory to build and start the API container, listening on port 5000. You can use `docker ps` to find the container ID of the API, and use `docker attach <CONTAINER_ID>` to follow the log output of the container.

The application needs AWS credentials to communicate with DynamoDB. In ECS, Scorekeep gets credentials from the task role. When you run the application locally, we will mount in ~/.aws/ to the container where the AWS SDK for Java will retrieve credentials. You can also configure your credentials as environment variables if you prefer.

Check out the *AWS SDK for Java Developer Guide* for instructions on how to provide access keys to the application: [Set up AWS Credentials for Development](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).

### Test the API

Note - The test script requires JQuery to be installed. If JQuery is not installed, you can use `brew` to install it:

    brew install jq

Use the test script to verify that the API works.

    ./bin/test-api.sh

The script targets `localhost:5000`. However, you can point it at the API running on any hostname or IP by modifying the API variable at the top of the file.

## Run the Scorekeep Frontend with Docker

1. In the scorekeep-frontend/public/app/scorekeep.js file, comment out the `module.value('api', '/api/');` line and uncomment the `// module.value('api', 'http://localhost:5000/api/');` line. This will point the Frontend towards the API that is run locally.

2. In the scorekeep-frontend/nginx.conf file, change the port that the server listens to from 80 to 8080.

3. Within the `scorekeep-frontend/` directory, execute `make run-local` to create and start the frontend container locally. You should be able to hit the frontend locally with a web browser or curl on [localhost:8080](http://localhost:8080)

## Run the X-Ray Daemon locally (Optional)

### Prerequisites
Add the the `AWSXrayFullAccess` policy for the AWS user that is logged into the CLI to allow the X-Ray daemon to export trace data.

Add the following environment variable to the Dockerfile in the root folder to allow the Scorekeep API to send data to the X-Ray daemon on macOS and Windows:

`ENV AWS_XRAY_DAEMON_ADDRESS="host.docker.internal:2000"`

Stop the docker container that is running the API and re-run it with the modified Dockerfile.

[Follow this guide to run the X-Ray daemon locally.](https://docs.aws.amazon.com/xray/latest/devguide/xray-daemon-local.html) Once the daemon is running, the API will send segment data to the daemon, and the daemon will in turn send the data to the AWS X-Ray API. [Go to the X-Ray console to view the data on the service map!](https://console.aws.amazon.com/xray/home)

## Local Clean-up

If you have created the Cloudformation stack with the template in the `run-local-resources/` directory, delete the `scorekeep-local` stack on the [CloudFormation Console](https://console.aws.amazon.com/cloudformation/home) by selecting the stack and clicking `delete`. Undo all configuration changes made in the [Frontend setup section](##Run-the-Scorekeep-Frontend-with-Docker).

To stop the docker containers of the Frontend and API:
1. Use `docker ps` to find the container IDs of the scorekeep-api and scorekeep-frontend containers
2. Use `docker stop <API-containerId> <Frontend-containerId>` to stop the containers.
3. Use `docker images` to find the image IDs of the scorekeep-api, scorekeep-frontend, and gradle images
4. Use `docker rmi <API-imageId> <Frontend-imageId> <gradle-imageId>` to remove the docker images.

# Building your own Docker Images of Scorekeep for ECS

AWS_REGION and ACCOUNT_ID to be configured in aws.env. Make sure to undo any configuration changes made that you have done if you [had setup the frontend for running locally](##Run-the-Scorekeep-Frontend-with-Docker), and delete the `scorekeep-local` stack on CloudFormation.

If you would like to modify the project, you must build and host the docker images of scorekeep.
To test your new images on ECS, you will need to upload the images to a container image repository, and replace the image repository urls in the parameter section of the Cloudformation template.

Build your API container by executing `make package` in the root folder

Build your Frontend container by executing `make package` in the `scorekeep-frontend/` folder

You can upload your images to AWS Elastic Container Registry (ECR). Your user will need the `AmazonEC2ContainerRegistryFullAccess` policy to access it. [See how to push a Docker image to ECR here](https://docs.aws.amazon.com/AmazonECR/latest/userguide/docker-push-ecr-image.html). Alternatively, you can also use the `make publish` command in the `root directory` or the `scorekeep-frontend/` directory to publish the Frontend and API images to a private ECR. You can obtain the URLs of the uploaded docker images on the [ECR console](https://console.aws.amazon.com/ecr/repositories).

## Using your own Docker Images of Scorekeep on ECS
To use the scorekeep application with your own docker images on ECS in the [Getting Started Guide](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html), replace the image repository URLs in the parameter section of the Cloudformation template (`cloudformation/cf-resources.yaml`) with your docker image repository URLs. With this modified template, follow the instructions in the [Getting Started Guide](https://docs.aws.amazon.com/xray/latest/devguide/xray-gettingstarted.html) to deploy your modified Scorekeep images on ECS.

# How it works

## Backend
The Java application is built using the gradle Docker container so it does not rely on your local Java or Gradle setup. The output of the build process appears in the `build/` folder of the project. After you build the application you will want to package it into a docker container so it can be executed. The docker container packaging takes the JAR produced from the build step and adds it to a Java base image. It then configures the environment, ports, and entry point. The docker containers can be ran locally with valid AWS credentials, or ran on ECS.

The API runs at paths under /api that provide access to user, session, game, state, and move resources stored as JSON documents in DynamoDB. The API is RESTful, so you can create resources by sending HTTP POST requests to the resource path, for example /api/session. See the [test script](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/bin/test-api.sh) for example requests with cURL.

The Cloudformation template creates a DynamoDB table for each resource type and an SNS topic for the API to work.

## Frontend
The frontend is an Angular 1.5 web app that uses `$resource` objects to perform CRUD operations on resources defined by the API. Users first encounter the [main view](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/scorekeep-frontend/public/main.html) and [controller](https://github.com/awslabs/eb-java-scorekeep/blob/fargate/scorekeep-frontend/public/app/mainController.js) and progress through session and game views at routes that include the IDs of resources that the user creates.

The frontend is served statically by an Nginx container. The [nginx.conf](https://github.com/aws-samples/eb-java-scorekeep/blob/xray-gettingstarted/scorekeep-frontend/nginx.conf) file in the source code sets up Nginx to serve the frontend html pages from root, and forward requests starting with /api to the API backend running on port 5000. 

## X-Ray Daemon
The X-Ray daemon listens and gathers raw segment data on port 2000, and sends it towards the AWS X-Ray API.

When running Scorekeep on ECS, the X-Ray daemon runs in a container that is deployed alongside the Scorekeep application. Check out the [AWS X-Ray Developer Guide](https://docs.aws.amazon.com/xray/latest/devguide/xray-daemon.html) for more details about the X-Ray Daemon.

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
  - Portability with [Amazon ECS](http://aws.amazon.com/ecs) or CloudFormation
  - Accessibility with [Amazon Polly](https://aws.amazon.com/documentation/polly/)
- Write tests!
  - Unit tests
  - Integration tests
  - Functional tests
  - Load tests
- File an [issue](https://github.com/aws-samples/eb-java-scorekeep/issues) to report a bug or request new features.
