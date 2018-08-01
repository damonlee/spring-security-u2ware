package io.github.u2ware.sample.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.AbstractSimpleRememberMeServices;
import org.springframework.security.web.authentication.rememberme.AbstractSimpleRememberMeSignature.PersistentRememberMeTokenSignature;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRemberMeService extends AbstractSimpleRememberMeServices {

	private PersistentTokenRepository tokenRepository;

	@Autowired
	public UserRemberMeService(UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository) {
		super(userDetailsService);
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

		if(! signature.validate(token)) {
			tokenRepository.removeUserTokens(token.getUsername());
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

		String username = super.retrieveUserName(auth);

		tokenRepository.removeUserTokens(username);
	}

}
