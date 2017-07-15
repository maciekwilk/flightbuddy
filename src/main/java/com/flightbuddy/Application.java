package com.flightbuddy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:environment.properties", 
	"file:${user.home}/flightbuddyData/environment.properties"})
public class Application {

    public static void main(String[] args) {
    	new SpringApplicationBuilder(Application.class).run(args);
    }
}
