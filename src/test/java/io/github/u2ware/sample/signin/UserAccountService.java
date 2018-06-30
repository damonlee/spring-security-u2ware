package io.github.u2ware.sample.signin;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService implements UserDetailsService {

	protected Log logger = LogFactory.getLog(getClass());

	private @Value("${security.user.name:}") String freepassUsername;
	private @Value("${security.user.password:}") String freepassPassword;
	private @Value("${security.user.role:}") String[] freepassRoles;

	private @Autowired UserAccountPasswordEncoder userAccountPasswordEncoder;
	private @Autowired UserAccountRepository userAccountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserAccount a = userAccountRepository.findByUsername(username);
		logger.info("loadUserByUsername1: " + username);
		if (a == null) {
			a = freepassUserAccount(username);
			if (a == null) {
				logger.info("loadUserByUsername2: " + username);
				return null;
			}
		}
		logger.info("loadUserByUsername3: " + username);
		if(! a.isEnabled()) return null;

		UserAccountPrincipalImpl p = new UserAccountPrincipalImpl();
		p.setUsername(a.getUsername());
		p.setPassword(a.getPassword());
		p.setRoles(a.getRoles());
		p.setNickname(a.getNickname());
		p.setEnabled(a.isEnabled());

		logger.info("loadUserByUsername: " + username);
		return p;
	}

	private UserAccount freepassUserAccount(String username) {
		if (!username.equals(freepassUsername))
			return null;

		UserAccount u = new UserAccount();
		u.setUuid(UUID.randomUUID());
		u.setUsername(freepassUsername);
		u.setNickname(freepassUsername);
		u.setPassword(userAccountPasswordEncoder.encode(freepassPassword));
		u.setRoles(freepassRoles);
		u.setEnabled(true);

		logger.info("freepassUser: " + username);

		return userAccountRepository.save(u);
	}
}