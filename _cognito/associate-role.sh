IDENTITYPOOLID=$(aws cloudformation describe-stacks --stack-name scorekeep-identitypool --query 'Stacks[0].Outputs[?ExportName==`ScorekeepIdentityPoolId`].OutputValue' --output text)
ROLEARN=$(aws cloudformation describe-stacks --stack-name scorekeep-identitypool --query 'Stacks[0].Outputs[?ExportName==`ScorekeepIdentityPoolRole`].OutputValue' --output text)
USERPOOLID=$(aws cloudformation describe-stacks --stack-name scorekeep-userpool --query 'Stacks[0].Outputs[?ExportName==`ScorekeepUserPoolId`].OutputValue' --output text)
CLIENTID=$(aws cloudformation describe-stacks --stack-name scorekeep-userpool --query 'Stacks[0].Outputs[?ExportName==`ScorekeepUserPoolClientId`].OutputValue' --output text)
REGION=$(aws cloudformation describe-stacks --stack-name scorekeep-identitypool --query 'Stacks[0].Outputs[?ExportName==`ScorekeepIdentityPoolRegion`].OutputValue' --output text)
PROVIDERID=cognito-idp.$REGION.amazonaws.com/$USERPOOLID:$CLIENTID
aws cognito-identity set-identity-pool-roles \
--identity-pool-id $IDENTITYPOOLID \
--roles authenticated=$ROLEARN \
--role-mappings "{\"$PROVIDERID\": {\"Type\": \"Token\", \"AmbiguousRoleResolution\":\"AuthenticatedRole\"}}"
