package scorekeep;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(value="/api/userpool")
public class UserPoolController {
  private static final Logger logger = LoggerFactory.getLogger("UserPoolController");

  /* GET /userpool */
  @RequestMapping(method=RequestMethod.GET)
  public UserPool getUserPool() {
    UserPool userpool = new UserPool();
    userpool.setPoolId(System.getenv("USERPOOL_ID"));
    userpool.setClientId(System.getenv("USERPOOL_CLIENT_ID"));
    userpool.setIdentityPoolId(System.getenv("IDENTITYPOOL_ID"));
    userpool.setRegion(System.getenv("AWS_REGION"));
    logger.info("cognito pool ID: " + userpool.getPoolId());
    logger.info("cognito client ID: " + userpool.getClientId());
    logger.info("cognito identity pool ID: " + userpool.getIdentityPoolId());
    return userpool;
  }
}
