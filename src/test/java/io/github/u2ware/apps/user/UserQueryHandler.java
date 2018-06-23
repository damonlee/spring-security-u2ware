package io.github.u2ware.apps.user;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(User.class)
public class UserQueryHandler {
	protected Log logger = LogFactory.getLog(getClass());

	@EventListener
	public void handleQuery(UserQuery userQuery) {
		logger.info("handleQuery: " + userQuery);
	}

	@HandleBeforeCreate
	public void handleBeforeCreate(User user) {
		logger.info("handleBeforeCreate: " + user);
	}

	@HandleBeforeDelete
	public void handleBeforeDelete(User user) {
		logger.info("handleBeforeDelete: " + user);
	}

	@HandleBeforeLinkDelete
	public void handleBeforeLinkDelete(User user) {
		logger.info("handleBeforeLinkDelete: " + user);
	}

	@HandleBeforeLinkSave
	public void handleBeforeLinkSave(User user) {
		logger.info("handleBeforeLinkSave: " + user);
	}

	@HandleBeforeSave
	public void handleBeforeSave(User user) {
		logger.info("handleBeforeSave: " + user);
	}

	@HandleAfterCreate
	public void handleAfterCreate(User user) {
		logger.info("handleAfterCreate: " + user);
	}

	@HandleAfterDelete
	public void handleAfterDelete(User user) {
		logger.info("handleAfterDelete: " + user);
	}

	@HandleAfterLinkDelete
	public void handleAfterLinkDelete(User user) {
		logger.info("handleAfterLinkDelete: " + user);
	}

	@HandleAfterLinkSave
	public void handleAfterLinkSave(User user) {
		logger.info("handleAfterLinkSave: " + user);
	}

	@HandleAfterSave
	public void handleAfterSave(User user) {
		logger.info("handleAfterSave: " + user);
	}


}

