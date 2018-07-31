package org.springframework.security.web.authentication.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public class AuthorizationHeaderPersistentTokenBasedRememberMeServices extends AbstractSimpleRememberMeServices{

	private PersistentTokenRepository tokenRepository;
	
	protected AuthorizationHeaderPersistentTokenBasedRememberMeServices(UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
		super(userDetailsService);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected Authentication loadRememberMe(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		String a = request.getHeader("Authorization");
		
		
		
		
		return null;
	}

	@Override
	protected void createRememberMe(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteRememberMe(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void cancelRememberMe(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}
	
}
