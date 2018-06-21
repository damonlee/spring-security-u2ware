package io.github.u2ware.apps.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import io.github.u2ware.apps.ApplicationTests;

public class RememberMeTests extends ApplicationTests{

	@Test
	public void contextLoads() throws Exception {
		
		/////////////////////////////
		//
		/////////////////////////////
		MvcResult r0 = this.mvc.perform(
				post("/login")
				.param("username", securityUserName)
				.param("password", securityUserPass)
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			).andReturn();

		
		/////////////////////////////
		//
		/////////////////////////////
		this.mvc.perform(
				get(uri("/profile"))
			).andDo(
				print()
			).andExpect(
				status().is4xxClientError()
			);
		
		MvcResult r1 = this.mvc.perform(
				get(uri("/profile"))
				.header("Authorization", r0.getResponse().getHeader("Authorization"))
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			).andReturn();

		this.mvc.perform(
				get(uri("/profile"))
				.header("Authorization", r1.getResponse().getHeader("Authorization"))
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);


		try {
			this.mvc.perform(
					get(uri("/profile"))
					.header("Authorization", r1.getResponse().getHeader("Authorization"))
				).andDo(
					print()
				).andExpect(
					status().is4xxClientError()
				);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	
}
