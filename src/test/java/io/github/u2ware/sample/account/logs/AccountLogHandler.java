package io.github.u2ware.sample.account.logs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.JpaPredicateBuilder;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import io.github.u2ware.sample.signin.UserAccountPrincipalDelegate;
import io.github.u2ware.sample.signin.UserAuthority;

@Component
@RepositoryEventHandler(AccountLog.class)
public class AccountLogHandler extends AccountLog implements Specification<AccountLog> {

	protected Log logger = LogFactory.getLog(getClass());
	private @Autowired UserAccountPrincipalDelegate principal;

	@Override
	public Predicate toPredicate(Root<AccountLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return JpaPredicateBuilder.getInstance(root, query, cb).eq("username", getUsername()).build();
	}

	@EventListener
	public void handleBeforeQuery(AccountLogHandler q) {
		logger.info("handleBeforeQuery: " + q);
		if (!principal.hasRoles(UserAuthority.ROLE_SUPER)) {
			q.setUsername(principal.getUsername());
		}
		logger.info("handleBeforeQuery: " + q);
	}
}
