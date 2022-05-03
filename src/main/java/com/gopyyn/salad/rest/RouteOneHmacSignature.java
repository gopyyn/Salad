package com.gopyyn.salad.rest;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.gopyyn.salad.core.SaladCommands;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static java.lang.String.format;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.codec.digest.DigestUtils.md5;
import static org.apache.commons.lang3.StringUtils.substring;

public class RouteOneHmacSignature {
	private static final String NEWLINE="\n";
	private static final String HMAC_SHA_256 = "HmacSHA256";
	private static final int CANONICAL_URL_SEARCH_INDEX = 8;

	private RouteOneHmacSignature() {
	}

	public static String compute(String sharedSecret, String canonicalRepresentation) {
		try {
			SecretKey secretKey = new SecretKeySpec(sharedSecret.getBytes(Charsets.UTF_8), HMAC_SHA_256);
			Mac mac = Mac.getInstance(HMAC_SHA_256);
			mac.init(secretKey);
			mac.update(canonicalRepresentation.getBytes(Charsets.UTF_8));
			return new String(encodeBase64(mac.doFinal()));
		} catch (NoSuchAlgorithmException|InvalidKeyException e) {
			throw new RuntimeException("Unable to compute hmac signature", e);
		}
	}

	public static String authorizationHeader(String httpMethod, String contentMd5, String contentType, String date, String dealer, String url) {
		StringBuilder canonicalRepresentation = new StringBuilder();
		canonicalRepresentation.append(httpMethod.toUpperCase()).append(NEWLINE);
		canonicalRepresentation.append(Strings.nullToEmpty(contentMd5).toLowerCase()).append(NEWLINE);
		canonicalRepresentation.append(Strings.nullToEmpty(contentType).toLowerCase()).append(NEWLINE);
		canonicalRepresentation.append(date.toLowerCase()).append(NEWLINE);
		canonicalRepresentation.append("X-RouteOne-Act-As-Dealership:".toLowerCase()).append(dealer).append(NEWLINE);
		canonicalRepresentation.append(getCanonicalUrl(url)).append(NEWLINE);
		String hmacSignature = compute(SaladCommands.getVariableAsString("hmac_ss"), canonicalRepresentation.toString());

		return format("RouteOne %s:%s", SaladCommands.getVariableAsString("hmac_id"), hmacSignature);
	}

	private static String getCanonicalUrl(String url) {
		if (url.startsWith("http")) {
			return substring(url, url.indexOf('/', CANONICAL_URL_SEARCH_INDEX));
		}

		return url;
	}

	public static String getMd5(String body) {
		return encodeBase64String(md5(body)); //NOSONAR [squid:S2070] Content-md5 has to be md5 hashing
	}
}
