package io.github.u2ware.sample.signin;

import static org.springframework.test.u2ware.RestMockMvc.GET;

import org.junit.Test;
import org.springframework.test.u2ware.security.UserDetailsDelegate;

import io.github.u2ware.sample.ApplicationTests;

public class SecurityAuthorizeRequestsTests extends ApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		GET(uri("/")).is4xx(mvc);
		GET(uri("/")).U(new UserDetailsDelegate("aaaa")).is2xx(mvc);
	}

}
