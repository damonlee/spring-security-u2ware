package io.github.u2ware.apps;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.AllArgsConstructor;
import lombok.Data;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected @Value("${spring.data.rest.base-path:}") String springDataRestBasePath;
	protected @Value("${security.user.name:}") String securityUserName;
	protected @Value("${security.user.password:}") String securityUserPass;
	protected @Value("${security.user.roles:}") String[] securityUserRoles;

	protected @Autowired CrudRepository<?, ?>[] repositorirs;
	protected @Autowired ObjectMapper objectMapper;
	protected @Autowired WebApplicationContext context;
	protected MockMvc mvc;
	
	@Before
	public void before(){
		this.mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build() ;
		logger.info("----------------------------------------------------------------------------");
	}
	
	@After
	public void after(){
		logger.info("----------------------------------------------------------------------------");
		for(CrudRepository<?, ?> repository : repositorirs) {
			repository.deleteAll();
		}
	}

	//////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////
	protected String performCreate(String uri, UserDetails u, String content, ResultMatcher... matchers) throws Exception {
		if(u == null) return performCreate(uri, content, matchers);
		MvcResult r = perform(post(uri).with(user(u)).contentType(MediaType.APPLICATION_JSON_UTF8).content(content) ,  matchers);
		return JsonPath.read(r.getResponse().getContentAsString(), "$._links.self.href");
	}
	protected void performRead(String uri, UserDetails u, ResultMatcher... matchers) throws Exception {
		if(u == null) performRead(uri, matchers);
		else perform(get(uri).with(user(u)), matchers);
	}
	protected void performRead(String uri, UserDetails u, MultiValueMap<String, String> params, ResultMatcher... matchers) throws Exception {
		if(u == null) performRead(uri, params, matchers);
		else perform(get(uri).with(user(u)).params(params), matchers);
	}
	protected String performUpdate(String uri, UserDetails u, String content, ResultMatcher... matchers) throws Exception {
		if(u == null) return performUpdate(uri, content, matchers);
		MvcResult r = perform(patch(uri).with(user(u)).contentType(MediaType.APPLICATION_JSON_UTF8).content(content) ,  matchers);
		return JsonPath.read(r.getResponse().getContentAsString(), "$._links.self.href");
	}
	protected void performDelete(String uri, UserDetails u, ResultMatcher... matchers) throws Exception {
		if(u == null) performDelete(uri, matchers);
		else perform(delete(uri).with(user(u)), matchers);
	}
	private String performCreate(String uri, String content, ResultMatcher... matchers) throws Exception {
		MvcResult r = perform(post(uri).contentType(MediaType.APPLICATION_JSON_UTF8).content(content) ,  matchers);
		return JsonPath.read(r.getResponse().getContentAsString(), "$._links.self.href");
	}
	private void performRead(String uri, ResultMatcher... matchers) throws Exception {
		perform(get(uri), matchers);
	}
	private void performRead(String uri, MultiValueMap<String, String> params, ResultMatcher... matchers) throws Exception {
		perform(get(uri).params(params), matchers);
	}
	private String performUpdate(String uri, String content, ResultMatcher... matchers) throws Exception {
		MvcResult r = perform(patch(uri).contentType(MediaType.APPLICATION_JSON_UTF8).content(content) ,  matchers);
		return JsonPath.read(r.getResponse().getContentAsString(), "$._links.self.href");
	}
	private void performDelete(String uri, ResultMatcher... matchers) throws Exception {
		perform(delete(uri), matchers);
	}
	
	
	private MvcResult perform(RequestBuilder requestBuilder, ResultMatcher... matchers) throws Exception {
		ResultActions actions = this.mvc.perform(
				requestBuilder
			).andDo(
				print()
			);
		if(matchers.length == 0) {
			actions = actions.andExpect(status().is2xxSuccessful());
		}else {
			for(ResultMatcher matcher : matchers) {
				actions = actions.andExpect(matcher);
			}
		}
		return actions.andReturn();
	}	
	
	
	//////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////
	protected String uri(String... path) {
		return springDataRestBasePath+StringUtils.arrayToDelimitedString(path, "");
	}
	protected MultiValueMap<String, String> params(String params) {
		LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		String[] tokens = StringUtils.delimitedListToStringArray(params, "&");
		for(String token : tokens) {
			String[] prop = StringUtils.delimitedListToStringArray(token, "=");
			map.add(prop[0], prop[1]);
		}
		return map;
	}

	protected UserDetails account(String username, String... roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for(String role : roles) {
			authorities.add(new UserAuthority(role));
		}
		User user = new User(username, username, authorities);
		
		logger.info(user);
		logger.info(user.getPassword());
		logger.info(user.getAuthorities());

		return user;
	}

	@Data @AllArgsConstructor @SuppressWarnings("serial")
	private static class UserAuthority implements GrantedAuthority{
		private String authority;
	}
	
	
	//////////////////////////////////////////////////////
	//
	//////////////////////////////////////////////////////
	@Test
	public void contextLoads() throws Exception {
		
		this.mvc.perform(
				get("/apis")
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
		
		String [] a1 = context.getBeanNamesForType(DataSource.class);
		for(String n : a1){
			logger.info("DataSource: "+n);
		}
		String [] a3 = context.getBeanNamesForType(EntityManagerFactory.class);
		for(String n : a3){
			logger.info("EntityManagerFactory: "+n);
		}
		
		String [] a4 = context.getBeanNamesForType(AuditingHandler.class);
		for(String n : a4){
			logger.info("AuditingHandler: "+n);
		}
		
		String [] a5 = context.getBeanNamesForType(PersistentEntities.class);
		for(String n : a5){
			logger.info("PersistentEntities: "+n);
		}
		
		String [] a6 = context.getBeanNamesForType(ConversionService.class);
		for(String n : a6){
			logger.info("ConversionService: "+n);
		}
	}
}
