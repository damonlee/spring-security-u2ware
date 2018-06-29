package io.github.u2ware.sample.account.logs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import io.github.u2ware.sample.sign.UserAccountPrincipalDelegate;
import io.github.u2ware.sample.sign.UserAuthority;

@Component
@RepositoryEventHandler(AccountLog.class)
public class AccountLogService {

	protected Log logger = LogFactory.getLog(getClass());

	private @Autowired UserAccountPrincipalDelegate principal;

	@EventListener
	public void handleBeforeQuery(AccountLogQuery q) {
		logger.info("handleBeforeQuery: " + q);

		if(! principal.hasRoles(UserAuthority.ROLE_SUPER)) {
			q.setUsername(principal.getUsername());
		}
	}
}
