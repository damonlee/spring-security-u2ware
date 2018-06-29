package io.github.u2ware.sample.signup;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.u2ware.sample.sign.UserAccount;
import io.github.u2ware.sample.sign.UserAccountPasswordEncoder;
import io.github.u2ware.sample.sign.UserAccountRepository;

@Service
public class JoinService {

	protected final Log logger = LogFactory.getLog(getClass());

	private @Value("${io.github.u2ware.sample.register.JoinService.roles:ROLE_USER}") String[] roles;
	private @Value("${io.github.u2ware.sample.register.JoinService.enabled:true}") Boolean enabled;
	private @Value("${io.github.u2ware.sample.register.JoinService.notify:false}") Boolean notify;

	private @Autowired UserAccountPasswordEncoder encoder;
	private @Autowired UserAccountRepository repository;

	public boolean userExists(String username) {
		return repository.existsByUsername(username);
	}

	public Object createUser(String username, String password) {

		UserAccount e = new UserAccount();
		e.setUuid(UUID.randomUUID());
		e.setUsername(username);
		e.setPassword(encoder.encode(password));
		e.setRoles(roles);
		e.setEnabled(enabled);

		return repository.save(e);
	}

}