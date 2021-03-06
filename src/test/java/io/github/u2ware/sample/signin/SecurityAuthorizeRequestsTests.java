package io.github.u2ware.sample.signin;

import static io.github.u2ware.sample.ApplicationMvc.GET;

import org.junit.Test;

import io.github.u2ware.sample.ApplicationMvc;
import io.github.u2ware.sample.ApplicationTests;

public class SecurityAuthorizeRequestsTests extends ApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		GET(uri("/")).is4xx(mvc);
		GET(uri("/")).U(ApplicationMvc.userDetails("aaaa")).is2xx(mvc);
	}

}
