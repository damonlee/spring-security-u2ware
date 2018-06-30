package io.github.u2ware.sample.sign;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface UserAccountRepository extends CrudRepository<UserAccount, UUID> {

	UserAccount findByUsername(String username);

	boolean existsByUsername(String username);

}
