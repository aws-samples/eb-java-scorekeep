package scorekeep;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.servlet.Filter;
import java.lang.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.xray.AWSXRay;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@Profile("nodb")
public class WebConfig {
  private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

  @Bean
  public Filter SimpleCORSFilter() {
    return new SimpleCORSFilter();
  }

  static {

    AWSXRay.beginSegment("Scorekeep-init");
    if ( System.getenv("NOTIFICATION_EMAIL") != null ){
      try { Sns.createSubscription(); }
      catch (Exception e ) {
        logger.warn("Failed to create subscription for email "+  System.getenv("NOTIFICATION_EMAIL"));
      }
    }

    AWSXRay.endSegment();
  }
}
