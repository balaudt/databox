package com.sagarius.goddess.shared;

import com.googlecode.gwt.crypto.bouncycastle.digests.SHA1Digest;
import com.googlecode.gwt.crypto.util.Str;

public class Utils {

	/**
	 * Encrypts a string using SHA1Digest algorithm
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] encrypt(String str) {
		byte[] dataIn = Str.toBytes(str.toCharArray());
		SHA1Digest digest = new SHA1Digest();
		digest.update(dataIn, 0, dataIn.length);
		byte[] dataOut = new byte[digest.getDigestSize()];
		digest.doFinal(dataOut, 0);
		return dataOut;
	}

	/**
	 * Encrypts a string using SHA1Digest algorithm and the converts the byte
	 * array into a string
	 * 
	 * @param str
	 * @return
	 */
	public static String encryptAsString(String str) {
		byte[] encryptBytes = encrypt(str);
		StringBuffer buffer = new StringBuffer(encryptBytes.length);
		for (byte b : encryptBytes) {
			buffer.append(b + "");
		}
		return buffer.toString();
	}

}
