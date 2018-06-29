package io.github.u2ware.sample.sign;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface AccountTokenRepository extends CrudRepository<AccountToken, String> {

	@Modifying
	@Transactional
	int deleteByUsername(@Param("username") String username);
}
