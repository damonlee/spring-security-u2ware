package io.github.u2ware.sample;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UserDetailsDelegate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

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

	protected String uri(String... path) {
		return springDataRestBasePath + StringUtils.arrayToDelimitedString(path, "");
	}

	@Test
	public void contextLoads() throws Exception {

		String[] a1 = context.getBeanNamesForType(DataSource.class);
		for (String n : a1) {
			logger.info("DataSource: " + n);
		}
		String[] a3 = context.getBeanNamesForType(EntityManagerFactory.class);
		for (String n : a3) {
			logger.info("EntityManagerFactory: " + n);
		}

		String[] a4 = context.getBeanNamesForType(AuditingHandler.class);
		for (String n : a4) {
			logger.info("AuditingHandler: " + n);
		}

		String[] a5 = context.getBeanNamesForType(PersistentEntities.class);
		for (String n : a5) {
			logger.info("PersistentEntities: " + n);
		}

		String[] a6 = context.getBeanNamesForType(ConversionService.class);
		for (String n : a6) {
			logger.info("ConversionService: " + n);
		}

		String[] a7 = context.getBeanNamesForType(JavaMailSender.class);
		for (String n : a7) {
			logger.info("JavaMailSender: " + n);
		}

		String[] a8 = context.getBeanNamesForType(FreeMarkerConfig.class);
		for (String n : a8) {
			logger.info("FreeMarkerConfig: " + n);
		}

		GET(uri("/profile")).AUTH(new UserDetailsDelegate("su")).RUN();
		GET(uri("/")).AUTH(new UserDetailsDelegate("su")).RUN();
		//http://localhost/apis/members{?page,size,sort}
	}

	//////////////////////////////////////////////
	//
	//////////////////////////////////////////////
	private MockHttpServletRequestBuilder request;
	private ResultActions response;

	protected ApplicationTests GET(String uri) throws Exception {
		request = MockMvcRequestBuilders.get(uri);
		response = null;
		return this;
	}

	protected ApplicationTests POST(String uri) throws Exception {
		request = MockMvcRequestBuilders.post(uri);
		response = null;
		return this;
	}

	protected ApplicationTests PUT(String uri) throws Exception {
		request = MockMvcRequestBuilders.put(uri);
		response = null;
		return this;
	}

	protected ApplicationTests PATCH(String uri) throws Exception {
		request = MockMvcRequestBuilders.patch(uri);
		response = null;
		return this;
	}

	protected ApplicationTests DELETE(String uri) throws Exception {
		request = MockMvcRequestBuilders.delete(uri);
		response = null;
		return this;
	}

	public ApplicationTests AUTH(String authorization) throws Exception {
		if (request == null)
			return this;
		if (authorization == null)
			return this;
		request = request.header("Authorization", authorization);
		return this;
	}

	public ApplicationTests AUTH(UserDetails userDetails) throws Exception {
		if (request == null)
			return this;
		if (userDetails == null)
			return this;
		request = request.with(user(userDetails));
		return this;
	}

	public ApplicationTests P(String key, String value) throws Exception {
		if (request == null)
			return this;
		if (key == null || value == null)
			return this;
		request = request.param(key, value);
		return this;
	}

	public ApplicationTests P(Map<String, Object> params) throws Exception {
		if (request == null)
			return this;
		if (params == null)
			return this;
		for(Entry<String, Object> e : params.entrySet()) {
			request = request.param(e.getKey(), e.getValue().toString());
		}
		return this;
	}

	public ApplicationTests CONTENT(String key, Object value) throws Exception{
		if (request == null)
			return this;
		if (key == null || value == null)
			return this;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		return CONTENT(map);
	}
	public ApplicationTests CONTENT(Map<String, Object> params) throws Exception{
		if (request == null)
			return this;
		if (params == null)
			return this;
		request = request.contentType(MediaType.APPLICATION_JSON_UTF8).content(objectMapper.writeValueAsString(params));
		return this;
	}

	//////////////////////////////////////////////
	//
	//////////////////////////////////////////////
	public ApplicationTests RUN(ResultMatcher... matchers) throws Exception {
		if (request == null)
			return this;
		response = mvc.perform(request).andDo(print());
		if (matchers.length == 0) {
			response = response.andExpect(status().is2xxSuccessful());
		}
		for (ResultMatcher matcher : matchers) {
			response = response.andExpect(matcher);
		}
		request = null;
		return this;
	}

	public MvcResult result() throws Exception {
		if (response == null)
			return null;
		return response.andReturn();
	}

	public String resultBody() throws Exception {
		if (response == null)
			return null;
		return response.andReturn().getResponse().getContentAsString();
	}

	public String resultAuth() throws Exception {
		if (response == null)
			return null;
		return response.andReturn().getResponse().getHeader("Authorization");
	}

	public String resultPath() throws Exception {
		if (response == null)
			return null;
		return resultPath("$._links.self.href");
	}

	public <T> T resultPath(String jsonPath) throws Exception {
		if (response == null)
			return null;
		try {
			String json = response.andReturn().getResponse().getContentAsString();
			return JsonPath.read(json, jsonPath);
		} catch (Exception e) {
			return null;
		}
	}

	public ResultMatcher is2xx() {
		return status().is2xxSuccessful();
	}

	public ResultMatcher is4xx() {
		return status().is4xxClientError();
	}

	public ResultMatcher is5xx() {
		return status().is5xxServerError();
	}

	public ResultMatcher isJson(String path, Object value) {
		return jsonPath(path).value(value);
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<T> clazz, MultiValueMap<String, Object> parameters) {
		BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
		for (PropertyDescriptor pd : beanWrapper.getPropertyDescriptors()) {
			String name = pd.getName();
			if (beanWrapper.isWritableProperty(name)) {

				Class<?> type = pd.getPropertyType();
				Object value = null;

				List<?> source = parameters.get(name);
				if (source != null) {
					if (ClassUtils.isAssignableValue(type, source) || type.isArray()) {
						value = source;
					} else {
						value = source.size() > 0 ? source.get(0) : null;
					}
				}

				if (value != null) {
					beanWrapper.setPropertyValue(name, value);
				}
			}
		}
		return (T) beanWrapper.getWrappedInstance();
	}

}
