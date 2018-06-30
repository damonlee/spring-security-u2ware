package io.github.u2ware.sample.signin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserAccountPrincipalDelegate implements UserAccountPrincipal, AuditorAware<String> {

	protected Log logger = LogFactory.getLog(getClass());

	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public UserAccountPrincipal getPrincipal() {
		return (UserAccountPrincipal) getAuthentication().getPrincipal();
	}

	@Override
	public String getCurrentAuditor() {
		logger.info("getCurrentAuditor");
		if (getAuthentication() != null)
			return getAuthentication().getName();
		else
			return "system";
	}

	@Override
	public String getUsername() {
		return getPrincipal().getUsername();
	}

	@Override
	public String getNickname() {
		return getPrincipal().getNickname();
	}

	@Override
	public boolean hasRoles(String... roles) {
		return getPrincipal().hasRoles(roles);
	}

}
