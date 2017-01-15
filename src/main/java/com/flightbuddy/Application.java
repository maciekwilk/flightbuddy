package com.flightbuddy;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:environment.properties"})
public class Application {

    public static void main(String[] args) {
    	Properties properties = new Properties();
    	properties.setProperty("logging.level.org.apache.http.wire", "ERROR");
    	new SpringApplicationBuilder(Application.class).properties(properties).run(args);
    }
}
