package io.github.u2ware.sample.accounts.logs;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.JpaPredicateBuilder;

public class AccountLogQuery  extends AccountLog implements Specification<AccountLog> {

	@Override
	public Predicate toPredicate(Root<AccountLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return JpaPredicateBuilder.getInstance(root, query, cb)
				.eq("username", getUsername())
				.build();
	}

}
