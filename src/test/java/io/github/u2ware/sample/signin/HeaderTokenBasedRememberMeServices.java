package io.github.u2ware.sample.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.support.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.support.RememberMeTokenSignature;
import org.springframework.security.web.authentication.rememberme.support.PersistentRememberMeTokenSignature;

public class HeaderTokenBasedRememberMeServices extends AbstractRememberMeServices {


	public HeaderTokenBasedRememberMeServices(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	protected Authentication loadRememberMe(HttpServletRequest request, HttpServletResponse response) {
		logger.info(request.getRequestURL() + " [loadRememberMe]: ");

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			return null;
		}

		RememberMeTokenSignature signature = RememberMeTokenSignature.getInstance(authorization);
		UserDetails user = getUserDetailsService().loadUserByUsername(signature.getUsername());

		if (user == null) {
			return null;
		}
		try {
			signature.validate(user);
		} catch (Exception e) {
			return null;
		}

		authorization = signature.getSignature();
		response.setHeader("Authorization", authorization);

		String username = user.getUsername();
		return createAuthentication(request, username);
	}

	@Override
	protected void saveRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.info(request.getRequestURL() + " [saveRememberMe]: ");

		UserDetails user = super.retrieveUserDetails(authentication);
		RememberMeTokenSignature signature = RememberMeTokenSignature.newInstance(user);

		String authorization = signature.getSignature();
		response.setHeader("Authorization", authorization);
	}

	@Override
	protected void cancelRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		logger.info(request.getRequestURL() + " [cancelRememberMe]: ");
		if (authentication == null)
			return;

	}

}
