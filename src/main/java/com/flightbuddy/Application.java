package com.flightbuddy;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@SpringBootApplication
@PropertySource({"classpath:environment.properties"})
public class Application {

    public static void main(String[] args) {
    	Properties properties = new Properties();
    	properties.setProperty("logging.level.com.flightbuddy.interceptors.LoggingRestInterceptor", "DEBUG");
    	new SpringApplicationBuilder(Application.class).properties(properties).run(args);
    }
    
    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    	@Override
    	protected void configure(HttpSecurity http) throws Exception {
		    http
		      .httpBasic()
		    .and()
		      .authorizeRequests()
		        .antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll()
		        .anyRequest().authenticated().and()
		        .csrf()
		        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    	}
    }
}
