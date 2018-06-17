package io.github.u2ware.apps.login;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

@Component
public class UserTokenService implements PersistentTokenRepository{

	protected Log logger = LogFactory.getLog(getClass());
	
	private @Autowired UserTokenRepository userTokenRepository;
	
	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		UserToken exists = userTokenRepository.findOne(token.getSeries());
		if (exists != null) {
			throw new DataIntegrityViolationException("Series Id '" + token.getSeries()+ "' already exists!");
		}
		
		UserToken newToken = new UserToken();
		newToken.setSeries(token.getSeries());
		newToken.setUsername(token.getUsername());
		newToken.setTokenValue(token.getTokenValue());
		newToken.setTokenDate(token.getDate().getTime());
		userTokenRepository.save(newToken);
		logger.info("createNewToken: "+token.getSeries());
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		UserToken exists = userTokenRepository.findOne(series);
		exists.setTokenValue(tokenValue);
		exists.setTokenDate(lastUsed.getTime());
		userTokenRepository.save(exists);
		logger.info("updateToken: "+series);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		UserToken exists = userTokenRepository.findOne(seriesId);
		if(exists == null) return null;
		PersistentRememberMeToken m = new PersistentRememberMeToken(exists.getUsername(), exists.getSeries(), exists.getTokenValue(),new Date(exists.getTokenDate()));
		logger.info("getTokenForSeries: "+seriesId);
		return m;
	}

	@Override
	public void removeUserTokens(String username) {
		userTokenRepository.deleteByUsername(username);
		logger.info("removeUserTokens: "+username);
	}
}
