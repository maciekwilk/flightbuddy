package com.flightbuddy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
class DatabaseConfig {

	@Value("${jdbc.username}")
	private String JDBC_USERNAME;
	@Value("${jdbc.url}")
	private String JDBC_URL;
	@Value("${jdbc.driver}")
	private String JDBC_DRIVER;
	@Value("${jdbc.password}")
	private String JDBC_PASSWORD;
	@Value("${jdbc.logging.enable}")
	private boolean loggingEnabled;
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(JDBC_URL);
		dataSource.setUsername(JDBC_USERNAME);
		dataSource.setPassword(JDBC_PASSWORD);
		dataSource.setDriverClassName(JDBC_DRIVER);
		return dataSource;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factoryBean.setDataSource(dataSource());
		factoryBean.setPackagesToScan("com.flightbuddy");
		Properties additionalProperties = new Properties();
	    additionalProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
	    additionalProperties.put("hibernate.hbm2ddl.auto", "none");
	    if (loggingEnabled) {
		    additionalProperties.put("hibernate.show_sql", "true");
    		additionalProperties.put("hibernate.type", "trace");
	    }
	    factoryBean.setJpaProperties(additionalProperties);
		return factoryBean;
	}

    @Bean
    public PlatformTransactionManager txManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }
}
