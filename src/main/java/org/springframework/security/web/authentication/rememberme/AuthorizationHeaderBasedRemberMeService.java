package org.springframework.security.web.authentication.rememberme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public class AuthorizationHeaderBasedRemberMeService extends AbstractSimpleRememberMeServices {

	private AuthorizationHeaderRepository tokenRepository;

	public AuthorizationHeaderBasedRemberMeService(String key, 
			UserDetailsService userDetailsService, AuthorizationHeaderRepository tokenRepository) {
		super(key, userDetailsService);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected Authentication loadRememberMe(
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(request.getRequestURL() + " [loadRememberMe]: ");

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			return null;
		}

		PersistentRememberMeTokenSignature signature = new PersistentRememberMeTokenSignature();
		signature.setSignature(authorization);

		PersistentRememberMeToken token = tokenRepository.getTokenForSeries(signature.getSeries());
		if (token == null) {
			return null;
		}

		try {
			signature.validate(token);
		}catch(Exception e) {
			tokenRepository.removeUserTokens(token.getUsername(), e);
			return null;
		}

		PersistentRememberMeTokenSignature newSignature = new PersistentRememberMeTokenSignature();
		newSignature.setUsername(token.getUsername());
		newSignature.setSeries(token.getSeries());

		PersistentRememberMeToken newToken = newSignature.getToken();
		tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());

		String newAuthorization = newSignature.getSignature();
		response.setHeader("Authorization", newAuthorization);

		return createAuthentication(request, newToken.getUsername());
	}

	@Override
	protected void saveRememberMe(
			HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logger.info(request.getRequestURL() + " [saveRememberMe]: ");

		PersistentRememberMeTokenSignature signature = new PersistentRememberMeTokenSignature();
		signature.setUsername(super.retrieveUserName(authentication));

		PersistentRememberMeToken newToken = signature.getToken();
		tokenRepository.createNewToken(newToken);

		String newAuthorization = signature.getSignature();
		response.setHeader("Authorization", newAuthorization);
	}

	@Override
	protected void cancelRememberMe(
			HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logger.info(request.getRequestURL() + " [cancelRememberMe]: ");
		if(authentication == null) return;

		String username = super.retrieveUserName(authentication);
		tokenRepository.removeUserTokens(username);
	}

}
