package io.github.u2ware.apps.user;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.UserDetailsDelegate;

import io.github.u2ware.apps.ApplicationTests;

public class UserRestRepositoryTests extends ApplicationTests {

	private @Autowired UserRestRepository userRestRepository;

	@Test
	public void contextLoads() throws Exception {

		userRestRepository.save(user("abcd"));
		userRestRepository.save(user("efgh"));

		String h = "/users/search/findByUsername";

		super.performRead(uri(h), new UserDetailsDelegate("u1"),               params("username=abcd"), status().is4xxClientError());
		super.performRead(uri(h), new UserDetailsDelegate("u1", "ROLE_ADMIN"), params("username=efgh"), status().is4xxClientError());
		super.performRead(uri(h), new UserDetailsDelegate("u1", "ROLE_ADMIN"), params("username=abcd"), status().is2xxSuccessful());

		super.performRead(uri("/users"),      new UserDetailsDelegate("u1"), status().is2xxSuccessful());
		super.performRead(uri("/users/abcd"), new UserDetailsDelegate("u1"), status().is2xxSuccessful());
		super.performRead(uri("/users/efgh"), new UserDetailsDelegate("u1"), status().is4xxClientError()); // 401
		super.performRead(uri("/users/ijkl"), new UserDetailsDelegate("u1"), status().is4xxClientError()); // 404

		super.performRead(uri("/users/!q/findByUsername"), new UserDetailsDelegate("u1"), status().is4xxClientError()); // 401
		super.performRead(uri("/users/!q/findByUsername"), new UserDetailsDelegate("u1", "ROLE_ADMIN"), status().is2xxSuccessful());
		super.performRead(uri("/users/!q"), new UserDetailsDelegate("u1", "ROLE_ADMIN"), status().is2xxSuccessful());

		//super.performRead(uri("/users/!q/hello"), new UserDetailsDelegate("u1", "ROLE_ADMIN"), status().is5xxServerError());

		userRestRepository.deleteAll();
	}

	protected User user(String username, String... roles) {
		User u = new User();
		u.setUsername(username);
		u.setPassword(username);
		u.setNickname(username);
		u.setAuthoritiesValue(roles);
		return u;
	}

}
