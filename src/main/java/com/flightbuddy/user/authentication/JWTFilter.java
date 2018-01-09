package com.flightbuddy.user.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JWTFilter extends GenericFilterBean {
	
	private static final String AUTHORITIES_KEY = "roles";
	
	private static final String COOKIE_KEY = "JWT-TOKEN";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String SET_COOKIE_HEADER = "Set-Cookie";
	private final RequestMatcher securedPaths;
	private final String signingKey;

	public JWTFilter(RequestMatcher securedPaths, String signingKey) {
		this.securedPaths = securedPaths;
		this.signingKey = signingKey;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String token = getAuthenticationToken(request);
		if (!token.isEmpty()) {
			setAuthenticationInSpringContext(response, request, token);
			response.addHeader(SET_COOKIE_HEADER, COOKIE_KEY + "=" + token + ";path=/");
		} else if (securedPaths.matches(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No Authentication token found.");
            return;
        }
        filterChain.doFilter(request, response);
	}

	private String getAuthenticationToken(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
			return authHeader.substring(7);
		}
		Optional<String> token = getJwtTokenFromCookies(request);
        return token.orElse("");
    }

	private Optional<String> getJwtTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return Optional.empty();
		}
		return Arrays.stream(cookies)
				.filter(cookie -> cookie.getName() != null)
				.filter(cookie -> cookie.getName().equals(COOKIE_KEY))
				.map(Cookie::getValue)
				.findFirst();
	}

	private void setAuthenticationInSpringContext(HttpServletResponse response, HttpServletRequest request, String token) throws IOException {
		try {
			Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
			request.setAttribute("claims", claims);
			SecurityContextHolder.getContext().setAuthentication(getAuthentication(claims));
		} catch (SignatureException e) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		}
	}

	@SuppressWarnings("unchecked")
	private Authentication getAuthentication(Claims claims) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		List<String> roles = (List<String>) claims.get(AUTHORITIES_KEY);
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		User principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}
}