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
import io.github.u2ware.apps.login.UserAccount.UserAccountAuthority;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=false, doNotUseGetters=true)
@Entity @Table(name="UserAccount")
public class UserAccount extends AbstractUserEntity<UserAccountAuthority>{

	private static final long serialVersionUID = 6205710433137313826L;

	@OneToMany(mappedBy="username",fetch=FetchType.EAGER, cascade={CascadeType.ALL},orphanRemoval=true)
	private @JsonIgnore Collection<UserAccountAuthority> authorities = new ArrayList<UserAccountAuthority>();

	@Override
	public UserAccountAuthority createAuthority(String authority) {
		UserAccountAuthority auth = new UserAccountAuthority();
		auth.setUsername(this);
		auth.setAuthority(authority);
		return auth;
	}
	
	@Data @SuppressWarnings("serial") 
	@Entity @Table(name="UserAccountAuthority")
	public static class UserAccountAuthority implements GrantedAuthority{

		private @Id @GeneratedValue Long id;
		private @ManyToOne(fetch=FetchType.LAZY) @JsonIgnore UserAccount username;
		private @NotNull String authority;
		
		public String toString(){
			return authority;
		}
	}
	
}
