package com.huawei.agilete.base.common;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class StringEncrypt {
	/**
	 * 对字符串加密,加密算法使用SHA-256（加盐）
	 * 
	 * @param strSrc
	 *            要加密的字符串
	 * @return
	 */
	public static String encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;
		StringBuilder sb = new StringBuilder(36);
		sb.append(UUID.randomUUID());
		int len = sb.length();
		if (len < 36) {
			for (int i = 0; i < 36 - len; i++) {
				sb.append("0");
			}
		}
		String salt = sb.toString();
		String pw = strSrc + salt;
		byte[] bt = pw.getBytes();
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(bt);
			strDes = bytes2Hex(md.digest());

		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return new BASE64Encoder().encode((salt + strDes).getBytes());
	}

	public static String bytes2Hex(byte[] bts) {
		StringBuffer des = new StringBuffer();
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des.append("0");
			}
			des.append(tmp);
		}
		return des.toString();
	}

	/**
	 * 校验密码是否正确
	 * 
	 * @throws IOException
	 */
	public static boolean verify(String password, String sha256) {

		String salt = "";
		String value = "";
		try {
			value = new String(new BASE64Decoder().decodeBuffer(sha256));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		salt = value.substring(0, 36);
		String pw = value.replace(salt, "");
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update((password + salt).getBytes());
		String strDes = bytes2Hex(md.digest());
		return pw.equals(strDes);
	}

}