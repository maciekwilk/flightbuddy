package com.flightbuddy.config;

import static com.flightbuddy.user.UserRole.ROLE_ADMIN;
import static com.flightbuddy.user.UserRole.ROLE_USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.flightbuddy.user.authentication.CustomUserDetailsService;
import com.flightbuddy.user.authentication.JWTFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
    @Autowired CustomUserDetailsService customUserDetailsService;
		
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/register.html", "/register", "/user/register").hasAuthority(ROLE_ADMIN.name())
	      	.antMatchers("/schedule.html", "/schedule", "/search/schedule/save").hasAnyAuthority(ROLE_ADMIN.name(), ROLE_USER.name())
	      	.antMatchers("/**").permitAll()
			.and()
				.addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class)
					.antMatcher("/register.html").antMatcher("/register").antMatcher("/user/register")
					.antMatcher("/schedule.html").antMatcher("/schedule").antMatcher("/search/schedule/save")
				.httpBasic()
			.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
				.csrf()
		        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
    }
	
	@Bean
    public AuthenticationProvider authenticationProvider() throws Exception{
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setUserDetailsService(customUserDetailsService);
    	provider.setPasswordEncoder(new ShaPasswordEncoder());
    	provider.afterPropertiesSet();
    	return provider;
    }
}
