package com.flightbuddy.config;

import static com.flightbuddy.user.UserRole.ROLE_ADMIN;
import static com.flightbuddy.user.UserRole.ROLE_USER;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.flightbuddy.user.authentication.CustomUserDetailsService;
import com.flightbuddy.user.authentication.JWTFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${jwt.signingkey}")
	private String SIGNING_KEY;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    private String[] securedPaths = new String[] {"/register.html", "/register", "/user/register", "/schedule.html", "/schedule",
                                          "/search/schedule/save", "/user/authenticate/token"};

    @Override
	protected void configure(HttpSecurity http) throws Exception {
		RequestMatcher requestMatcher = createRequestMatcherFromSecuredPaths();
		http			
			.authorizeRequests()
			.antMatchers("/register.html", "/register", "/user/register").hasAuthority(ROLE_ADMIN.name())
	      	.antMatchers("/schedule.html", "/schedule", "/search/schedule/save", "/user/authenticate/token").hasAnyAuthority(ROLE_ADMIN.name(), ROLE_USER.name())
	      	.antMatchers("/**").permitAll()
			.and()
				.addFilterBefore(new JWTFilter(requestMatcher, SIGNING_KEY), UsernamePasswordAuthenticationFilter.class)
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
	
	@SuppressWarnings("WeakerAccess")
    @Bean
    public AuthenticationProvider authenticationProvider() throws Exception {
    	DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    	provider.setUserDetailsService(customUserDetailsService);
    	provider.setPasswordEncoder(new BCryptPasswordEncoder());
    	provider.afterPropertiesSet();
    	return provider;
    }
	
	private RequestMatcher createRequestMatcherFromSecuredPaths() {
		List<RequestMatcher> requestMatchers = new ArrayList<>();
		for (String pathForFiltering : securedPaths) {
			requestMatchers.add(new AntPathRequestMatcher(pathForFiltering));
		}
		return new OrRequestMatcher(requestMatchers);
	}
}