package org.springframework.security.web.authentication;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class UserDetailsDelegate extends User{

	public UserDetailsDelegate(String username, String... roles) {
		super(username, username, UserDetailsDelegate.authorities(roles));
	}

	private static class UserAuthority implements GrantedAuthority{
		private String authority;

		public UserAuthority(String authority) {
			this.authority = authority;
		}
		public String getAuthority() {
			return authority;
		}
		public String toString() {
			return authority;
		}
	}

	@JsonIgnore
	private static Collection<UserAuthority> authorities(String... roles){
		Collection<UserAuthority> authorities = new ArrayList<UserAuthority>();
		for(String role : roles) {
			authorities.add(new UserAuthority(role));
		}
		return authorities;
	}
}