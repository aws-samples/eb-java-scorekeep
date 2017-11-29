package scorekeep;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WebConfig {
  private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

  @Bean
  public Filter SimpleCORSFilter() {
    return new SimpleCORSFilter();
  }

  static {
    if ( System.getenv("NOTIFICATION_EMAIL") != null ){
      try { Sns.createSubscription(); }
      catch (Exception e ) {
        logger.warn("Failed to create subscription for email "+  System.getenv("NOTIFICATION_EMAIL"));
      }
    }
  }
}
