package org.springframework.security.web.authentication.rememberme;

import java.lang.reflect.Method;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractSimpleRememberMeServices implements RememberMeServices, LogoutHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	private String key = getClass().getName();
	private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
		try {
			UserDetails user = loadRememberMe(request, response);
			userDetailsChecker.check(user);
			RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(key, user,
					authoritiesMapper.mapAuthorities(user.getAuthorities()));
			auth.setDetails(authenticationDetailsSource.buildDetails(request));
			return auth;

		} catch (CookieTheftException cte) {
			cancelRememberMe(request, response);
			throw cte;
		} catch (UsernameNotFoundException noUser) {
			logger.debug("Remember-me login was valid but corresponding user not found.", noUser);
		} catch (InvalidCookieException invalidCookie) {
			logger.debug("Invalid remember-me cookie: " + invalidCookie.getMessage());
		} catch (AccountStatusException statusInvalid) {
			logger.debug("Invalid UserDetails: " + statusInvalid.getMessage());
		} catch (RememberMeAuthenticationException e) {
			logger.debug(e.getMessage());
		}
		cancelRememberMe(request, response);
		return null;
	}

	//////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////
	@Override
	public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		logger.debug("Login of user " + (auth == null ? "Unknown" : auth.getName()));
		saveRememberMe(request, response, auth);
	}

	@Override
	public void loginFail(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Interactive login attempt was unsuccessful.");
		cancelRememberMe(request, response);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logger.debug("Logout of user " + (authentication == null ? "Unknown" : authentication.getName()));
		cancelRememberMe(request, response);
	}

	////////////////////////
	//
	////////////////////////
	protected abstract UserDetails loadRememberMe(HttpServletRequest request, HttpServletResponse response);

	protected abstract void saveRememberMe(HttpServletRequest request, HttpServletResponse response,
			Authentication successfulAuthentication);

	protected abstract void cancelRememberMe(HttpServletRequest request, HttpServletResponse response);

	////////////////////////////////////////////////////////////////////
	//
	////////////////////////////////////////////////////////////////////
	protected String retrieveUserName(Authentication authentication) {
		if (isInstanceOfUserDetails(authentication)) {
			return ((UserDetails) authentication.getPrincipal()).getUsername();
		} else {
			return authentication.getPrincipal().toString();
		}
	}

	protected String retrievePassword(Authentication authentication) {
		if (isInstanceOfUserDetails(authentication)) {
			return ((UserDetails) authentication.getPrincipal()).getPassword();
		} else {
			if (authentication.getCredentials() == null) {
				return null;
			}
			return authentication.getCredentials().toString();
		}
	}

	private boolean isInstanceOfUserDetails(Authentication authentication) {
		return authentication.getPrincipal() instanceof UserDetails;
	}

	/////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////
	protected void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
			String cookieValue, int maxAge) {
		addCookie(request, response, cookieName, cookieValue, maxAge, null, null);
	}

	protected void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
			String cookieValue, int maxAge, String cookieDomain) {
		addCookie(request, response, cookieName, cookieValue, maxAge, cookieDomain, null);
	}

	protected void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
			String cookieValue, int maxAge, String cookieDomain, Boolean useSecureCookie) {

		logger.debug("addCookie");

		Cookie cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(maxAge);
		cookie.setPath(request.getContextPath().length() > 0 ? request.getContextPath() : "/");
		if (cookieDomain != null) {
			cookie.setDomain(cookieDomain);
		}
		if (maxAge < 1) {
			cookie.setVersion(1);
		}
		cookie.setSecure(useSecureCookie == null ? request.isSecure() : useSecureCookie);

		Method setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly", boolean.class);
		if (setHttpOnlyMethod != null) {
			ReflectionUtils.invokeMethod(setHttpOnlyMethod, cookie, Boolean.TRUE);
		} else if (logger.isDebugEnabled()) {
			logger.debug(
					"Note: Cookie will not be marked as HttpOnly because you are not using Servlet 3.0 (Cookie#setHttpOnly(boolean) was not found).");
		}
		response.addCookie(cookie);
	}

	protected void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
		removeCookie(request, response, cookieName, null);
	}

	protected void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
			String cookieDomain) {

		logger.debug("removeCookie");
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(request.getContextPath().length() > 0 ? request.getContextPath() : "/");
		if (cookieDomain != null) {
			cookie.setDomain(cookieDomain);
		}
		response.addCookie(cookie);
	}

}
