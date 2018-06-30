package io.github.u2ware.sample.sign;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported = false)
public interface AccountEventRepository extends CrudRepository<AccountEvent, Long> {

}
