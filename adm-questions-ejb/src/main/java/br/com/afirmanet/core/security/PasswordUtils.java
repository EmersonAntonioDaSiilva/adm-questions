package br.com.afirmanet.core.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class PasswordUtils {
	public static String md5(String senha) {
		String sen = "";
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
			sen = hash.toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sen;
	}

	public static String getTokenUsuario() {
		SecureRandom secureRandom = new SecureRandom();
		byte bytes[] = new byte[35];
		secureRandom.nextBytes(bytes);
		
		return bytes.toString();
	}
	
}
