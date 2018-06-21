package io.github.u2ware.apps.login;


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

	private @Value("${security.user.name:}") String securityUserName;
	private @Value("${security.user.password:}") String securityUserPass;
	private @Value("${security.user.role:}") String[] securityUserRole;
	
	private @Autowired UserAccountRepository userAccountRepository;
	private @Autowired UserPasswordEncoder userPasswordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserAccount user = userAccountRepository.findOne(username);
		if(user == null) {
			user = freepassUser(username);
		}
		logger.info("loadUserByUsername: "+user);
		return user;
	}

	public UserAccount freepassUser(String username) {
		if(username.equals(securityUserName)) {
			logger.info("freepassUser: "+username);
			UserAccount u = new UserAccount();
			u.setUsername(securityUserName);
			u.setPassword(userPasswordEncoder.encode(securityUserPass));
			u.setAuthoritiesValue(securityUserRole);
			return userAccountRepository.save(u);
		}
		return null;
	}

}