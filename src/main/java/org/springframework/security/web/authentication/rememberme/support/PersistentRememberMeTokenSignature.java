package org.springframework.security.web.authentication.rememberme.support;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

public class PersistentRememberMeTokenSignature extends AbstractRememberMeTokenSignature {

	private static final int TWO_WEEKS_S = 1209600;
	private static final int DEFAULT_SERIES_LENGTH = 16;
	private static final int DEFAULT_TOKEN_LENGTH = 16;
	private static final SecureRandom random = new SecureRandom();

	private PersistentRememberMeTokenSignature() {
	}

	public static PersistentRememberMeTokenSignature getInstance(String signature) {
		PersistentRememberMeTokenSignature s = new PersistentRememberMeTokenSignature();
		String[] values = s.decode(signature);
		s.series = values[0];
		s.tokenValue = values[1];
		return s;
	}

	public static PersistentRememberMeTokenSignature newInstance(PersistentRememberMeToken token) {
		PersistentRememberMeTokenSignature s = new PersistentRememberMeTokenSignature();
		s.username = token.getUsername();
		s.series = token.getSeries();
		s.tokenValue = s.generateTokenData();
		return s;
	}

	public static PersistentRememberMeTokenSignature newInstance(String username) {
		PersistentRememberMeTokenSignature s = new PersistentRememberMeTokenSignature();
		s.username = username;
		s.series = s.generateSeriesData();
		s.tokenValue = s.generateTokenData();
		return s;
	}

	private String username;
	private String series;
	private String tokenValue;

	public String getSeries() {
		return series;
	}
	public String getTokenValue() {
		return tokenValue;
	}

	////////////////////////////////////////
	//
	////////////////////////////////////////
	public String getSignature() {
		return encode(new String[] { series, tokenValue });
	}

	public PersistentRememberMeToken getToken() {
		return new PersistentRememberMeToken(username, series, tokenValue, new Date());
	}

	////////////////////////////////////////
	//
	////////////////////////////////////////
	public void validate(PersistentRememberMeToken token) {
		if (! tokenValue.equals(token.getTokenValue())) {
			// "PersistentTokenBasedRememberMeServices.cookieStolen"+
			// "Invalid remember-me token (Series/token) mismatch. Implies previous cookie
			// theft attack.");
			throw new RememberMeAuthenticationException("invalid");
		}

		if (token.getDate().getTime() + TWO_WEEKS_S * 1000L < System.currentTimeMillis()) {
			// throw new RememberMeAuthenticationException("Remember-me login has expired");
			throw new RememberMeAuthenticationException("expired");
		}
	}

	////////////////////////////////////////
	//
	////////////////////////////////////////
	private String generateSeriesData() {
		byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
		random.nextBytes(newSeries);
		return new String(Base64.encode(newSeries));
	}

	private String generateTokenData() {
		byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
		random.nextBytes(newToken);
		return new String(Base64.encode(newToken));
	}
}
