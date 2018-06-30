package io.github.u2ware.sample.signin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserAccountPasswordEncoder implements PasswordEncoder{

	private BCryptPasswordEncoder e1 = new BCryptPasswordEncoder();
	//private StandardPasswordEncoder e2 = new StandardPasswordEncoder();
	
	@Override
	public String encode(CharSequence rawPassword) {
		return e1.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return e1.matches(rawPassword, encodedPassword);
	}

}
