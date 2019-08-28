package com.h3c.iclouds.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PwdUtils {

	private final static String DES = "DES";
	
	public static String getPwd() {
		int pwd_len = 10;	// 密码长度
		int type_num = 3;	// 字符种类
		int i; // 生成的随机数
		int count = 0; // 生成的密码的长度
		
		char[] letterStr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 
				'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
		char[] numStr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		char[] charStr = {'!', '@', '#', '$', '%', '^', '&', '*'};
		List<char[]> list = new ArrayList<char[]>();
		list.add(letterStr);
		list.add(numStr);
		list.add(charStr);
		StringBuffer pwd = new StringBuffer("");
		//字符串类型数
		Set<Integer> setInt = new HashSet<Integer>();
		Random r = new Random();
		List<char[]> tempList = new ArrayList<char[]>();
		int x = list.size();
		boolean flag = true;
		while(flag){
			for (int j = 0; j < type_num; j++){
				j = Math.abs(r.nextInt(x));
				if (!setInt.contains(j)){
					setInt.add(j);
					tempList.add(list.get(j));
				}
				if (setInt.size() == type_num){
					flag = false;
				}
			}
		}
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止生成负数，且需包含字母数字和特殊字符
			int j = Math.abs(r.nextInt(tempList.size()));
			i = Math.abs(r.nextInt(tempList.get(j).length));
			if (i >= 0 && i < tempList.get(j).length) {
				pwd.append(tempList.get(j)[i]);
				count++;
			}
		}
		return pwd.toString();
	}
	
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
	
	/**
	 * 加密
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        String strs = Base64.encodeBase64String(bt);
        return strs;
    }
	
	 private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
	 }
	 
	 /**
	  * 解密
	  * @param data
	  * @param key
	  * @return
	  * @throws IOException
	  * @throws Exception
	  */
	 public static String decrypt(String data, String key) throws IOException, Exception {
		 if (data == null)
		     return null;
		 byte[] buf = Base64.decodeBase64(data);
		 byte[] bt = decrypt(buf,key.getBytes());
		 return new String(bt);
	 }
	
}
