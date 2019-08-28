package com.h3c.iclouds.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.h3c.cloudos.common.e.Crypto;
import com.h3c.cloudos.common.e.CryptoFactory;

/**
 * 加解密工具类。
 * 
 * @author w07586
 */
public class CryptoUtils {

	/** 日志记录对象 */
	Logger log = LoggerFactory.getLogger(CryptoUtils.class);

	Crypto crypto = CryptoFactory.getInstance().getCrypto();

	/**
	 * 对字符串进行加密，主要用于在保存用户密码时，进行加密操作。
	 * 
	 * @param plainText
	 *            文本明文。
	 * @param charsetName
	 *            字符编码名称。
	 * @return 文本密文。
	 */
	public static String encryptData(String plainText, String charsetName) {
		try {
			return new String(encryptData(plainText.getBytes(charsetName)),
					charsetName);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 对字符串进行加密，主要用于在保存用户密码时，进行加密操作。
	 * 
	 * @param plainText
	 *            文本明文。
	 * @return 文本密文，该密文已经过Base64编码。
	 */
	public static byte[] encryptData(byte[] plainText) {

		// 执行加密操作
		byte[] cryptoText = null;
		try {
			cryptoText = CryptoFactory.getInstance().getCrypto()
					.encrypt(plainText);
		} catch (Exception e) {
			return null;
		}

		// 执行Base64编码
		if (cryptoText != null) {
			return Base64.encodeBase64(cryptoText);
		}

		return null;
	}

	/**
	 * 对字符串数据进行解密操作。
	 * 
	 * @param cryptoText
	 *            文本密文。
	 * @param charsetName
	 *            字符编码名称。
	 * @return 文本明文。
	 */
	public String decryptData(String cryptoText, String charsetName) {
		try {
			return new String(decryptData(cryptoText.getBytes(charsetName)),
					charsetName);
		} catch (Exception e) {
			log.error(null, e);
			return null;
		}
	}

	/***
	 * 使用"UTF8"编码方式对字符串进行解密操作
	 * 
	 * @param cryptoText
	 *            文本密文。
	 * @return 文本明文。
	 */
	public String decryptData(String cryptoText) {
		try {
			return new String(decryptData(cryptoText.getBytes("UTF8")), "UTF8");
		} catch (UnsupportedEncodingException e) {
			log.error(null, e);
			return null;
		}
	}

	/**
	 * 对字符串数据进行解密操作。
	 * 
	 * @param cryptoText
	 *            经过Base64编码的文本密文。
	 * @return 文本明文。
	 */
	public byte[] decryptData(byte[] cryptoText) {

		// 对密文进行Base64解码
		byte[] decodedText = Base64.decodeBase64(cryptoText);
		if (decodedText == null) {
			return null;
		}

		// 进行数据解密操作
		try {
			return crypto.decrypt(decodedText);
		} catch (Exception e) {
			log.error(null, e);
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(encryptData("b5014ff2fcd74887654321", "UTF-8"));
//		if (args.length < 1) {
//			System.out.println("No arguments.");
//			return;
//		}
//		if (args[0].equalsIgnoreCase("-encrypt")) {
//			if (args.length != 2) {
//				System.out.println("encrypt format is error.");
//				return;
//			}
//			String plainText = args[1].trim();
//			System.out.println("encrypt : " + encryptData(plainText, "UTF-8"));
//			return;
//		} else if (args[0].equalsIgnoreCase("-decrypt")) {
//			if (args.length != 2) {
//				System.out.println("decrypt format is error.");
//				return;
//			}
//			String plainText = args[1].trim();
////			System.out.println("decrypt : " + decryptData(plainText, "UTF-8"));
//			return;
//		}
	}
}