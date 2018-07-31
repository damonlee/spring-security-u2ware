package org.springframework.security.web.authentication.rememberme;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.util.StringUtils;

public class TokenSignature extends AbstractTokenSignature{

	private static final int TWO_WEEKS_S = 1209600;

	private String key = getClass().getName();
	private String[] tokens;

	public TokenSignature(String username, String password) {

	}
	public TokenSignature(String value) {
		this.tokens = super.decode(value);
	}

	public String encode() {
		return super.encode(this.tokens);
	}
	public void validate(String username, String password) {
		validate(this.tokens, username, password);
	}

	
	
	
	
	
	
	
	
	
	

	private void validate(String[] tokens, String username, String password) {

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
