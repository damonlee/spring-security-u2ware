package io.github.u2ware.apps.login;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.github.u2ware.apps.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class AuthorizeRequestsTests {

	protected Log logger = LogFactory.getLog(getClass());
	
	private MockMvc mvc;
	private @Autowired WebApplicationContext context;
	private @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	
	@Before
	public void setUp(){
		this.mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build() ;
	}

	@Test
	public void contextLoads() throws Exception {
		
		////////////////////////////
		//
		////////////////////////////
		this.mvc.perform(
				get(springDataRestBasePath)
			).andDo(
				print()
			).andExpect(
				status().is4xxClientError()
			);
		
		
		////////////////////////////
		//
		////////////////////////////
		this.mvc.perform(
				get("/apis")
				.with(user(newUserAccount("aaaa")))
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
		
	}	
	
	public UserAccount newUserAccount(String username, String... roles) {
		UserAccount u = new UserAccount();
		u.setUsername(username);
		u.setAuthoritiesValue(roles);
		return u;
	}
	
}
