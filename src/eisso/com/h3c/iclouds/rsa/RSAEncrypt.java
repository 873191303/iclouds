package com.h3c.iclouds.rsa;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

@SuppressWarnings("deprecation")
public class RSAEncrypt {

	/**
	 * 私钥
	 */
	private RSAPrivateKey privateKey;

	/**
	 * 公钥
	 */
	private RSAPublicKey publicKey;

	/**
	 * 获取私钥
	 * 
	 * @return 当前的私钥对象
	 */
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * 获取公钥
	 * 
	 * @return 当前的公钥对象
	 */
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}
	
	static String RSA_INSTANCE = "RSA";

	public static String getContentByPath(String path) {
		Path $path = Paths.get(path);
		StringBuffer buffer = new StringBuffer();
		try {
			List<String> lines = Files.readAllLines($path, Charset.forName("UTF-8"));
			for (String line : lines) {
				if (!line.contains("----")) {
					buffer.append(line).append("\r");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * 从字符串中加载公钥
	 * @param publicKeyStr 公钥数据字符串
	 * @throws Exception 加载公钥时产生的异常
	 */
	public void loadPublicKey(String publicKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decodeBase64(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_INSTANCE, new BouncyCastleProvider());
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("公钥非法");
		} catch (NullPointerException e) {
			throw new Exception("公钥数据为空");
		}
	}

	public void loadPrivateKeyPKCS8(String privateKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decodeBase64(privateKeyStr);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_INSTANCE, new BouncyCastleProvider());
			this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	public void loadPrivateKey(String privateKeyStr) throws Exception {
		try {
			byte[] buffer = Base64.decodeBase64(privateKeyStr);
			RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure(
					(ASN1Sequence) ASN1Sequence.fromByteArray(buffer));
			RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(),
					asn1PrivKey.getPrivateExponent());
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_INSTANCE, new BouncyCastleProvider());
			PrivateKey priKey = keyFactory.generatePrivate(rsaPrivKeySpec);
			this.privateKey = (RSAPrivateKey) priKey;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此算法");
		} catch (InvalidKeySpecException e) {
			throw new Exception("私钥非法");
		} catch (IOException e) {
			throw new Exception("私钥数据内容读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥数据为空");
		}
	}

	/**
	 * 合并数组
	 * @param a
	 * @param b
	 * @return
	 */
	public static byte[] concat(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	/**
	 * 字符串分组截取
	 * 
	 * @param text
	 * @param index
	 * @param size
	 * @return
	 */
	public static String group(String text, int index, int size) {
		if (text.length() > size + index) {
			return text.substring(index, size + index);
		}
		return text.substring(index, text.length());
	}
	
	private byte[] group(byte[] data, int index, int size) {
		int end = data.length >= (size + index) ? (size + index) : (data.length - index);
		byte[] result = new byte[size];
		int i = 0;
		for (; index < end; index++) {
			result[i++] = data[index];
		}
		return result;
	}

	/**
	 * 私钥解密过程
	 * 
	 * @param privateKey 私钥
	 * @param cipherData 密文数据
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		try {
			byte[] result = new byte[] {};
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			for (int i = 0; i < cipherData.length; i += decode_size) {
				byte[] data = group(cipherData, i, decode_size);
				byte[] output = cipher.doFinal(data);
				result = concat(result, output);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}
	
	/**
	 * 公钥解密过程
	 * @param publicKey 私钥
	 * @param cipherData 密文数据
	 * @return 明文
	 * @throws Exception 解密过程中的异常信息
	 */
	public byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws Exception {
		if (privateKey == null) {
			throw new Exception("解密私钥为空, 请设置");
		}
		try {
			byte[] result = new byte[] {};
			Cipher cipher = Cipher.getInstance(RSA_INSTANCE, new BouncyCastleProvider());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			for (int i = 0; i < cipherData.length; i += decode_size) {
				byte[] data = group(cipherData, i, decode_size);
				byte[] output = cipher.doFinal(data);
				result = concat(result, output);
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此解密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("解密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("密文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("密文数据已损坏");
		}
	}
	
	/**
	 * 秘钥加密过程
	 * @param privateKey 秘钥
	 * @param data 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public byte[] encrypt(RSAPrivateKey privateKey, String data) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		try {
			byte[] result = new byte[] {};
			Cipher cipher = Cipher.getInstance(RSA_INSTANCE, new BouncyCastleProvider());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			for (int i = 0; i < data.length(); i += encode_size) {
				String text = group(data, i, encode_size);
				byte[] plainTextData = text.getBytes();
				byte[] output = cipher.doFinal(plainTextData);
				result = concat(result, output);
				if (text.length() != encode_size) {
					break;
				}
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
		return null;
	}
	
	/**
	 * 公钥加密过程
	 * @param publicKey 公钥
	 * @param data 明文数据
	 * @return
	 * @throws Exception 加密过程中的异常信息
	 */
	public byte[] encrypt(RSAPublicKey publicKey, String data) throws Exception {
		if (publicKey == null) {
			throw new Exception("加密公钥为空, 请设置");
		}
		try {
			byte[] result = new byte[] {};
//			Cipher cipher = Cipher.getInstance(RSA_INSTANCE, new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			for (int i = 0; i < data.length(); i += encode_size) {
				String text = group(data, i, encode_size);
				byte[] plainTextData = text.getBytes();
				byte[] output = cipher.doFinal(plainTextData);
				result = concat(result, output);
				if (text.length() != encode_size) {
					break;
				}
			}
			return result;
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("无此加密算法");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			throw new Exception("加密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new Exception("明文长度非法");
		} catch (BadPaddingException e) {
			throw new Exception("明文数据已损坏");
		}
	}
	
	static int encode_size = 256;
	
	static int decode_size = 256;

	static String encryptStr = "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj";
	
	
	static String testStr = "test1111111111111111111111111111111111111111111111"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj"
			+ "test1111111111111111111111111111111111111111111111"
			+ "this is abc.asfasfjkafaksfjakjfkalsfjakslfjasklfjasklfjasklfjasklfjakslfj";
	

	public static void main(String[] args) throws Exception {
		RSAEncrypt rsaEncrypt = new RSAEncrypt();

		String privatePath = "C:/h3crypto.pri";
		String private_key = getContentByPath(privatePath);

		String publicPath = "C:/h3crypto.pub";
		String public_key = getContentByPath(publicPath);

		// 加载公钥
		try {
			rsaEncrypt.loadPublicKey(public_key);
			System.out.println("加载公钥成功");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("加载公钥失败");
		}

		// 加载私钥
		try {
			rsaEncrypt.loadPrivateKey(private_key);
			System.out.println("加载私钥成功");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("加载私钥失败");
		}
		
		// 公钥加密
		
		String testStr = "guanxianchun";
		byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), testStr);
		String miwen = Base64.encodeBase64String(cipher);
		miwen = new String(miwen).replaceAll("[\\n\\r]", "");
		System.out.println(miwen);
		
		// 私钥解密
		byte[] plainBytes = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);
		String plainText = new String(plainBytes);
		System.out.println(plainText);

		// 测试字符串
//		String encryptStr = "Test String";
//		try {
//			// 加密
//			byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPrivateKey(), testStr);
//			System.out.println("密文长度：" + cipher.length);
//			String cipherText = Base64.encode(cipher);
//			cipherText = new String(cipherText).replaceAll("[\\n\\r]", "");
//			System.out.println("密文：" + cipherText);
//			System.out.println("密文长度:" + cipherText.length());
//			
//			// 解密
//			byte[] plainBytes = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), cipher);
//			
//			String plainText = new String(plainBytes);
//			plainText = plainText.replaceAll("[\\t\\n\\r]", "");
//			System.out.println("明文长度:" + plainText.length());
//			System.out.println(plainText);
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
		}
		
	
}
