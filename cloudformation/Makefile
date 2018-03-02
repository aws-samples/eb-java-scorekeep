include ../aws.env

.PHONY: check-region stack clean

check-region:
	@test -n "$(AWS_REGION)" || (echo "AWS_REGION must be defined in ../aws.env"; exit 1)

stack: check-region
	aws --region $(AWS_REGION) cloudformation create-stack --stack-name scorekeep --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources.yaml

update: check-region
	aws --region $(AWS_REGION) cloudformation update-stack --stack-name scorekeep --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources.yaml

check: check-region
	aws --region $(AWS_REGION) cloudformation describe-stack-events --stack-name scorekeep --max-items 4

outputs: check-region
	@echo "\nVPC"
	@aws --region $(AWS_REGION) cloudformation describe-stacks --stack-name scorekeep --query 'Stacks[0].Outputs[?OutputKey==`PubPrivateVPCID`].OutputValue' --output text
	@echo "\nPrivate subnets"
	@aws --region $(AWS_REGION) cloudformation describe-stacks --stack-name scorekeep --query 'Stacks[0].Outputs[?OutputKey==`PrivateSubnet1ID`].OutputValue' --output text
	@aws --region $(AWS_REGION) cloudformation describe-stacks --stack-name scorekeep --query 'Stacks[0].Outputs[?OutputKey==`PrivateSubnet2ID`].OutputValue' --output text
	@echo "\nPublic subnets"
	@aws --region $(AWS_REGION) cloudformation describe-stacks --stack-name scorekeep --query 'Stacks[0].Outputs[?OutputKey==`PublicSubnet1ID`].OutputValue' --output text
	@aws --region $(AWS_REGION) cloudformation describe-stacks --stack-name scorekeep --query 'Stacks[0].Outputs[?OutputKey==`PublicSubnet2ID`].OutputValue' --output text

clean: check-region
	aws --region $(AWS_REGION) cloudformation delete-stack --stack-name scorekeep
