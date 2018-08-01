package org.springframework.security.web.authentication;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public interface AuthenticationHandler 
		extends AuthenticationSuccessHandler, 
				AuthenticationFailureHandler,
				LogoutSuccessHandler, 
				LogoutHandler,
				AccessDeniedHandler, 
				AuthenticationEntryPoint {

}
