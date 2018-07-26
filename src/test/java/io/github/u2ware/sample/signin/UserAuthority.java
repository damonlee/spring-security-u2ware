package io.github.u2ware.sample.signin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

public interface UserAuthority {

	public final static String ROLE_SUPER   = "ROLE_SUPER";
	public final static String ROLE_MANAGER = "ROLE_MANAGER";
	public final static String ROLE_USER    = "ROLE_USER";

	@Target({ ElementType.METHOD, ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Documented
	@PreAuthorize("hasRole('ROLE_SUPER')")
	public @interface RoleSuperAuthorize {
	}

}
