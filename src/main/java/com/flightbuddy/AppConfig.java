package com.flightbuddy;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan(basePackages = {"com.flightbuddy"})
@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter {

	@Bean
	public RestTemplate restTemplate() {
	    RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
	    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
	  	interceptors.add(new LoggingRestInterceptor());
	    restTemplate.setInterceptors(interceptors);
	    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
	    return restTemplate;
	}
}
