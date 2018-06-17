package io.github.u2ware.apps.login;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;

@SuppressWarnings("deprecation")
@Component
public class UserPasswordEncoder extends ShaPasswordEncoder {

	public UserPasswordEncoder(){
		super(256);
	}
	
	public String encode(String rawPass) {
		return super.encodePassword(rawPass, null);
	}
}
