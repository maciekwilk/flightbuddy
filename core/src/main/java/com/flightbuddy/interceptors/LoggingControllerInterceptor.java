package com.flightbuddy.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
 
public class LoggingControllerInterceptor extends HandlerInterceptorAdapter {
    
	private static final Logger log = LoggerFactory.getLogger(LoggingControllerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		log(request.getMethod() + " " + request.getRequestURI(), response.getStatus());
	    log("Response : " + response.getStatus(), response.getStatus());
	}
   
    private void log(String message, int responseCode){
        if(responseCode / 100 > 2){
                log.error(message);
        } else{
                log.debug(message);
        }
    }
}
