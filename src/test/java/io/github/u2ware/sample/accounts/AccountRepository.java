package io.github.u2ware.sample.accounts;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import io.github.u2ware.sample.signin.UserAuthority.RoleSuperAuthorize;

@RoleSuperAuthorize
public interface AccountRepository 
		extends PagingAndSortingRepository<Account, UUID>, JpaSpecificationExecutor<Account> {

}
