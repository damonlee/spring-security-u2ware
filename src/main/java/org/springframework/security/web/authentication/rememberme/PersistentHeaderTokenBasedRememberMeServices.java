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
		if (StringUtils.hasLength(headerValue)) {
			logger.info(request.getRequestURL() + " [Remember-me cookie detecteing...]: ");
		}
		return headerValue;
	}

	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
		String headerValue = encodeCookie(tokens);
		response.setHeader(super.getCookieName(), headerValue);
		logger.info(request.getRequestURL() + " [Remember-me cookie accepting...]: ");
	}

	@Override
	protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
		// Do nothing...
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Authentication auth = authentication;
		if (auth == null) {
			auth = super.autoLogin(request, response);
		}

		logger.info(request.getRequestURL() + " [Remember-me cookie deleting...]: ");
		super.logout(request, response, auth);
	}
}