package com.h3c.iclouds.utils;

import com.h3c.iclouds.rsa.RSAEncrypt;
import org.apache.commons.codec.binary.Base64;

public class PwdRSAUtils extends RSAEncrypt {

	@SuppressWarnings("unused")
	private String privatePath;
	@SuppressWarnings("unused")
	private String publicPath;

	public PwdRSAUtils(String privatePath, String publicPath) {
		this.privatePath = privatePath;
		this.publicPath = publicPath;

		String private_key = getContentByPath(privatePath);
		String public_key = getContentByPath(publicPath);

		// 加载私钥
		try {
			loadPrivateKey(private_key);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		try {
			loadPublicKey(public_key);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * 私钥加密
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public String encryptByPri(String plainText) throws Exception {
		String result = null;
		byte[] cipher = encrypt(getPrivateKey(), plainText);
		result = Base64.encodeBase64String(cipher);
		return result;
	}

	/**
	 * 公钥加密
	 * @param plainText
	 * @return
	 * @throws Exception
	 */
	public String encryptByPub(String plainText) throws Exception {
		String result = null;
		byte[] cipher = encrypt(getPublicKey(), plainText);
		result = Base64.encodeBase64String(cipher);
		return result;
	}

	/**
	 * 公钥解密
	 * @param cipherText
	 * @return
	 * @throws Exception
	 */
	public String decryptByPub(String cipherText) throws Exception {
		String result = null;
		byte[] cipher = Base64.decodeBase64(cipherText);
		byte[] plainBytes = decrypt(getPublicKey(), cipher);
		result = new String(plainBytes);
		return result;
	}
	
	public String decryptByPri(String cipherText) throws Exception {
		String result = null;
		byte[] cipher = Base64.decodeBase64(cipherText);
		byte[] plainBytes = decrypt(getPrivateKey(), cipher);
		result = new String(plainBytes);
		return result;
	}
	
	

	public void setPrivatePath(String privatePath) {
		this.privatePath = privatePath;
	}

	public void setPublicPath(String publicPath) {
		this.publicPath = publicPath;
	}

}
