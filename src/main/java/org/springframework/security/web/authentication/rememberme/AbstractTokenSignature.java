package org.springframework.security.web.authentication.rememberme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.StringUtils;

public abstract class AbstractTokenSignature {

	private static final String DELIMITER = ":";
	private static final int TWO_WEEKS_S = 1209600;
	private SecureRandom random = new SecureRandom();

	public static final int DEFAULT_SERIES_LENGTH = 16;
	public static final int DEFAULT_TOKEN_LENGTH = 16;

	private int seriesLength = DEFAULT_SERIES_LENGTH;
	private int tokenLength = DEFAULT_TOKEN_LENGTH;

	
	
	public String[] createByUsernameAndPassword(String username, String password) {
		int tokenLifetime = TWO_WEEKS_S;
		long expiryTime = System.currentTimeMillis();
		// SEC-949
		expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);

		String signatureValue = makeTokenSignature(expiryTime, username, password);

		return new String[] {username, Long.toString(expiryTime), signatureValue};
	}
	
	public String[] createByPersistentRememberMeToken() {
		String series = generateSeriesData();
		String tokenValue = generateTokenData();
		return new String[] {series, tokenValue};
	}
	public void validateByUsernameAndPassword(String[] tokens, String username, String password) {

		long tokenExpiryTime;

		try {
			tokenExpiryTime = new Long(tokens[1]).longValue();
		}
		catch (NumberFormatException nfe) {
			throw new InvalidCookieException(
					"Cookie token[1] did not contain a valid number (contained '"
							+ tokens[1] + "')");
		}

		if (isTokenExpired(tokenExpiryTime)) {
			throw new InvalidCookieException("Cookie token[1] has expired (expired on '"
					+ new Date(tokenExpiryTime) + "'; current time is '" + new Date()
					+ "')");
		}

		String expectedTokenSignature = makeTokenSignature(tokenExpiryTime, username, password);

		if (!equals(expectedTokenSignature, tokens[2])) {
			throw new InvalidCookieException("Cookie token[2] contained signature '"
					+ tokens[2] + "' but expected '" + expectedTokenSignature + "'");
		}
	}
	public void validateByPersistentRememberMeToken(String[] tokens, PersistentRememberMeToken token) {

	}

	
	protected String generateSeriesData() {
		byte[] newSeries = new byte[seriesLength];
		random.nextBytes(newSeries);
		return new String(Base64.encode(newSeries));
	}

	protected String generateTokenData() {
		byte[] newToken = new byte[tokenLength];
		random.nextBytes(newToken);
		return new String(Base64.encode(newToken));
	}
	
	
	
	protected String makeTokenSignature(long tokenExpiryTime, String username, String password) {
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

	public String encode(String[] tokens) {

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
	
	public String[] decode(String value)  {
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
	
	
}
