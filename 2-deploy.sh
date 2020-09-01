#!/bin/bash
set -eo pipefail
ARTIFACT_BUCKET=$(cat bucket-name.txt)
cd function
rm -rf node_modules
rm -f package-lock.json
npm install --production
cd ../
./bin/curl-agent.sh  # Downloads and unpacks the latest X-Ray agent distro
zip -q package.zip $(git ls-files)
aws cloudformation package --template-file template.yml --s3-bucket $ARTIFACT_BUCKET --output-template-file out.yml
aws cloudformation deploy --template-file out.yml --stack-name scorekeep --capabilities CAPABILITY_NAMED_IAM
