package io.github.u2ware.sample.account.logs;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.method.P;

public interface AccountLogRepository
		extends PagingAndSortingRepository<AccountLog, Long>, JpaSpecificationExecutor<AccountLog> {

	@RestResource(exported = false)
	public AccountLog findOne(Long id);

	@RestResource(exported = false)
	public <S extends AccountLog> S save(S entity);

	@RestResource(exported = false)
	public void delete(@P("entity") AccountLog entity);

	Page<AccountLog> findAll(Specification<AccountLog> spec, Pageable pageable);
}
