package io.github.u2ware.apps.login;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.u2ware.apps.AbstractUserEntity;
import io.github.u2ware.apps.login.UserAccount.Authority;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserAccount")
public @SuppressWarnings("serial") class UserAccount extends AbstractUserEntity<Authority> {

	@OneToMany(mappedBy = "username", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	private @Getter @Setter @JsonIgnore Collection<Authority> authorities = new ArrayList<Authority>();

	@Override
	public Authority createAuthority(String authority) {
		Authority auth = new Authority();
		auth.setUsername(this);
		auth.setAuthority(authority);
		return auth;
	}

	@Entity
	@Table(name = "UserAccountAuthority")
	public static class Authority implements GrantedAuthority {

		@Id
		@GeneratedValue
		private @Getter @Setter Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private @Getter @Setter @JsonIgnore UserAccount username;

		@NotNull
		private @Getter @Setter String authority;

		public String toString() {
			return authority;
		}
	}

}
