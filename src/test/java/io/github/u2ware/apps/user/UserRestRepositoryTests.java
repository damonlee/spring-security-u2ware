package io.github.u2ware.apps.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class UserRestRepositoryTests {

	protected Log logger = LogFactory.getLog(getClass());
	
	private MockMvc mvc;
	private @Autowired WebApplicationContext context;
	private @Autowired UserRestRepository userRestRepository;
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
		
		
		userRestRepository.save(newUser("aa", "abcd", "aa"));
		
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
		
		this.mvc.perform(
				get("/apis/users/search/findByUsername")
				.with(user(newUser("aa", "aa")))
				.param("username", "abcd")
			).andDo(
				print()
			).andExpect(
				status().is4xxClientError()
			);
		
		this.mvc.perform(
				get("/apis/users/search/findByUsername")
				.with(user(newUser("aa", "aa", "ROLE_ADMIN")))
				.param("username", "efgh")
			).andDo(
				print()
			).andExpect(
				status().is4xxClientError()
			);

		
		this.mvc.perform(
				get("/apis/users/search/findByUsername")
				.with(user(newUser("aa", "aa", "ROLE_ADMIN")))
				.param("username", "abcd")
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
		
		this.mvc.perform(
				get("/apis/users/"+securityUserName)
				.with(user(newUser("aa", "aa", "ROLE_ADMIN")))
			).andDo(
				print()
			).andExpect(
				status().is4xxClientError()
			);
		
		this.mvc.perform(
				get("/apis/users/aa")
				.with(user(newUser("aa", "aa", "ROLE_ADMIN")))
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
	}
	
	public User newUser(String username, String nickname, String... roles) {
		User u = new User();
		u.setUsername(username);
		u.setPassword("password");
		u.setNickname(nickname);
		u.setRoles(roles);
		return u;
	}
	
	
}
