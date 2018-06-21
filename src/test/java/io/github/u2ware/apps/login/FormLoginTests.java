package io.github.u2ware.apps.login;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class SecurityFormLoginTests {

	protected Log logger = LogFactory.getLog(getClass());
	
	private MockMvc mvc;
	private @Autowired WebApplicationContext context;
	private @Value("${security.user.name:}") String securityUserName;
	private @Value("${security.user.password:}") String securityUserPass;

	
	@Before
	public void setUp(){
		this.mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build() ;
	}

	@Test
	public void contextLoads() throws Exception {
		
		/////////////////////////////
		//
		/////////////////////////////
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
