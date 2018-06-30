package io.github.u2ware.sample.signin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
public class UserAccountPrincipalImpl implements UserAccountPrincipal, UserDetails {

	private @Getter @Setter String nickname;

	private @Getter @Setter String username;

	private @Getter @Setter @JsonIgnore String password;

	private @Getter @Setter @JsonIgnore boolean accountNonExpired = true;

	private @Getter @Setter @JsonIgnore boolean accountNonLocked = true;

	private @Getter @Setter @JsonIgnore boolean credentialsNonExpired = true;

	private @Getter @Setter boolean enabled = true;

	private @Getter @Setter @JsonIgnore Collection<Authority> authorities = new ArrayList<Authority>();

	public void setRoles(String... roles) {
		List<Authority> authorities = new ArrayList<Authority>();
		for (String role : roles) {
			authorities.add(new Authority(role));
		}
		getAuthorities().clear();
		getAuthorities().addAll(authorities);
	}

	public String[] getRoles() {
		List<String> authorities = new ArrayList<String>();
		for (Authority authority : getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
		String[] r = new String[authorities.size()];
		authorities.toArray(r);
		return r;
	}

	public boolean hasRoles(String... roles) {
		Collection<? extends GrantedAuthority> authorities = getAuthorities();
		for (GrantedAuthority authority : authorities) {
			for (String role : roles) {
				if (authority.getAuthority().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}

	@AllArgsConstructor
	public static class Authority implements GrantedAuthority {
		private @Getter @Setter String authority;
	}

}
