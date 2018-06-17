package io.github.u2ware.apps.login;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
public interface UserAccountRepository extends PagingAndSortingRepository<UserAccount, String>{

}
