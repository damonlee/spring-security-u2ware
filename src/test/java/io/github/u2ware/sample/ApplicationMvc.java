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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class ApplicationMvc {

	public static ApplicationMvc GET(String uri) throws Exception {
		return new ApplicationMvc(get(uri));
	}

	public static ApplicationMvc POST(String uri) throws Exception {
		return new ApplicationMvc(post(uri));
	}

	public static ApplicationMvc PUT(String uri) throws Exception {
		return new ApplicationMvc(put(uri));
	}

	public static ApplicationMvc PATCH(String uri) throws Exception {
		return new ApplicationMvc(patch(uri));
	}

	public static ApplicationMvc FILEUPLOAD(String uri, MockMultipartFile file) throws Exception {
		return new ApplicationMvc(fileUpload(uri).file(file));
	}

	public static ApplicationMvc DELETE(String uri) throws Exception {
		return new ApplicationMvc(delete(uri));
	}

	private static final ObjectMapper mapper = new ObjectMapper();
	private MockHttpServletRequestBuilder requestBuilder;
	private ResultActions resultActions;

	private ApplicationMvc(MockHttpServletRequestBuilder builder) {
		this.requestBuilder = builder;
	}

	//////////////////////////////////////////////////////
	// Request Header /  Parameter / Content
	///////////////////////////////////////////////////////
	public ApplicationMvc H(String key, String value) throws Exception {
		this.requestBuilder = requestBuilder.header(key, value);
		return this;
	}
	
	public ApplicationMvc P(String key, Object value) throws Exception {
		this.requestBuilder = requestBuilder.param(key, (value != null) ? value.toString() : "");
		return this;
	}

	public ApplicationMvc P(Map<String, ?> params) throws Exception {
		for (Entry<String, ?> param : params.entrySet()) {
			P(param.getKey(), param.getValue());
		}
		return this;
	}

	public ApplicationMvc P(String query) throws Exception {
		UriComponents uriComponents = UriComponentsBuilder.newInstance().query(query).build();
		return P(uriComponents.getQueryParams().toSingleValueMap());
	}

	public ApplicationMvc C(Map<String, ?> params) throws Exception {
		this.requestBuilder = requestBuilder.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(mapper.writeValueAsString(params));
		return this;
	}

	//////////////////////////////////////////////////////
	// security....
	///////////////////////////////////////////////////////
	public static UserDetails userDetails() throws Exception{
		return userDetails("User"+System.currentTimeMillis());
	}
	public static UserDetails userDetails(String key) throws Exception{
		return User.withUsername(key).password(key).authorities(key).build();
	}
	public static UserDetails userDetails(String username, String... roles) {
		return User.withUsername(username).password(username).authorities(roles).build();
	}

	public ApplicationMvc U(UserDetails auth) throws Exception{
		this.requestBuilder = requestBuilder.with(user(auth));
		return this;
	}

	//////////////////////////////////////////
	// actions
	///////////////////////////////////////////
	public ApplicationMvc is2xx(MockMvc mvc) throws Exception {
		this.resultActions = mvc.perform(requestBuilder).andDo(print()).andExpect(status().is2xxSuccessful());
		return this;
	}

	public ApplicationMvc is4xx(MockMvc mvc) throws Exception {
		this.resultActions = mvc.perform(requestBuilder).andDo(print()).andExpect(status().is4xxClientError());
		return this;
	}

	public ApplicationMvc is5xx(MockMvc mvc) throws Exception {
		this.resultActions = mvc.perform(requestBuilder).andDo(print()).andExpect(status().is5xxServerError());
		return this;
	}

	//////////////////////////////////////////
	// response handle
	///////////////////////////////////////////
	public ApplicationMvc and(ResultMatcher... matchers) throws Exception {
		for (ResultMatcher matcher : matchers) {
			this.resultActions = resultActions.andExpect(matcher);
		}
		return this;
	}

	public ApplicationMvc and(ResultHandler... handlers) throws Exception {
		for (ResultHandler handler : handlers) {
			this.resultActions = resultActions.andDo(handler);
		}
		return this;
	}

	//////////////////////////////////////////
	//  MvcResultWrapper
	///////////////////////////////////////////
	public String header(String name) throws Exception {
		return new ApplicationMvcResult(resultActions.andReturn()).header(name);
	}

	public Map<String, Object> content(ObjectMapper objectMapper) throws Exception {
		return new ApplicationMvcResult(resultActions.andReturn()).contents(objectMapper);
	}

	public <T> T path(String path) throws Exception {
		return new ApplicationMvcResult(resultActions.andReturn()).path(path);
	}

	public String uri() throws Exception {
		return new ApplicationMvcResult(resultActions.andReturn()).uri();
	}

	public static class ApplicationMvcResult {

		private MvcResult r;

		private Map<String, Object> content;
		private Object document;
		private String uri;

		public ApplicationMvcResult(MvcResult r) {
			this.r = r;
		}

		public String header(String name) throws Exception {
			return r.getResponse().getHeader(name);
		}

		@SuppressWarnings("unchecked")
		public Map<String, Object> contents(ObjectMapper objectMapper) throws Exception {
			if (content != null)
				return content;
			String body = r.getResponse().getContentAsString();
			content = objectMapper.readValue(body, Map.class);
			content.remove("_links");
			return content;
		}

		public <T> T path(String path) throws Exception {
			if (document != null)
				return JsonPath.read(document, path);
			String body = r.getResponse().getContentAsString();
			document = Configuration.defaultConfiguration().jsonProvider().parse(body);
			return JsonPath.read(document, path);
		}

		public String uri() throws Exception {
			if (uri != null)
				return uri;

			uri = r.getResponse().getHeader("Location");
			if (uri != null) {
				return uri;
			}
			uri = (String) path("$._links.self.href");
			if (uri != null) {
				return uri;
			}

			uri = r.getRequest().getRequestURL().toString();
			if (uri != null) {
				return uri;
			}
			return null;
		}
	}

}
