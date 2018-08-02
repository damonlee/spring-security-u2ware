package io.github.u2ware.sample;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.MockMvcHelper.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.support.UserDetailsDelegate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Value("${security.user.name:}") String freepassUsername;
	protected @Value("${security.user.password:}") String freepassPassword;
	protected @Value("${security.user.role:}") String[] freepassRoles;

	// protected @Autowired CrudRepository<?, ?>[] repositorirs;
	protected final HashMap<String, String> links = new HashMap<String, String>();
	protected final HashMap<String, Map<String,Object>> forms = new HashMap<String, Map<String,Object>>();
	protected @Autowired ObjectMapper objectMapper;
	protected @Autowired WebApplicationContext context;
	protected MockMvc mvc;

	@Before
	public void before() {
		this.mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
		logger.info("----------------------------------------------------------------------------");
	}

	@After
	public void after() {
		logger.info("----------------------------------------------------------------------------");
		// for(CrudRepository<?, ?> repository : repositorirs) {
		// repository.deleteAll();
		// }
	}

	@Test
	public void contextLoads() throws Exception {

		GET(uri("/profile")).U(new UserDetailsDelegate("su")).is2xx(mvc);
		GET(uri("/")).U(new UserDetailsDelegate("su")).is2xx(mvc);
		//http://localhost/apis/members{?page,size,sort}
	}

	protected String uri(String key) {
		if (links.containsKey(key)) {
			return links.get(key);
		} else {
			return springDataRestBasePath + key;
		}
	}
	protected Map<String,Object> content(String key) {
		return forms.get(key);
	}

	protected ResultHandler uris(final String key) {
		return new ResultHandler() {
			@Override
			public void handle(MvcResult result) throws Exception {
				String value = result.getResponse().getHeader("Location");
				if(value != null) {
					links.put(key, value);
					return;
				}
				value = JsonPath.read(result.getResponse().getContentAsString(), "$._links.self.href");
				if(value != null) {
					links.put(key, value);
					return;
				}
				value = result.getRequest().getRequestURL().toString();
				if(value != null) {
					links.put(key, value);
					return;
				}
			}
		};
	}

	protected ResultHandler contents(final String key) {
		return new ResultHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void handle(MvcResult result) throws Exception {
				String a = result.getResponse().getContentAsString();
				forms.put(key, objectMapper.readValue(a, Map.class));
			}
		};
	}

	protected ResultMatcher json(String path, Object value) {
		return jsonPath(path).value(value);
	}

}
