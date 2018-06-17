package org.springframework.security.web.authentication.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

public class HeaderBasedRememberMeServices extends TokenBasedRememberMeServices{

	public HeaderBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
		super.setCookieName("Authorization");
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		//RememberMeAuthenticationFilter f;
		return true;
	}

	@Override
	protected String extractRememberMeCookie(HttpServletRequest request) {
		return request.getHeader(super.getCookieName());
	}
	
	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
		String headerValue = encodeCookie(tokens);
		response.setHeader(super.getCookieName(), headerValue);
	}

	@Override
	protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
		//Do nothing...
	}
}