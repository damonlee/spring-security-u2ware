package org.springframework.security.web.authentication.rememberme.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

public class RememberMeTokenSignature extends AbstractRememberMeTokenSignature {

	private static final int TWO_WEEKS_S = 1209600;

	private RememberMeTokenSignature() {
	}

	public static RememberMeTokenSignature getInstance(String signature) {
		RememberMeTokenSignature s = new RememberMeTokenSignature();
		String[] values = s.decode(signature);
		s.username = values[0];
		s.expiryTime = values[1];
		s.signatureValue = values[2];
		return s;
	}
	public static RememberMeTokenSignature newInstance(UserDetails user) {
		RememberMeTokenSignature s = new RememberMeTokenSignature();
		long tokenExpiryTime = s.getSEC949();
		s.username = user.getUsername();
		s.expiryTime = Long.toString(tokenExpiryTime);
		s.signatureValue = s.makeTokenSignature(tokenExpiryTime, user.getUsername(), user.getPassword());
		return s;
	}

	private String username;
	private String expiryTime;
	private String signatureValue;

	public String getUsername() {
		return username;
	}

	public String getExpiryTime() {
		return expiryTime;
	}

	public String getSignatureValue() {
		return signatureValue;
	}

	/////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////
	public String getSignature() {
		return encode(new String[] { username, expiryTime, signatureValue });
	}

	/////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////
	public void validate(UserDetails user) {
		long tokenExpiryTime;
		try {
			tokenExpiryTime = new Long(getExpiryTime()).longValue();
		} catch (NumberFormatException nfe) {
			// throw new InvalidCookieException(
			// "Cookie token[1] did not contain a valid number (contained '"+
			// getExpiryTime() + "')");
			throw new RememberMeAuthenticationException("invalid");
		}

		if (tokenExpiryTime < System.currentTimeMillis()) {
			// throw new InvalidCookieException(
			// "Cookie token[1] has expired (expired on '"+ new Date(tokenExpiryTime) +
			// "'; current time is '" + new Date() + "')");
			throw new RememberMeAuthenticationException("expired");
		}

		String expectedTokenSignature 
			= makeTokenSignature(tokenExpiryTime, user.getUsername(), user.getPassword());

		if (!equals(expectedTokenSignature, getSignatureValue())) {
			// throw new InvalidCookieException("Cookie token[2] contained signature '"
			// + getToken(2) + "' but expected '" + expectedTokenSignature + "'");
			throw new RememberMeAuthenticationException("invalid");
		}
	}

	/////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////
	private String makeTokenSignature(long tokenExpiryTime, String username, String password) {
		String data = username + ":" + tokenExpiryTime + ":" + password + ":" + getClass();
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
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
