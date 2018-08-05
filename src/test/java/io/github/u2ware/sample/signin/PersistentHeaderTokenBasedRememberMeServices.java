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

	public PersistentHeaderTokenBasedRememberMeServices(
			String key, 
			UserDetailsService userDetailsService,
			PersistentTokenRepository tokenRepository) {
		super(key, userDetailsService);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected Authentication loadRememberMe(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("[loadRememberMe  ]: "+request.getRequestURL());

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

		PersistentRememberMeTokenSignature newSignature = PersistentRememberMeTokenSignature.newInstance(token);
		PersistentRememberMeToken newToken = newSignature.getToken();
		tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());

		String newAuthorization = signature.getSignature();
		response.setHeader("Authorization", newAuthorization);

		String username = newToken.getUsername();
		return createAuthentication(request, username);
	}

	@Override
	protected void saveRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.debug("[saveRememberMe  ]: "+request.getRequestURL());

		String username = super.retrieveUserName(authentication);
		PersistentRememberMeTokenSignature newSignature = PersistentRememberMeTokenSignature.newInstance(username);

		PersistentRememberMeToken newToken = newSignature.getToken();
		tokenRepository.createNewToken(newToken);

		String authorization = newSignature.getSignature();
		response.setHeader("Authorization", authorization);
	}

	@Override
	protected void cancelRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.debug("[cancelRememberMe]: "+request.getRequestURL());

		Authentication auth = authentication;
		if(auth == null) {
			auth = loadRememberMe(request, response);
		}
		if (auth == null)
			return;

		String username = super.retrieveUserName(auth);
		tokenRepository.removeUserTokens(username);
	}

}
