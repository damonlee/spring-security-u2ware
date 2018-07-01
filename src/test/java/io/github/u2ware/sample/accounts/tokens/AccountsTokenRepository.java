package io.github.u2ware.sample.accounts.tokens;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.method.P;

import io.github.u2ware.sample.signin.UserAuthority.RoleSuperAuthorize;

@RoleSuperAuthorize
public interface AccountsTokenRepository
		extends PagingAndSortingRepository<AccountsToken, String>, JpaSpecificationExecutor<AccountsToken> {

	@RestResource(exported = false)
	public AccountsToken findOne(String id);

	@RestResource(exported = false)
	public <S extends AccountsToken> S save(S entity);

	@RestResource(exported = false)
	public void delete(@P("entity") AccountsToken entity);

}
