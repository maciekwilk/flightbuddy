package com.flightbuddy.interceptors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = LoggerFactory.getLogger(LoggingRestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        traceRequest(request, body, response);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        log.debug("===========================request begin=============================");
        log.debug("URI         : {}", request.getURI());
        log.debug("Method      : {}", request.getMethod());
        HttpStatus responseStatus = response.getStatusCode();
        if (responseStatus.is4xxClientError() || responseStatus.is5xxServerError()) {
        	log.error("Headers     : {}", request.getHeaders() );
        	log.error("Request body: {}", new String(body, "UTF-8"));
        }
        log.debug("==========================request end==============================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = putResponseIntoInputStream(response);
        log.debug("============================response begin==========================================");
        log.debug("Status code  : {}", response.getStatusCode());
        log.debug("Status text  : {}", response.getStatusText());
        HttpStatus responseStatus = response.getStatusCode();
        if (responseStatus.is4xxClientError() || responseStatus.is5xxServerError()) {
        	log.error("Headers      : {}", response.getHeaders());
        	log.error("Response body: {}", inputStringBuilder.toString());
        }
        log.debug("=======================response end=================================================");
    }

	private StringBuilder putResponseIntoInputStream(ClientHttpResponse response) throws UnsupportedEncodingException, IOException {
		StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
		return inputStringBuilder;
	}

}
