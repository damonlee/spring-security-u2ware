package io.github.u2ware.apps.login;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
public interface UserTokenRepository extends PagingAndSortingRepository<UserToken, String>{

	@Modifying @Transactional
	int deleteByUsername(@Param("username")String username);
}
