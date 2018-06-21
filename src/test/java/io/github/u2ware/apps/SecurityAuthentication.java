package io.github.u2ware.apps;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuthentication implements AuditorAware<AbstractBaseEntity.Auditing> {

	protected Log logger = LogFactory.getLog(getClass());

	protected Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	protected AbstractUserEntity<?> getPrincipal() {
		return (AbstractUserEntity<?>)getAuthentication().getPrincipal();
	}

	public String getUsername() {
		return getPrincipal().getUsername();
	}

	public boolean hasRoles(SecurityAuthority... roles) {
		Collection<? extends GrantedAuthority> authorities = getPrincipal().getAuthorities();
		for( GrantedAuthority authority : authorities){
			for(SecurityAuthority role : roles) {
				if(authority.getAuthority().equals(role.name())) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public AbstractBaseEntity.Auditing getCurrentAuditor() {
		
		logger.info("getCurrentAuditor");
		AbstractUserEntity<?> a = getPrincipal();
		AbstractBaseEntity.Auditing u = new AbstractBaseEntity.Auditing();
		u.setUsername(a.getUsername());
		u.setDatetime(System.currentTimeMillis());
		return u;
	}
}
