exports.handler = function(event, context) {
    // Enter your user pool id here
    if(event.userPoolId === process.env.USERPOOL_ID) {
        event.response.autoConfirmUser = true;
    }
    // Return result to Cognito
    context.done(null, event);
};
