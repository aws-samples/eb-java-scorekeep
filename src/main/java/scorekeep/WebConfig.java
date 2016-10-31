package scorekeep;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import javax.servlet.Filter;

@Configuration
public class WebConfig {

  @Bean
  public Filter SimpleCORSFilter() {
    return new SimpleCORSFilter();
  }
}