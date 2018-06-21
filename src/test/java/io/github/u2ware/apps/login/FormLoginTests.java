package io.github.u2ware.apps.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import io.github.u2ware.apps.ApplicationTests;

public class FormLoginTests extends ApplicationTests{

	@Test
	public void contextLoads() throws Exception {

		this.mvc.perform(
				post("/login")
				.param("username", securityUserName)
				.param("password", securityUserPass)
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
	}	
	
}
