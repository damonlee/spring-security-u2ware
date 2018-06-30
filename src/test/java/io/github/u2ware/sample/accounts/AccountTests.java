package io.github.u2ware.sample.accounts;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.u2ware.sample.ApplicationTests;
import io.github.u2ware.sample.signin.UserAccountPrincipalImpl;

public class AccountTests extends ApplicationTests {

	@Test
	public void contextLoads() throws Exception {

		UserDetails su = account("su", "ROLE_SUPER");

		UserDetails u = account("ad2");

		POST(uri("/accounts")).AUTH(u).CONTENT(members("user1")).RUN(is4xx());
		String href1 = POST(uri("/accounts")).AUTH(su).CONTENT(members("user1")).RUN().resultPath();
		String href2 = POST(uri("/accounts")).AUTH(su).CONTENT(members("user2")).RUN().resultPath();
		String href3 = POST(uri("/accounts")).AUTH(su).CONTENT(members("user3", "ROLE_ADMIN")).RUN().resultPath();
		String href4 = POST(uri("/accounts")).AUTH(su).CONTENT(members("user4")).RUN().resultPath();
		String href5 = POST(uri("/accounts")).AUTH(su).CONTENT(members("user5", "ROLE_USER")).RUN().resultPath();

		GET(uri("/accounts/!q")).AUTH(u).RUN(is4xx());
		GET(uri("/accounts/!q")).AUTH(su).RUN(is2xx());

		GET(href1).AUTH(su).RUN(is2xx());
		GET(href1).AUTH(u).RUN(is4xx());
		GET(href4).AUTH(u).RUN(is4xx());
		GET(href4).AUTH(su).RUN(is2xx());

		POST("/login").P(register("user1", "user1")).RUN();
		POST("/login").P(register("user2", "user2")).RUN();
		POST("/login").P(register("user3", "user3")).RUN();
		POST("/login").P(register("user4", "user4")).RUN();
		POST("/login").P(register("user5", "user5")).RUN();

		DELETE(href1).AUTH(u).RUN(is4xx());
		DELETE(href1).AUTH(su).RUN(is2xx());
		DELETE(href2).AUTH(su).RUN(is2xx());
		DELETE(href3).AUTH(su).RUN(is2xx());
		DELETE(href4).AUTH(u).RUN(is4xx());
		DELETE(href4).AUTH(su).RUN(is2xx());
		DELETE(href5).AUTH(su).RUN(is2xx());

		GET(uri("/accountLogs/!q")).AUTH(account("su", "ROLE_SUPER")).RUN(is2xx());
		GET(uri("/accountLogs/!q")).AUTH(account("user1")).RUN(is2xx(), isJson("$.page.totalElements", 1));
	}

	protected UserDetails account(String username, String... roles) {
		UserAccountPrincipalImpl u = new UserAccountPrincipalImpl();
		u.setUsername(username);
		u.setRoles(roles);
		return u;
	}

	private Map<String, Object> members(String username, String... roles) throws Exception {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("username", username);
		p.put("roles", roles);
		return p;
	}
	private Map<String, Object> register(String username, String password){
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("username", username);
		m.put("password", password);
		return m;
	}

}