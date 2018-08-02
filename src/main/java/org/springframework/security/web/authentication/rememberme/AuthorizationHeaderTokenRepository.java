package org.springframework.security.web.authentication.rememberme;

import java.util.Date;

public interface AuthorizationHeaderTokenRepository extends PersistentTokenRepository{


	void createNewToken(PersistentRememberMeToken token, Action t);

	void updateToken(String series, String tokenValue, Date lastUsed);

	PersistentRememberMeToken getTokenForSeries(String seriesId);

	void removeUserTokens(String username, Action e);

	
	public static enum Action{
		singin,
		singout,
		invalid,
		expired,
		reset, //change password
		delete,// delete account
	}
	
}
