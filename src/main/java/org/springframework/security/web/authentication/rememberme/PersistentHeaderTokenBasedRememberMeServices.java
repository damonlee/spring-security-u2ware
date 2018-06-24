package org.springframework.security.web.authentication.rememberme;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.StringUtils;

public class PersistentHeaderTokenBasedRememberMeServices extends PersistentTokenBasedRememberMeServices{

	private PersistentTokenRepository tokenRepository;
	private LogoutSuccessHandler logoutSuccessHandler;
	
	public PersistentHeaderTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
		this(key, userDetailsService, tokenRepository, null);
	}
	public PersistentHeaderTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository, LogoutSuccessHandler logoutSuccessHandler) {
		super(key, userDetailsService, tokenRepository);
		super.setCookieName("Authorization");
		this.tokenRepository = tokenRepository;
		this.logoutSuccessHandler = logoutSuccessHandler;
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		return true;
	}

	@Override
	protected String extractRememberMeCookie(HttpServletRequest request) {
		String headerValue = request.getHeader(super.getCookieName());
		if(StringUtils.hasLength(headerValue))
			logger.info("Remember-me cookie detecteing..."+"["+request.getMethod()+"]"+request.getRequestURL());
		return headerValue;
	}
	
	@Override
	protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
		String headerValue = encodeCookie(tokens);
		response.setHeader(super.getCookieName(), headerValue);
		logger.info("Remember-me cookie accepting..."+"["+request.getMethod()+"]"+request.getRequestURL());
	}

	@Override
	protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
		//Do nothing...
	}
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logger.info("Remember-me cookie Logout..."+"["+request.getMethod()+"]"+request.getRequestURL());
		super.logout(request, response, authentication);

		if (authentication == null) {
			PersistentRememberMeToken token = extractRememberMeToken(request);
			logger.info("Remember-me cookie Logout..."+token);

			if(token != null) {
				tokenRepository.removeUserTokens(token.getUsername());

				if(logoutSuccessHandler != null) {
					try {
						Authentication auth = new TestingAuthenticationToken(token.getUsername(), null);
						logoutSuccessHandler.onLogoutSuccess(request, response, auth);
					} catch (IOException e) {
						logger.info("Remember-me cookie Token Logout error", e);
					} catch (ServletException e) {
						logger.info("Remember-me cookie Token Logout error", e);
					}
				}
			}
		}
	}

	protected PersistentRememberMeToken extractRememberMeToken(HttpServletRequest request) {
		String extractRememberMeCookie = request.getHeader(super.getCookieName());
		if(StringUtils.hasLength(extractRememberMeCookie)) {
			String[] cookieTokens = decodeCookie(extractRememberMeCookie);
			return tokenRepository.getTokenForSeries(cookieTokens[0]);
		}
		return null;
	}

}
