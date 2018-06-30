package io.github.u2ware.sample.sign;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UserDetailsDelegate;

import io.github.u2ware.sample.ApplicationTests;

public class SecurityAuthorizeRequestsTests extends ApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		GET(uri()).AUTH((UserDetails) null).RUN(is4xx());
		GET(uri()).AUTH(new UserDetailsDelegate("aaaa")).RUN(is2xx());
	}

}
