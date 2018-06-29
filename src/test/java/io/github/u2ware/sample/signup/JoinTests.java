package io.github.u2ware.sample.signup;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.github.u2ware.sample.ApplicationTests;

public class JoinTests extends ApplicationTests {

	@Test
	public void contextLoads() throws Exception {

		GET("/join").P(register("user1", "pass1")).RUN(is2xx());
		POST("/join").P(register("user1", "pass1")).RUN(is2xx());
		GET("/join").P(register("user1", "pass1")).RUN(is5xx());
		POST("/join").P(register("user1", "pass1")).RUN(is5xx());

		POST("/login").P(register("user1", "pass1")).RUN(is2xx());

	}

	private Map<String, Object> register(String username, String password) {
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("username", username);
		m.put("password", password);
		return m;
	}
}
