package com.flightbuddy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@Controller
@PropertySource({"classpath:environment.properties", 
"file:${user.home}/flightbuddyData/environment.properties"})
public class JSTestApplication {

	@RequestMapping("/")
	public String home() {
		return "forward:/test.html";
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(JSTestApplication.class).run(args);
	}

}