exports.handler = function(event, context) {
    // Enter your user pool id here
    if(event.userPoolId === "us-east-1_AbCd12345") {
        event.response.autoConfirmUser = true;
    }
    // Return result to Cognito
    context.done(null, event);
};
