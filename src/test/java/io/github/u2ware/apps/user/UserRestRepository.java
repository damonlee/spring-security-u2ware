package io.github.u2ware.apps.user;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserRestRepository extends PagingAndSortingRepository<User, String>{


	@PostAuthorize("returnObject.nickname == 'abcd'")
	User findOne(String id);
	
	
	@PreAuthorize("hasRole('ROLE_ADMIN') and #username == 'abcd'")
	List<User> findByUsername(@Param("username")String username);
	
	
}
