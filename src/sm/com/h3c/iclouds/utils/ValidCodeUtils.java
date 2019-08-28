package com.h3c.iclouds.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.common.ConfigProperty; 

public class ValidCodeUtils {
	
	public static String CODEFONT = null;
	
	public static String getCodeFont() {
		if(CODEFONT == null) {
			String sep = File.separator;
			Path confPath = Paths.get(CacheSingleton.getInstance().getProjectPath() + "resource" + sep + "valid_code.font");
			StringBuffer buffer = new StringBuffer();
			List<String> list = null;
			try {
				list = Files.readAllLines(confPath, Charset.forName("UTF-8"));
				
				for (String line : list) {
					buffer.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			CODEFONT = buffer.toString();
		}
		return CODEFONT;
	}

	// 图片的宽度。
	private int width = 120;
	// 图片的高度。
	private int height = 45;
	// 验证码字符个数
	public static int CODE_COUNT = 4;
	// 验证码干扰线数
	private int lineCount = 20;
	// 验证码
	private String code = null;
	// 验证码图片Buffer
	private BufferedImage buffImg = null;

	private char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8' };

	// 生成随机数
	private Random random = new Random();

	public static ValidCodeUtils create() {
		return new ValidCodeUtils();
	}
	
	public ValidCodeUtils() {  
        this.createCode();  
    }

	/** 
     *  
     * @param width 
     *            图片宽 
     * @param height 
     *            图片高 
     */  
    public ValidCodeUtils(int width, int height) {  
        this.width = width;  
        this.height = height;  
        this.createCode();  
    }

	/** 
     *  
     * @param width 
     *            图片宽 
     * @param height 
     *            图片高 
     * @param codeCount 
     *            字符个数 
     * @param lineCount 
     *            干扰线条数 
     */  
    public ValidCodeUtils(int width, int height, int codeCount, int lineCount) {  
        this.width = width;  
        this.height = height;  
        this.lineCount = lineCount;  
        this.createCode();  
    }

	public void createCode() {
		int codeX = 0;
		int fontHeight = 0;
		fontHeight = height - 5 * 2;// 字体的高度
		codeX = width / (CODE_COUNT + 2);// 每个字符的宽度

		// 图像buffer
		buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();

		// 将图像填充为白色
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		// 创建字体
		ImgFontByte imgFont = new ImgFontByte();
		Font font = imgFont.getFont(fontHeight);
		g.setFont(font);

		// 绘制干扰线
		for (int i = 0; i < lineCount; i++) {
			int xs = getRandomNumber(width);
			int ys = getRandomNumber(height);
			int xe = xs + getRandomNumber(width / 8);
			int ye = ys + getRandomNumber(height / 8);
			g.setColor(getRandomColor());
			g.drawLine(xs, ys, xe, ye);
		}

		StringBuffer randomCode = new StringBuffer();
		// 随机产生验证码字符
		for (int i = 0; i < CODE_COUNT; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			// 设置字体颜色
			g.setColor(new Color(243, 155, 97));
			// 设置字体位置
			g.drawString(strRand, (i + 1) * codeX, getRandomNumber(height / 2) + 25);
			randomCode.append(strRand);
		}
		code = randomCode.toString();
	}

	/** 获取随机颜色 */
	private Color getRandomColor() {
		int r = getRandomNumber(255);
		int g = getRandomNumber(255);
		int b = getRandomNumber(255);
		return new Color(r, g, b);
	}

	/** 获取随机数 */
	private int getRandomNumber(int number) {
		return random.nextInt(number);
	}

	public void write(String path) throws IOException {
		OutputStream sos = new FileOutputStream(path);
		this.write(sos);
	}

	public void write(OutputStream sos) throws IOException {
		ImageIO.write(buffImg, "png", sos);
		sos.close();
	}

	public BufferedImage getBuffImg() {
		return buffImg;
	}

	public String getCode() {
		return code;
	}

	/** 字体样式类 */
	class ImgFontByte {
		public Font getFont(int fontHeight) {
			try {
//				Font baseFont = Font.createFont(Font.TRUETYPE_FONT,
//						new ByteArrayInputStream(hex2byte(ValidCodeUtils.getCodeFont())));
//				return baseFont.deriveFont(Font.PLAIN, fontHeight);
				return new Font("Arial", Font.BOLD, fontHeight);
			} catch (Exception e) {
				return new Font("Arial", Font.BOLD, fontHeight);
			}
		}
	}
	
	/**
	 * 验证码超时时间:120秒
	 */
	public static final long CODE_TIMEOUT = 120;
	
	public static boolean existKey(String key) {
		String redisValue = (String) RedisUtils.get(key);
		// 重置时间
		RedisUtils.expire(key, CODE_TIMEOUT * 1000);
		return redisValue == null ? false : true;
	}
	
	public static void setCodeToRedis(String key) {
		RedisUtils.set(key, ConfigProperty.SYSTEM_FLAG, CODE_TIMEOUT * 1000);
	}

	public static void setCodeToRedis(String key, String value) {
		RedisUtils.set(key, value, CODE_TIMEOUT * 1000);
	}
	
	public static boolean equalCode(String key, String value) {
		String redisValue = (String) RedisUtils.get(key);
		RedisUtils.delete(key);	// 删除
		if(!StrUtils.checkParam(redisValue)) {
			return false;
		}
		if(value.toLowerCase().equals(redisValue.toLowerCase())) {
			return true;
		}
		return false;
	}
}
