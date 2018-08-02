package io.github.u2ware.sample.signin;

import static org.springframework.test.web.servlet.MockMvcHelper.GET;

import org.junit.Test;
import org.springframework.security.test.web.support.UserDetailsDelegate;

import io.github.u2ware.sample.ApplicationTests;

public class SecurityAuthorizeRequestsTests extends ApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		GET(uri("/")).is4xx(mvc);
		GET(uri("/")).U(new UserDetailsDelegate("aaaa")).is2xx(mvc);
	}

}
