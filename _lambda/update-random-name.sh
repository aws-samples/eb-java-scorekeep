#!/bin/bash
cd random-name
npm install
zip -r ../random-name.zip *
aws lambda update-function-code --function-name random-name --zip-file fileb://../random-name.zip
