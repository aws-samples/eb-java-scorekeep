package scorekeep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class Application {

    enum Profile {
        MYSQL("mysql"),
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

    public static String getProfile() {
        if (null != System.getenv("RDS_HOSTNAME") && null != System.getenv("RDS_USERNAME") && null != System.getenv("RDS_PASSWORD") && null != System.getenv("RDS_PORT")) {
            return Profile.PGSQL.toString();
        } else {
            return Profile.MYSQL.toString();
        }
    }
}