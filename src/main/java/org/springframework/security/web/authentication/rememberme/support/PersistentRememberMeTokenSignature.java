package org.springframework.security.web.authentication.rememberme;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.security.crypto.codec.Base64;

public class PersistentRememberMeTokenSignature extends RememberMeTokenSignature {

	private static final int TWO_WEEKS_S = 1209600;
	private static final int DEFAULT_SERIES_LENGTH = 16;
	private static final int DEFAULT_TOKEN_LENGTH = 16;
	private static final SecureRandom random = new SecureRandom();

	private String username;
	private String series = generateSeriesData();
	private String tokenValue = generateTokenData();
	private Date date = new Date();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////
	public void setSignature(String signatureText) {
		String[] values = decode(signatureText);
		this.series = values[0];
		this.tokenValue = values[1];
	}

	public String getSignature() {
		return encode(new String[] { series, tokenValue });
	}

	/////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////
	public void setToken(PersistentRememberMeToken token) {
		this.username = token.getUsername();
		this.series = token.getSeries();
		this.tokenValue = token.getTokenValue();
		this.date = token.getDate();
	}

	public PersistentRememberMeToken getToken() {
		return new PersistentRememberMeToken(username, series, tokenValue, new Date());
	}

	////////////////////////////////////////
	// 
	////////////////////////////////////////
	public void validate(PersistentRememberMeToken token) {
		if (!getTokenValue().equals(token.getTokenValue())) {
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
