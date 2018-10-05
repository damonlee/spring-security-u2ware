package io.github.u2ware.sample;

import static io.github.u2ware.sample.ApplicationMvc.GET;
import static io.github.u2ware.sample.ApplicationMvc.userDetails;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.u2ware.sample.ApplicationMvc.ApplicationMvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Value("${security.user.name:}") String freepassUsername;
	protected @Value("${security.user.password:}") String freepassPassword;
	protected @Value("${security.user.role:}") String[] freepassRoles;

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

		GET(uri("/profile")).U(userDetails("su")).is2xx(mvc);
		GET(uri("/")).U(userDetails("su")).is2xx(mvc);
		//http://localhost/apis/members{?page,size,sort}
	}

	//////////////////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////////////////
	protected final HashMap<String, ApplicationMvcResult> results = new HashMap<String, ApplicationMvcResult>();

	public ResultHandler mark(final String key) throws Exception{
		return new ResultHandler() {
			public void handle(MvcResult result) throws Exception {
				results.put(key, new ApplicationMvcResult(result));
			}
		};
	}
	protected ResultMatcher json(String path, Object value) {
		return jsonPath(path).value(value);
	}

	protected String uri(String keyDotPath) throws Exception{
		if (results.containsKey(keyDotPath)) {
			return results.get(keyDotPath).uri();
		} else {
			int idx = keyDotPath.indexOf('.');
			if(idx > -1) {
				String key = keyDotPath.substring(0, idx);
				String path = "$"+keyDotPath.substring(idx);
				return results.get(key).path(path);
			}else {
				return springDataRestBasePath + keyDotPath;
			}
		}
	}
	protected Map<String,Object> content(String key) throws Exception{
		return results.get(key).contents(objectMapper);
	}
	protected <T> T path(String keyDotPath) throws Exception{
		int idx = keyDotPath.indexOf('.');
		String key = keyDotPath.substring(0, idx);
		String path = "$"+keyDotPath.substring(idx);
		return results.get(key).path(path);
	}

}
