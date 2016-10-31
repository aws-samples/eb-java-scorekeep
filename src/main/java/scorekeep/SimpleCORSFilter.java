package scorekeep;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class SimpleCORSFilter extends CorsFilter {

  public SimpleCORSFilter() {
    super(configurationSource());
  }

  private static UrlBasedCorsConfigurationSource configurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    // Modify allowed origins if you run the client at a different endpoint
    config.addAllowedOrigin("http://localhost:8080");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}