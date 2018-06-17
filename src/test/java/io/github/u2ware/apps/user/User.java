package io.github.u2ware.apps.user;

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
import io.github.u2ware.apps.user.User.UserAuthority;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data @EqualsAndHashCode(callSuper=true) @SuppressWarnings("serial")
@Entity @Table(name="UserAccount")
public class User extends AbstractUserEntity<UserAuthority>{

	@OneToMany(mappedBy="username",fetch=FetchType.EAGER, cascade={CascadeType.ALL},orphanRemoval=true)
	private @JsonIgnore Collection<UserAuthority> authorities = new ArrayList<UserAuthority>();

	@Override
	public UserAuthority createAuthority(String authority) {
		UserAuthority auth = new UserAuthority();
		auth.setUsername(this);
		auth.setAuthority(authority);
		return auth;
	}
	
	@Data 
	@Entity @Table(name="UserAccountAuthority")
	public static class UserAuthority implements GrantedAuthority{

		private @Id @GeneratedValue Long id;
		private @ManyToOne(fetch=FetchType.LAZY) @JsonIgnore User username;
		private @NotNull String authority;
		
		public String toString(){
			return authority;
		}
	}
	

}
