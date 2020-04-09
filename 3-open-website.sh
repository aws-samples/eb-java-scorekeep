#!/bin/bash
set -eo pipefail
ENDPOINT=$(aws cloudformation describe-stacks --stack-name scorekeep --query Stacks[0].Outputs[0].OutputValue --output text)
echo http://$ENDPOINT