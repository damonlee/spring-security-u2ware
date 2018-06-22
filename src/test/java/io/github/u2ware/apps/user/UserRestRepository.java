package io.github.u2ware.apps.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserRestRepository extends PagingAndSortingRepository<User, String>, JpaSpecificationExecutor<User> {

	@PostAuthorize("(returnObject != null && returnObject.nickname == 'abcd') || returnObject == null")
	User findOne(String id);

	@PreAuthorize("hasRole('ROLE_ADMIN') and #username == 'abcd'")
	List<User> findByUsername(@Param("username") String username);

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	Page<User> findAll(Specification<User> spec, Pageable pageable);
}
