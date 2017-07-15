package com.flightbuddy;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
public class JSTestApplication {

	@RequestMapping("/")
	public String home() {
		return "forward:/test.html";
	}

	public static void main(String[] args) {
		Properties properties = new Properties();
    	properties.setProperty("spring.aop.proxy-target-class", "true");
    	properties.setProperty("server.port", "9999");
    	properties.setProperty("security.basic.enabled", "false");
		new SpringApplicationBuilder(JSTestApplication.class)
			.properties(properties).run(args);
	}

}