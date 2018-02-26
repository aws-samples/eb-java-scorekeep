include ../aws.env

.PHONY: check-region stack clean

check-region:
	test -n "$(AWS_REGION)" || (echo "AWS_REGION must be defined in ../aws.env"; exit 1)

stack: check-region
	aws --region $(AWS_REGION) cloudformation create-stack --stack-name scorekeep --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources.yaml

update: check-region
	aws --region $(AWS_REGION) cloudformation update-stack --stack-name scorekeep --capabilities "CAPABILITY_NAMED_IAM" --template-body file://cf-resources.yaml

check: check-region
	aws --region $(AWS_REGION) cloudformation describe-stack-events --stack-name scorekeep --max-items 4

clean: check-region
	aws --region $(AWS_REGION) cloudformation delete-stack --stack-name scorekeep
