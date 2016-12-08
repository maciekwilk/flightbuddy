package com.flightbuddy.db;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.googlecode.flyway.core.Flyway;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@ComponentScan(basePackages = {"com.flightbuddy"})
@PropertySource({"classpath:environment.properties"})
@EnableTransactionManagement
@EnableScheduling
public class DaoConfiguration {

	@Value("${jdbc.username}")
	private String JDBC_USERNAME;
	@Value("${jdbc.url}")
	private String JDBC_URL;
	@Value("${jdbc.driver}")
	private String JDBC_DRIVER;
	@Value("${jdbc.password}")
	private String JDBC_PASSWORD;
	@Value("${flyway.init_version}")
	private String FLYWAY_INIT_VERSION;
	@Value("${database.logging.enabled}")
	private boolean databaseLoggingEnabled;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public Flyway flyway() throws PropertyVetoException, SQLException{
		Flyway flyway = new Flyway();
		flyway.setDataSource(dataSource());
		flyway.setInitOnMigrate(true);
		flyway.setInitVersion(FLYWAY_INIT_VERSION);
		flyway.getPlaceholders().put("owner", JDBC_USERNAME);
		flyway.migrate();
		return flyway;
	}
	
	@Bean
	public DataSource dataSource() throws PropertyVetoException, SQLException{
		ComboPooledDataSource ds = new ComboPooledDataSource();
		ds.setJdbcUrl(JDBC_URL);
		ds.setUser(JDBC_USERNAME);
		ds.setPassword(JDBC_PASSWORD);
		ds.setDriverClass(JDBC_DRIVER);
		ds.setIdleConnectionTestPeriod(300);
		ds.setMinPoolSize(3);
		ds.setMaxPoolSize(20);
		ds.setMaxStatements(0);
		ds.setLoginTimeout(300);
		return ds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() throws PropertyVetoException, SQLException{
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory() throws PropertyVetoException, SQLException{
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
		factoryBean.setDataSource(dataSource());
		factoryBean.setPersistenceUnitName("reisePU");
		factoryBean.setPackagesToScan("com.flightbuddy");
		factoryBean.getJpaPropertyMap().put("eclipselink.weaving", "false");
		factoryBean.getJpaPropertyMap().put("eclipselink.ddl-generation", "none"); // flyway
		factoryBean.getJpaPropertyMap().put("eclipselink.session.customizer", "com.flightbuddy.db.UUIDSequence");
		if(databaseLoggingEnabled){
//			factoryBean.getJpaPropertyMap().put("eclipselink.logging.level.sql", "FINE");
//			factoryBean.getJpaPropertyMap().put("eclipselink.logging.parameters", "true");
		}
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

    @Bean
    public PlatformTransactionManager txManager() throws PropertyVetoException, SQLException {
        return new JpaTransactionManager(entityManagerFactory());
    }
}
