package com.flightbuddy;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:environment.properties", 
	"file:${user.home}/flightbuddyData/environment.properties"})
public class Application {

    public static void main(String[] args) {
    	Properties properties = new Properties();
    	properties.setProperty("logging.level.com.flightbuddy.interceptors.LoggingRestInterceptor", "DEBUG");
    	properties.setProperty("spring.aop.proxy-target-class", "true");
    	properties.setProperty("spring.jackson.serialization.write_dates_as_timestamps", "false");
    	properties.setProperty("logging.file", "${user.home}/flightbuddy/logs/console.log");
    	new SpringApplicationBuilder(Application.class).properties(properties).run(args);
    }
}
