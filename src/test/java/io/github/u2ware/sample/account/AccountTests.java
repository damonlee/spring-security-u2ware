package io.github.u2ware.sample.account;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.github.u2ware.sample.ApplicationTests;

public class AccountTests extends ApplicationTests{

	@Test
	public void contextLoads() throws Exception {

		////////////////////////////////////
		//
		////////////////////////////////////
		String a0 = GET(uri("/account")).AUTH("aaaa").RUN(is4xx()).resultAuth();
		Assert.assertNull(a0);

		String a1 = POST("/login").P(register(freepassUsername, freepassPassword)).RUN().resultAuth();
		String a2 = PUT(uri("/account")).AUTH(a1).P("nickname", "he11o").RUN().resultAuth();
		String a3 = PUT(uri("/account")).AUTH(a2).RUN(is5xx()).resultAuth();

		try {
			//토큰 재사용 시도...
			PUT(uri("/account")).AUTH(a2).RUN(is5xx()).resultAuth();
		}catch(Exception e) {
		}
		//토큰 재사용 시도하지 않았다면, 2xx 응답
		GET(uri("/account")).AUTH(a3).RUN(is4xx());

		////////////////////////////////////
		//
		////////////////////////////////////
		a1 = POST("/login").P(register(freepassUsername, freepassPassword)).RUN().resultAuth();
		a2 = PATCH(uri("/account")).AUTH(a1).P("oldPassword", "he11o").P("newPassword", "he11o").RUN(is4xx()).resultAuth();
		a3 = PATCH(uri("/account")).AUTH(a2).P("oldPassword", freepassPassword).P("newPassword", "he11o").RUN(is2xx()).resultAuth();

		a0 = POST("/login").P(register(freepassUsername, freepassPassword)).RUN(is4xx()).resultAuth();
		a1 = POST("/login").P(register(freepassUsername, "he11o")).RUN(is2xx()).resultAuth();
		a2 = PATCH(uri("/account")).AUTH(a1).P("oldPassword", "he11o").P("newPassword", freepassPassword).RUN(is2xx()).resultAuth();
		a3 = POST("/login").P(register(freepassUsername, freepassPassword)).RUN(is2xx()).resultAuth();

		GET(uri("/accountLogs/!q")).AUTH(a3).RUN();
	}

	private Map<String, Object> register(String username, String password){
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("username", username);
		m.put("password", password);
		return m;
	}
}
