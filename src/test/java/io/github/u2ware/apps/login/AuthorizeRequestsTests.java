package io.github.u2ware.apps.login;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import io.github.u2ware.apps.ApplicationTests;

public class AuthorizeRequestsTests extends ApplicationTests{


	@Test
	public void contextLoads() throws Exception {
		super.performRead(uri(), null, status().is4xxClientError());
		super.performRead(uri(), account("aaaa"), status().is2xxSuccessful());
	}	
	
}
