package io.github.u2ware.sample;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class ApplicationMockMvc {

	public static ApplicationMockMvc GET(String uri)  throws Exception {
		return new ApplicationMockMvc(get(uri));
	}
	public static ApplicationMockMvc POST(String uri)  throws Exception {
		return new ApplicationMockMvc(post(uri));
	}
	public static ApplicationMockMvc PUT(String uri)  throws Exception {
		return new ApplicationMockMvc(put(uri));
	}
	public static ApplicationMockMvc PATCH(String uri)  throws Exception {
		return new ApplicationMockMvc(patch(uri));
	}
	public static ApplicationMockMvc FILEUPLOAD(String uri, MockMultipartFile file)  throws Exception {
		return new ApplicationMockMvc(fileUpload(uri).file(file));
	}
	public static ApplicationMockMvc DELETE(String uri)  throws Exception {
		return new ApplicationMockMvc(delete(uri));
	}

	private static final ObjectMapper mapper = new ObjectMapper();
	private MockHttpServletRequestBuilder requestBuilder;
	private ResultActions resultActions;

	private ApplicationMockMvc(MockHttpServletRequestBuilder builder) {
		this.requestBuilder = builder;
	}

	public ApplicationMockMvc H(String auth) throws Exception{
		this.requestBuilder = requestBuilder.header("Authorization", auth);
		return this;
	}
	public ApplicationMockMvc H(ApplicationMockMvc helper) throws Exception{
		this.requestBuilder = requestBuilder.header("Authorization", helper.header());
		return this;
	}
	public ApplicationMockMvc U(String username) throws Exception{
		return U(User.withUsername(username).password(username).authorities(username).build());
	}
	public ApplicationMockMvc U(String username, String... authorities) throws Exception{
		return U(User.withUsername(username).password(username).authorities(authorities).build());
	}
	public ApplicationMockMvc U(UserDetails auth) throws Exception{
		this.requestBuilder = requestBuilder.with(user(auth));
		return this;
	}
	
	public ApplicationMockMvc P(String query) throws Exception{
		UriComponents uriComponents = UriComponentsBuilder.newInstance().query(query).build();
		return P(uriComponents.getQueryParams().toSingleValueMap());
	}
	
	public ApplicationMockMvc P(String key, Object value) throws Exception{
		this.requestBuilder = requestBuilder.param(key, (value != null) ? value.toString() : "");
		return this;
	}
	public ApplicationMockMvc P(Map<String,?> params) throws Exception{
		for(Entry<String,?> param : params.entrySet()) {
			P(param.getKey(), param.getValue());
		}
		return this;
	}
	public ApplicationMockMvc C(Map<String,?> params) throws Exception {
		this.requestBuilder = requestBuilder
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(params));
		return this;
	}
	public ApplicationMockMvc and(ResultMatcher... matchers) throws Exception {
		for(ResultMatcher matcher : matchers) {
			this.resultActions = resultActions.andExpect(matcher);
		}
		return this;
	}
	public ApplicationMockMvc and(ResultHandler... handlers) throws Exception {
		for(ResultHandler handler : handlers) {
			this.resultActions = resultActions.andDo(handler);
		}
		return this;
	}

	//////////////////////////////////////////
	//
	///////////////////////////////////////////
	public ApplicationMockMvc is2xx(MockMvc mvc) throws Exception {
		this.resultActions = mvc.perform(requestBuilder).andDo(print())
				.andExpect(status().is2xxSuccessful());
		return this;
	}
	public ApplicationMockMvc is4xx(MockMvc mvc)throws Exception {
		this.resultActions = mvc.perform(requestBuilder).andDo(print())
				.andExpect(status().is4xxClientError());
		return this;
	}
	public ApplicationMockMvc is5xx(MockMvc mvc) throws Exception {
		this.resultActions = mvc.perform(requestBuilder).andDo(print())
				.andExpect(status().is5xxServerError());
		return this;
	}

	//////////////////////////////////////////
	//
	///////////////////////////////////////////
	public String header() throws Exception {
		return resultActions.andReturn().getResponse().getHeader("Authorization");
	}
	public String body() throws Exception {
		return resultActions.andReturn().getResponse().getContentAsString();
	}
	public <T> T body(String path) throws Exception {
		return JsonPath.read(body(), path);
	}

}
