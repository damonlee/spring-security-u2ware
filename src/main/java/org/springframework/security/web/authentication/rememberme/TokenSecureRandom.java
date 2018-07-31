package org.springframework.security.web.authentication.rememberme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.util.StringUtils;

public class TokenSecureRandom {

	private SecureRandom random = new SecureRandom();

	public static final int DEFAULT_SERIES_LENGTH = 16;
	public static final int DEFAULT_TOKEN_LENGTH = 16;

	private int seriesLength = DEFAULT_SERIES_LENGTH;
	private int tokenLength = DEFAULT_TOKEN_LENGTH;

	public TokenSecureRandom() {
		this.tokens = decode();
	}

	public String encode() {
		return encode(this.tokens);
	}
	public void validate(String username, String password) {
		validate(this.tokens, username, password);
	}

	private String encode(String[] values) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]);

			if (i < values.length - 1) {
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
	
	private PersistentRememberMeToken decode(String username) throws InvalidCookieException {

		PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(
				username, generateSeriesData(), generateTokenData(), new Date());
		return persistentToken;
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

	
	
	
	private String makeTokenSignature(long tokenExpiryTime, String username, String password) {
		String data = username + ":" + tokenExpiryTime + ":" + password + ":" + key;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(data.getBytes())));
	}

	private void validate(String[] tokens, String username, String password) {

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
	private boolean isTokenExpired(long tokenExpiryTime) {
		return tokenExpiryTime < System.currentTimeMillis();
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

	
}
