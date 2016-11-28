package com.flightbuddy;

import java.io.IOException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
 
public class LoggingRestInterceptor implements ClientHttpRequestInterceptor{
    
	private static final Logger log = LoggerFactory.getLogger(LoggingRestInterceptor.class);
       
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        log(request,body,response);
        return response;
    }
 
    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        log(request.getMethod() + " " + request.getURI(), response.getRawStatusCode());
        log("Request headers : " + request.getHeaders(), response.getRawStatusCode());
        log("Request body : " + new String(body, Charsets.UTF_8), response.getRawStatusCode());
	    log("Response : " + response.getStatusCode() + " " + response.getStatusText(), response.getRawStatusCode());
        log("Response headers : " + response.getHeaders(), response.getRawStatusCode());
       
        if(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()){
                log.error("Response body : " + IOUtils.toString(response.getBody()));               
        }
    }
   
    private void log(String message, int responseCode){
        if(responseCode / 100 > 2){
                log.error(message);
        } else{
                log.debug(message);
        }
    }
}
