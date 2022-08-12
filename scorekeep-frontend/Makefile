include ../aws.env
include ecr.env

.PHONY: package tag login publish check-region check-repo check-account check-env

package:
	docker build -t scorekeep-frontend .

check-repo:
	test -n "$(ECR_REPO)" || (echo "ECR_REPO must be defined in ecr.env"; exit 1)

check-account:
	test -n "$(ACCOUNT_ID)" || (echo "ACCOUNT_ID must be defined in ../aws.env"; exit 1)

check-region:
	test -n "$(AWS_REGION)" || (echo "AWS_REGION must be defined in ../aws.env"; exit 1)

check-env: check-repo check-region check-account

run-local: package check-env
	docker run -d -p 8080:8080 scorekeep-frontend

tag: package check-env
	docker tag scorekeep-frontend:latest $(ECR_REPO)

login: check-region
	aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin $(ACCOUNT_ID).dkr.ecr.$(AWS_REGION).amazonaws.com

publish: tag login check-env
	docker push $(ECR_REPO)
