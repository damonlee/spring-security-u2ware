package io.github.u2ware.sample.signin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.support.AbstractRememberMeServices;

public class YourSsoRememberMeService extends AbstractRememberMeServices{

	protected YourSsoRememberMeService(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
	}

	@Override
	protected Authentication loadRememberMe(HttpServletRequest request, HttpServletResponse response) {
		// 3rd-party SSO 를 이용하여 로그인 여부를 확인 합니다. 
		// String username = ....
		// Authentication auth = super.createAuthentication(request, username);
		// return auth;
		return null;
	}

	@Override
	protected void saveRememberMe(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		// (필요에 따라) 로그인 성공 정보를 3rd-party SSO 에 전달합니다. 
	}

	@Override
	protected void cancelRememberMe(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		// (필요에 따라) 로그인 실패 또는 로그아웃 정보를 3rd-party SSO 에 전달합니다. 
	}

}
