package org.springframework.security.web.authentication.rememberme.support;

import org.springframework.security.web.authentication.logout.LogoutHandler;

public interface RememberMeServices 
extends org.springframework.security.web.authentication.RememberMeServices , LogoutHandler{

	public String getKey();
}
