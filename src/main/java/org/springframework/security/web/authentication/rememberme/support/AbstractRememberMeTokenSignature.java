package org.springframework.security.web.authentication.rememberme.support;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.StringUtils;

public abstract class AbstractRememberMeTokenSignature {

	public static final String DELIMITER = ":";

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

	protected String[] decode(String value) {
		for (int j = 0; j < value.length() % 4; j++) {
			value = value + "=";
		}

		if (!Base64.isBase64(value.getBytes())) {
			throw new RuntimeException("token was not Base64 encoded; value was '" + value + "'");
		}

		String cookieAsPlainText = new String(Base64.decode(value.getBytes()));

		String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText, DELIMITER);

		if ((tokens[0].equalsIgnoreCase("http") || 
			tokens[0].equalsIgnoreCase("https")) && 
			tokens[1].startsWith("//")) {

			// Assume we've accidentally split a URL (OpenID identifier)
			String[] newTokens = new String[tokens.length - 1];
			newTokens[0] = tokens[0] + ":" + tokens[1];
			System.arraycopy(tokens, 2, newTokens, 1, newTokens.length - 1);
			tokens = newTokens;
		}
		return tokens;
	}
}
