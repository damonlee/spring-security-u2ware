package org.springframework.security.web.authentication.rememberme;

public interface AuthorizationHeaderRepository extends PersistentTokenRepository{

	void removeUserTokens(String username, Exception e);


}
