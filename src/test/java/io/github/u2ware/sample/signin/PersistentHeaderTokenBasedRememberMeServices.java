package io.github.u2ware.sample.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.support.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.support.PersistentRememberMeTokenSignature;

public class PersistentHeaderTokenBasedRememberMeServices extends AbstractRememberMeServices {

	private PersistentTokenRepository tokenRepository;

	public PersistentHeaderTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService,
			PersistentTokenRepository tokenRepository) {
		super(key, userDetailsService);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected Authentication loadRememberMe(HttpServletRequest request, HttpServletResponse response) {
		logger.info(request.getRequestURL() + " [loadRememberMe]: ");

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			return null;
		}

		PersistentRememberMeTokenSignature signature = PersistentRememberMeTokenSignature.getInstance(authorization);
		PersistentRememberMeToken token = tokenRepository.getTokenForSeries(signature.getSeries());

		if (token == null) {
			return null;
		}
		try {
			signature.validate(token);
		} catch (Exception e) {
			tokenRepository.removeUserTokens(token.getUsername());
			return null;
		}

		//////////////////////////////////////////////////////////////////////
		signature = PersistentRememberMeTokenSignature.newInstance(token);
		token = signature.getToken();
		tokenRepository.updateToken(token.getSeries(), token.getTokenValue(), token.getDate());
		/////////////////////////////////////////
		
		authorization = signature.getSignature();
		response.setHeader("Authorization", authorization);

		String username = token.getUsername();
		return createAuthentication(request, username);
	}

	@Override
	protected void saveRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.info(request.getRequestURL() + " [saveRememberMe]: ");

		String username = super.retrieveUserName(authentication);
		PersistentRememberMeTokenSignature signature = PersistentRememberMeTokenSignature.newInstance(username);

		///////////////////////////////
		PersistentRememberMeToken newToken = signature.getToken();
		tokenRepository.createNewToken(newToken);
		///////////////////////////////

		String authorization = signature.getSignature();
		response.setHeader("Authorization", authorization);
	}

	@Override
	protected void cancelRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.info(request.getRequestURL() + " [cancelRememberMe]: ");
		if (authentication == null)
			return;

		String username = super.retrieveUserName(authentication);
		tokenRepository.removeUserTokens(username);
	}

}
