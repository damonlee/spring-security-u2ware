package org.springframework.security.web.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceDelegate implements UserDetailsService{

	
	private UserDetailsService[] userDetailsServices;
	
	public UserDetailsServiceDelegate(UserDetailsService... userDetailsServices ) {
		this.userDetailsServices = userDetailsServices;
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		for(UserDetailsService userDetailsService : userDetailsServices) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if(userDetails != null) return userDetails;
		}
		return null;
	}

}
