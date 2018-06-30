package io.github.u2ware.sample.accounts;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.github.u2ware.sample.signin.UserAccount;
import io.github.u2ware.sample.signin.UserAccountPasswordEncoder;
import io.github.u2ware.sample.signin.UserAccountRepository;

@Component
@RepositoryEventHandler(Account.class)
public class AccountService {

	protected Log logger = LogFactory.getLog(getClass());

	private @Value("${io.github.u2ware.sample.accounts.AccountService.roles:ROLE_USER}") String[] roles;
	private @Value("${io.github.u2ware.sample.accounts.AccountService.enabled:true}") Boolean enabled;

	private @Autowired UserAccountPasswordEncoder encoder;
	private @Autowired UserAccountRepository repository;

	@EventListener
	public void handleBeforeQuery(AccountQuery q) {
		logger.info("handleBeforeQuery: " + q);
	}

	@HandleBeforeCreate
	public void handleBeforeCreate(Account e) {
		logger.info("handleBeforeCreate: " + e);
		if (repository.existsByUsername(e.getUsername())) {
			throw new RuntimeException("Username aleady exists.");
		}
		e.setUuid(UUID.randomUUID());
		e.setPassword(encoder.encode(e.getUsername()));
		if (StringUtils.isEmpty(e.getNickname()))
			e.setNickname(e.getUsername());
		if (e.getRoles().length == 0)
			e.setRoles(roles);
		e.setEnabled(enabled);
	}

	@HandleBeforeSave
	public void handleBeforeSave(Account e) {
		logger.info("handleBeforeSave: " + e);

		UserAccount exists = repository.findOne(e.getUuid());

		if (!exists.getUsername().equals(e.getUsername())) {
			throw new RuntimeException("username cannot change");
		}
		if("reset".equals(e.getPassword())){
			logger.info("handleBeforeSave: " + e.getPassword());
			e.setPassword(encoder.encode(e.getUsername()));
		}
	}

}
