package com.flightbuddy.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
public class LoggingControllerInterceptor extends HandlerInterceptorAdapter {

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
