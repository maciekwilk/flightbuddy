package com.flightbuddy.user.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Component
public class JWTFilter extends GenericFilterBean {

	@Value("${jwt.signingkey}")
	private String SIGNING_KEY;
	
	public static final String AUTHORITIES_KEY = "roles";
	
	private static final String COOKIE_KEY = "JWT-TOKEN";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String SET_COOKIE_HEADER = "Set-Cookie";
	private RequestMatcher pathsForFiltering;
	
	public void setPathsForFiltering(RequestMatcher pathsForFiltering) {
		this.pathsForFiltering = pathsForFiltering;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		if (pathsForFiltering.matches(request)) {
			performAuthentication(res, filterChain, request);
		} else {
			filterChain.doFilter(req, res);
		}
	}

	private void performAuthentication(ServletResponse res, FilterChain filterChain, HttpServletRequest request) throws IOException, ServletException {
		String authHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
			String token = authHeader.substring(7);
			setAuthenticationAndContinueTheChain(res, filterChain, request, token);
		} else {
			Optional<String> token = getJwtTokenFromCookies(request);
			if (token.isPresent()) {
				setAuthenticationAndContinueTheChain(res, filterChain, request, token.get());
			} else {
				((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Authorization header.");
			}
		}
	}

	private void setAuthenticationAndContinueTheChain(ServletResponse res, FilterChain filterChain, HttpServletRequest request, 
			String token) throws IOException, ServletException {
		try {
			setAuthenticationInContext(filterChain, request, token);
			HttpServletResponse response = addCookie(res, token);
			filterChain.doFilter(request, response);
		} catch (SignatureException e) {
			((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		}
	}

	private void setAuthenticationInContext(FilterChain filterChain, HttpServletRequest request, String token) throws IOException, 
			ServletException {
		Claims claims = Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
		request.setAttribute("claims", claims);
		SecurityContextHolder.getContext().setAuthentication(getAuthentication(claims));
	}

	@SuppressWarnings("unchecked")
	public Authentication getAuthentication(Claims claims) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		List<String> roles = (List<String>) claims.get(AUTHORITIES_KEY);
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		User principal = new User(claims.getSubject(), "", authorities);
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				principal, "", authorities);
		return usernamePasswordAuthenticationToken;
	}

	private HttpServletResponse addCookie(ServletResponse res, String token) {
		HttpServletResponse response = (HttpServletResponse) res;
		response.addHeader(SET_COOKIE_HEADER, COOKIE_KEY + "=" + token);
		return response;
	}

	private Optional<String> getJwtTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		return Arrays.stream(cookies)
					.filter(cookie -> cookie.getName() != null)
					.filter(cookie -> cookie.getName().equals(COOKIE_KEY))
					.map(cookie -> cookie.getValue())
					.findFirst();
	}
}