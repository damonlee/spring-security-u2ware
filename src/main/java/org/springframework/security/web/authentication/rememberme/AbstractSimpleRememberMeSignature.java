package org.springframework.security.web.authentication.rememberme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.util.StringUtils;

public abstract class AbstractSimpleRememberMeSignature {

	public static final String DELIMITER = ":";
	public static final int TWO_WEEKS_S = 1209600;

	protected String encode(String[] tokens) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append(tokens[i]);

			if (i < tokens.length - 1) {
				sb.append(DELIMITER);
			}
		}

		String value = sb.toString();

		sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

		while (sb.charAt(sb.length() - 1) == '=') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	protected String[] decode(String value)  {
		for (int j = 0; j < value.length() % 4; j++) {
			value = value + "=";
		}

		if (!Base64.isBase64(value.getBytes())) {
			throw new RuntimeException("token was not Base64 encoded; value was '"+value+"'");
		}

		String cookieAsPlainText = new String(Base64.decode(value.getBytes()));

		String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText, DELIMITER);

		if ((tokens[0].equalsIgnoreCase("http") || tokens[0].equalsIgnoreCase("https"))
				&& tokens[1].startsWith("//")) {
			// Assume we've accidentally split a URL (OpenID identifier)
			String[] newTokens = new String[tokens.length - 1];
			newTokens[0] = tokens[0] + ":" + tokens[1];
			System.arraycopy(tokens, 2, newTokens, 1, newTokens.length - 1);
			tokens = newTokens;
		}
		return tokens;
	}

	/////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////
	public static class UserDetailsRememberMeTokenSignature extends AbstractSimpleRememberMeSignature{

		private String username;
		private String expiryTime;
		private String signatureValue;

		/////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////
		public void setTokenObject(UserDetails user) {
			long tokenExpiryTime = getSEC949();

			this.username = user.getUsername();
			this.expiryTime = Long.toString(tokenExpiryTime);
			this.signatureValue = makeTokenSignature(tokenExpiryTime, user.getUsername(), user.getPassword());
		}
		public UserDetails getTokenObject() {
			return null;
		}

		/////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////
		public void setToken(String signatureText) {
			String[] values = decode(signatureText);
			this.username = values[0];
			this.expiryTime = values[1];
			this.signatureValue = values[2];
		}
		public String getToken() {
			return encode(new String[] {username, expiryTime, signatureValue});
		}


		/////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getExpiryTime() {
			return expiryTime;
		}
		public void setExpiryTime(String expiryTime) {
			this.expiryTime = expiryTime;
		}
		public String getSignatureValue() {
			return signatureValue;
		}
		public void setSignatureValue(String signatureValue) {
			this.signatureValue = signatureValue;
		}
		
		/////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////
		public boolean validate(UserDetails user) {
			long tokenExpiryTime;
			try {
				tokenExpiryTime = new Long(getExpiryTime()).longValue();
			}catch (NumberFormatException nfe) {
//				throw new InvalidCookieException(
//						"Cookie token[1] did not contain a valid number (contained '"+ getExpiryTime() + "')");
				return false;
			}
	
			if (isTokenExpired(tokenExpiryTime)) {
//				throw new InvalidCookieException(
//						"Cookie token[1] has expired (expired on '"+ new Date(tokenExpiryTime) +
//						"'; current time is '" + new Date() + "')");
				return false;
			}
	
			String expectedTokenSignature = makeTokenSignature(tokenExpiryTime, user.getUsername(), user.getPassword());
	
			if (!equals(expectedTokenSignature, getSignatureValue())) {
//				throw new InvalidCookieException("Cookie token[2] contained signature '"
//						+ getToken(2) + "' but expected '" + expectedTokenSignature + "'");
				return false;
			}
			return true;
		}

		private boolean isTokenExpired(long tokenExpiryTime) {
			return tokenExpiryTime < System.currentTimeMillis();
		}

		private String makeTokenSignature(long tokenExpiryTime, String username, String password) {
			String data = username + ":" + tokenExpiryTime + ":" + password + ":" + getClass();
			MessageDigest digest;
			try {
				digest = MessageDigest.getInstance("MD5");
			}
			catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException("No MD5 algorithm available!");
			}

			return new String(Hex.encode(digest.digest(data.getBytes())));
		}
		private boolean equals(String expected, String actual) {
			byte[] expectedBytes = bytesUtf8(expected);
			byte[] actualBytes = bytesUtf8(actual);
			if (expectedBytes.length != actualBytes.length) {
				return false;
			}

			int result = 0;
			for (int i = 0; i < expectedBytes.length; i++) {
				result |= expectedBytes[i] ^ actualBytes[i];
			}
			return result == 0;
		}

		private byte[] bytesUtf8(String s) {
			if (s == null) {
				return null;
			}
			return Utf8.encode(s);
		}

		private Long getSEC949() {
			int tokenLifetime = TWO_WEEKS_S;
			long expiryTime = System.currentTimeMillis();
			// SEC-949
			expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);
			return expiryTime;
		}
	}

	/////////////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////////////
	public static class PersistentRememberMeTokenSignature extends AbstractSimpleRememberMeSignature{

		private static final int DEFAULT_SERIES_LENGTH = 16;
		private static final int DEFAULT_TOKEN_LENGTH = 16;
		private static final SecureRandom random = new SecureRandom();

		private String username;
		private String series = generateSeriesData();
		private String tokenValue = generateTokenData();
		private Date date = new Date();

		/////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////
		public void setToken(String signatureText) {
			String[] values = decode(signatureText);
			this.series = values[0];
			this.tokenValue = values[1];
		}
		public String getToken() {
			return encode(new String[] {series, tokenValue});
		}

		/////////////////////////////////////////////////
		//
		/////////////////////////////////////////////////
		public void setTokenObject(PersistentRememberMeToken token) {
			this.username = token.getUsername();
			this.series = token.getSeries();
			this.tokenValue = token.getTokenValue();
			this.date = token.getDate();
		}
		public PersistentRememberMeToken getTokenObject() {
			return new PersistentRememberMeToken(username, series, tokenValue, new Date());
		}

		////////////////////////////////////////
		// validate
		////////////////////////////////////////
		public void validate(PersistentRememberMeToken token) {
			if (! getTokenValue().equals(token.getTokenValue())) {
				throw new RememberMeAuthenticationException("invalid");
//				"PersistentTokenBasedRememberMeServices.cookieStolen"+
//				"Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.");
			}

			if (token.getDate().getTime() + TWO_WEEKS_S * 1000L < System.currentTimeMillis()) {
				throw new RememberMeAuthenticationException("expired");
//				throw new RememberMeAuthenticationException("Remember-me login has expired");
			}
		}

		////////////////////////////////////////
		//
		////////////////////////////////////////
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
}

