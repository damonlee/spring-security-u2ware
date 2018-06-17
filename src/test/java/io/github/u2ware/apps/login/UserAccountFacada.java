package io.github.u2ware.apps.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.github.u2ware.apps.AbstractBaseEntity.Auditing;

@Component
public class UserAccountFacada implements AuditorAware<Auditing> {

	protected Log logger = LogFactory.getLog(getClass());

	protected Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	protected UserAccount getPrincipal() {
		return (UserAccount)getAuthentication().getPrincipal();
	}

	public String getUsername() {
		return getPrincipal().getUsername();
	}
	public String[] getRoles() {
		return getPrincipal().getRoles();
	}
	public boolean hasRoles(String... roles) {
		return getPrincipal().hasRoles(roles);
	}
	@Override
	public Auditing getCurrentAuditor() {
		
		logger.info("getCurrentAuditor");
		UserAccount a = getPrincipal();
		Auditing u = new Auditing();
		u.setUsername(a.getUsername());
		u.setDatetime(System.currentTimeMillis());
		return u;
	}
}
