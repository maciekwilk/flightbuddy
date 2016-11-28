package com.flightbuddy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:environment.properties")
public class TestDaoConfiguration {

//	@Value("${jdbc.username}")
//	private String JDBC_USERNAME = "root";
//	@Value("${jdbc.url}")
//	private String JDBC_URL = "jdbc:mysql://localhost:3306/reisekosten_test";
//	@Value("${jdbc.driver}")
//	private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//	@Value("${jdbc.password}")
//	private String JDBC_PASSWORD = "tX37!/S";
//
//	@Bean
//	public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
//		return new PersistenceAnnotationBeanPostProcessor();
//	}
//
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//		return new PropertySourcesPlaceholderConfigurer();
//	}
//
//	@Bean
//	public RestTemplate restTemplate(){
//		return new RestTemplate();
//	}
//	
//	@Bean
//	public DataSource dataSource() throws PropertyVetoException {
//		//ComboPooledDataSource ds = new ComboPooledDataSource();
//		MysqlDataSource ds = new MysqlDataSource();
//		ds.setUrl(JDBC_URL);
//		ds.setUser(JDBC_USERNAME);
//		ds.setPassword(JDBC_PASSWORD);
//		//ds.setDriverClass(JDBC_DRIVER);
//		return ds;
//	}
//	
//	@Bean
//	public JdbcTemplate jdbcTemplate() throws PropertyVetoException{
//		return new JdbcTemplate(dataSource());
//	}
//
//	@Bean
//	public EntityManagerFactory entityManagerFactory()
//			throws PropertyVetoException {
//		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
//		factoryBean.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
//		factoryBean.setDataSource(dataSource());
//		factoryBean.setPersistenceUnitName("reisePU");
//		factoryBean.setPackagesToScan("com.nowsol");
//		factoryBean.getJpaPropertyMap().put("eclipselink.weaving", "false");
//		factoryBean.getJpaPropertyMap().put("eclipselink.logging.level.sql",
//				"FINE");
//		factoryBean.getJpaPropertyMap().put("eclipselink.logging.parameters",
//				"true");
//		factoryBean.getJpaPropertyMap().put("eclipselink.session.customizer",
//				"com.nowsol.base.UUIDSequence");
//		factoryBean.getJpaPropertyMap().put("eclipselink.ddl-generation",
//				"drop-and-create-tables");
//		factoryBean.getJpaPropertyMap().put("eclipselink.ddl-generation.output-mode",
//				"database");
//		factoryBean.afterPropertiesSet();
//		return factoryBean.getObject();
//	}
//
//	@Bean
//	public PlatformTransactionManager txManager() throws PropertyVetoException {
//		return new JpaTransactionManager(entityManagerFactory());
//	}
	
//	@Bean
//	public CustomScopeConfigurer customScopeConfigurer(){
//		CustomScopeConfigurer csc = new CustomScopeConfigurer();
//		csc.addScope("session", new SimpleThreadScope());
//		return csc;
//	}

}
