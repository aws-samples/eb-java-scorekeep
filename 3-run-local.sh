export AWS_REGION=ca-central-1
export NOTIFICATION_TOPIC=$(aws cloudformation describe-stack-resource --stack-name scorekeep --logical-resource-id notificationTopic --query 'StackResourceDetail.PhysicalResourceId' --output text)

./gradlew bootrun