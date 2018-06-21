package io.github.u2ware.apps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public @SuppressWarnings("serial") abstract class AbstractUserEntity<V extends GrantedAuthority> implements UserDetails {

	@Id
	private @Getter @Setter String username;

	@Column(length = 512, nullable = false)
	private @Getter @Setter @JsonIgnore String password;

	private @Getter @Setter String nickname;

	public abstract Collection<V> getAuthorities();

	public abstract V createAuthority(String authority);

	@Transient
	private @Getter @JsonIgnore boolean accountNonExpired = true;

	@Transient
	private @Getter @JsonIgnore boolean accountNonLocked = true;

	@Transient
	private @Getter @JsonIgnore boolean credentialsNonExpired = true;

	@Transient
	private @Getter @JsonIgnore boolean enabled = true;

	@Transient
	public void setAuthoritiesValue(String... roles) {
		List<V> authorities = new ArrayList<V>();
		for (String role : roles) {
			authorities.add(createAuthority(role));
		}
		getAuthorities().clear();
		getAuthorities().addAll(authorities);
	}

	@Transient
	public String[] getAuthoritiesValue() {
		List<String> authorities = new ArrayList<String>();
		for (V authority : getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
		String[] r = new String[authorities.size()];
		authorities.toArray(r);
		return r;
	}

}
