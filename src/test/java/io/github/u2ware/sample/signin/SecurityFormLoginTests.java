package io.github.u2ware.sample.sign;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import io.github.u2ware.sample.ApplicationTests;


public class SecurityFormLoginTests extends ApplicationTests{

	@Test
	public void contextLoads() throws Exception {

		this.mvc.perform(
				post("/login")
				.param("username", "hello")
				.param("password", "world")
			).andDo(
				print()
			).andExpect(
				status().is4xxClientError()
			);

		this.mvc.perform(
				post("/login")
				.param("username", freepassUsername)
				.param("password", freepassPassword)
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
	}	
	
}
