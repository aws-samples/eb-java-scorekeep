package scorekeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;

@Configuration
@ComponentScan
public class Application {

    enum Profile {
        NODB("nodb"),
        PGSQL("pgsql");

        private final String name;

        Profile(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static void main(String[] args) {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, getProfile());
        SpringApplication.run(Application.class, args);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static String getProfile() {
        if (isRdsEnabled()) {
            return Profile.PGSQL.toString();
        } else {
            return Profile.NODB.toString();
        }
    }

    public static boolean isRdsEnabled() {
        // Only enabled if the relevant environment variables are set
        return (null != System.getenv("RDS_HOSTNAME")
                && null != System.getenv("RDS_USERNAME")
                && null != System.getenv("RDS_PASSWORD")
                && null != System.getenv("RDS_PORT"));
    }
}