package org.springframework.security.web.authentication.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

public class PersistentHeaderTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices {

	public PersistentHeaderTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService,
			PersistentTokenRepository tokenRepository) {
		super(key, userDetailsService, tokenRepository);
		super.setCookieName("Authorization");
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		return true;
	}

	@Override
	protected String extractRememberMeCookie(HttpServletRequest request) {
		String headerValue = request.getHeader(super.getCookieName());
		if (StringUtils.hasLength(headerValue))
			logger.info("Remember-me cookie detecteing..." + "[" + request.getMethod() + "]" + request.getRequestURL());
		return headerValue;
	}

	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
		String headerValue = encodeCookie(tokens);
		response.setHeader(super.getCookieName(), headerValue);
		logger.info("Remember-me cookie accepting..." + "[" + request.getMethod() + "]" + request.getRequestURL());
	}

	@Override
	protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
		// Do nothing...
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logger.info("Remember-me cookie Logout..." + "[" + request.getMethod() + "]" + request.getRequestURL());
		Authentication auth = authentication;
		if (auth == null) {
			auth = super.autoLogin(request, response);
		}
		super.logout(request, response, auth);
	}
}