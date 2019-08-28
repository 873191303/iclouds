package com.h3c.iclouds.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * AES加密解密工具
 * 
 * @author fangls
 * @since hxzntech 1.0
 */
public class EncrypAES {
	// KeyGenerator 提供对称密钥生成器的功能，支持各种算�?
	private KeyGenerator keygen;
	
	// SecretKey 负责保存对称密钥
	private SecretKey deskey;
	
	// Cipher负责完成加密或解密工�?
	private Cipher c;
	
	// 该字节数组负责保存加密的结果
	private byte[] cipherByte;

	public EncrypAES(String key) throws NoSuchAlgorithmException, NoSuchPaddingException {
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		// 实例化支持DES算法的密钥生成器(算法名称命名�?按规定，否则抛出异常)
		keygen = KeyGenerator.getInstance("AES");
		keygen.init(new SecureRandom(key.getBytes()));
		// 生成密钥
		deskey = keygen.generateKey();
		// 生成Cipher对象,指定其支持的DES算法
		c = Cipher.getInstance("AES");
	}

	/**
	 * 获取加密字符串
	 * 
	 * @param key
	 * @param msg
	 * @return
	 */
	public static String getEncryptAES(String key, String msg) {
		String encStr = "";
		try {
			EncrypAES encAES = new EncrypAES(key);
			byte[] encontent = encAES.Encrytor(msg);
			encStr = encAES.parseByte2HexStr(encontent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encStr;
	}

	/**
	 * 获取揭秘字符串
	 * 
	 * @param key
	 * @param encStr
	 * @return
	 */
	public static String getDecryptAES(String key, String encStr) {
		String decStr = "";
		try {
			EncrypAES decAES = new EncrypAES(key);
			byte[] decontent = decAES.Decryptor(decAES.parseHexStr2Byte(encStr));
			decStr = new String(decontent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decStr;
	}

	/**
	 * 校验密码是否正确，正确返回true，错误返回false
	 * 
	 * @param key
	 * @param ptex
	 * @param secret
	 * @return
	 */
	public static boolean validPassWord(String key, String ptex, String secret) {
		String str = getDecryptAES(key, secret);
		if (str.equals(ptex))
			return true;
		else
			return false;
	}

	/**
	 * 对字符串加密
	 * 
	 * @param str
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Encrytor(String str) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
		c.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] src = str.getBytes();
		// 加密，结果保存进cipherByte
		cipherByte = c.doFinal(src);
		return cipherByte;
	}

	/**
	 * 对字符串解密
	 * 
	 * @param buff
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Decryptor(byte[] buff) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
		c.init(Cipher.DECRYPT_MODE, deskey);
		cipherByte = c.doFinal(buff);
		return cipherByte;
	}

	/**
	 * 将二进制转换�?16进制
	 * 
	 * @param buf
	 * @return
	 */
	public String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * �?16进制转换为二进制
	 * 
	 * @param hexStr
	 * @return
	 */
	public byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * @param args
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 */
	public static void main(String[] args) throws Exception {
		EncrypAES de1 = new EncrypAES("Flzx3qcY4yhljt");
		EncrypAES de2 = new EncrypAES("Flzx3qcY4yhljt");
		String msg = "cloudos";
		System.out.println("明文:" + msg);
		byte[] encontent = de1.Encrytor(msg);
		String authcation = de1.parseByte2HexStr(encontent);
		System.out.println("加密:" + authcation);
		byte[] decontent = de2.Decryptor(de2.parseHexStr2Byte(authcation));
		System.out.println("解密:" + new String(decontent));
	}
}
