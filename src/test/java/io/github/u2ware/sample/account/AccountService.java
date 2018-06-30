package io.github.u2ware.sample.account;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import io.github.u2ware.sample.signin.UserAccount;
import io.github.u2ware.sample.signin.UserAccountPasswordEncoder;
import io.github.u2ware.sample.signin.UserAccountPrincipalDelegate;
import io.github.u2ware.sample.signin.UserAccountRepository;

@Service("MyAccountService")
public class AccountService {

	protected final Log logger = LogFactory.getLog(getClass());

	private @Autowired UserAccountPrincipalDelegate principal;
	private @Autowired UserAccountPasswordEncoder encoder;
	private @Autowired UserAccountRepository repository;

	public Object readUser() {
		return principal.getPrincipal();
	}

	public Object changeNickname(String nickname) {
		String username = principal.getUsername();
		UserAccount u = repository.findByUsername(username);
		u.setNickname(nickname);
		return repository.save(u);
	}

	public Object changePassword(String oldPassword, String newPassword) {

		String username = principal.getUsername();
		UserAccount u = repository.findByUsername(username);

		if (!encoder.matches(oldPassword, u.getPassword())) {
			throw new AccessDeniedException("Password incorrect");
		}

		u.setPassword(encoder.encode(newPassword));
		return repository.save(u);
	}

	public Object updateUser(MultiValueMap<String, Object> account) {
		return new RuntimeException("not supported.");
	}

	public Object deleteUser(MultiValueMap<String, Object> params) {
		String username = principal.getUsername();
		UserAccount u = repository.findByUsername(username);
		u.setEnabled(false);
		return repository.save(u);
	}
}