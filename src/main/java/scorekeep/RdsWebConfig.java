package scorekeep;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.sql.DataSource;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import scorekeep.dao.gamehistory.GameHistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ElasticBeanstalkPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;

@Configuration
@EnableAutoConfiguration
@Profile("pgsql")
public class RdsWebConfig {
  private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

  @Bean
  public Filter TracingFilter() {
    return new AWSXRayServletFilter("Scorekeep");
  }

  @Bean
  public Filter SimpleCORSFilter() {
    return new SimpleCORSFilter();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    logger.info("Initializing PostgreSQL datasource");
    return DataSourceBuilder.create()
        .driverClassName("org.postgresql.Driver")
        .url("jdbc:postgresql://" + System.getenv("RDS_HOSTNAME") + ":" + System.getenv("RDS_PORT") + "/ebdb")
        .username(System.getenv("RDS_USERNAME"))
        .password(System.getenv("RDS_PASSWORD"))
        .build();
  }

  @Bean
  public GameHistoryModel gameHistoryModel() {
    return new GameHistoryModel();
  }

  @Autowired
  LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

  @PostConstruct
  public void schemaExport() {
    EntityManagerFactoryImpl entityManagerFactoryImpl = (EntityManagerFactoryImpl) localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
    SessionFactoryImplementor sessionFactoryImplementor = entityManagerFactoryImpl.getSessionFactory();
    StandardServiceRegistry standardServiceRegistry = sessionFactoryImplementor.getSessionFactoryOptions().getServiceRegistry();
    MetadataSources metadataSources = new MetadataSources(new BootstrapServiceRegistryBuilder().build());
    metadataSources.addAnnotatedClass(GameHistory.class);
    MetadataImplementor metadataImplementor = (MetadataImplementor) metadataSources.buildMetadata(standardServiceRegistry);
    SchemaExport schemaExport = new SchemaExport(standardServiceRegistry, metadataImplementor);

    AWSXRay.beginSegment("Scorekeep-init");
    schemaExport.create(true, true);
    AWSXRay.endSegment();
  }

  static {
    AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new EC2Plugin()).withPlugin(new ElasticBeanstalkPlugin());
    URL ruleFile = WebConfig.class.getResource("/sampling-rules.json");
    builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));
    AWSXRay.setGlobalRecorder(builder.build());
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
