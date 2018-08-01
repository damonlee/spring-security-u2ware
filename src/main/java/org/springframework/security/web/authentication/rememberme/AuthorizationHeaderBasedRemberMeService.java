package org.springframework.security.web.authentication.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.AbstractSimpleRememberMeSignature.PersistentRememberMeTokenSignature;

public class AuthorizationHeaderBasedRemberMeService extends AbstractSimpleRememberMeServices {

	private AuthorizationHeaderRepository tokenRepository;

	public AuthorizationHeaderBasedRemberMeService(String key, UserDetailsService userDetailsService, AuthorizationHeaderRepository tokenRepository) {
		super(key, userDetailsService);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected Authentication loadRememberMe(HttpServletRequest request, HttpServletResponse response) {
		logger.info(request.getRequestURL() + " [loadRememberMe]: ");

		/////////////////////////////////////////////////////
		// exists
		//////////////////////////////////////////////////////
		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			return null;
		}

		PersistentRememberMeTokenSignature signature = new PersistentRememberMeTokenSignature();
		signature.setToken(authorization);

		PersistentRememberMeToken token = tokenRepository.getTokenForSeries(signature.getSeries());
		if (token == null) {
			return null;
		}

		/////////////////////////////////////////////////////
		// 
		//////////////////////////////////////////////////////
		try {
			signature.validate(token);
		}catch(Exception e) {
			tokenRepository.removeUserTokens(token.getUsername(), e);
			return null;
		}

		/////////////////////////////////////////////////////
		// 
		//////////////////////////////////////////////////////
		PersistentRememberMeTokenSignature newSignature = new PersistentRememberMeTokenSignature();
		newSignature.setUsername(token.getUsername());
		newSignature.setSeries(token.getSeries());

		PersistentRememberMeToken newToken = newSignature.getTokenObject();
		tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());

		String newAuthorization = newSignature.getToken();
		response.setHeader("Authorization", newAuthorization);

		return createAuthentication(request, newToken.getUsername());
	}

	@Override
	protected void saveRememberMe(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		logger.info(request.getRequestURL() + " [saveRememberMe]: ");

		/////////////////////////////////////////////////////
		// new
		//////////////////////////////////////////////////////
		PersistentRememberMeTokenSignature signature = new PersistentRememberMeTokenSignature();
		signature.setUsername(super.retrieveUserName(auth));

		PersistentRememberMeToken newToken = signature.getTokenObject();
		tokenRepository.createNewToken(newToken);

		String newAuthorization = signature.getToken();
		response.setHeader("Authorization", newAuthorization);
	}

	@Override
	protected void cancelRememberMe(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		logger.info(request.getRequestURL() + " [cancelRememberMe]: ");
		if(auth == null) return;

		String username = super.retrieveUserName(auth);
		tokenRepository.removeUserTokens(username);
	}

}
