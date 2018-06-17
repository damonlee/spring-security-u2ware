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

import lombok.Data;

@MappedSuperclass
@SuppressWarnings("serial")
@Data 
public abstract class AbstractUserEntity<V extends GrantedAuthority> implements UserDetails {

	private @Id String username;
	private @JsonIgnore @Column(length=512, nullable=false) String password;
	private @JsonIgnore @Transient boolean accountNonExpired = true;
	private @JsonIgnore @Transient boolean accountNonLocked = true;
	private @JsonIgnore @Transient boolean credentialsNonExpired = true;
	private @JsonIgnore @Transient boolean enabled = true;
	
	private String nickname;
	
	public abstract Collection<V> getAuthorities();
	public abstract V createAuthority(String authority);
	
	@Transient 
	public void setRoles(String... roles){
		List<V> authorities = new ArrayList<V>();
		for(String role : roles){
			authorities.add(createAuthority(role));
		}
		getAuthorities().clear();
		getAuthorities().addAll(authorities);
	}

	@Transient 
	public String[] getRoles(){
		List<String> authorities = new ArrayList<String>();
		for(V authority : getAuthorities()){
			authorities.add(authority.getAuthority());
		}
		String[] r = new String[authorities.size()];
		authorities.toArray(r);
		return r;
	}
	 
	public @Transient boolean hasRoles(String... roles){
		for(V authority : getAuthorities()){
			for(String role : roles) {
				if(authority.getAuthority().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
