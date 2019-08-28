package com.h3c.iclouds.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.validate.PatternValidator;

public class MailUtils {

	public static void main(String[] args) {
		List<String> targetList = new ArrayList<String>();
		targetList.add("KF.zengruixin@h3c.com");
//		targetList.add("KF.yinyiyun@h3c.com");
		sendEmail(targetList, "test", "this is a test email");
	}

	public static void sendEmail(String target, String title, String content) {
		List<String> targetList = new ArrayList<>();
		targetList.add(target);
		sendEmail(targetList, title, content, null, null);
	}

	public static void sendEmail(List<String> targetList, String title, String content, List<String> ccList) {
		sendEmail(targetList, title, content, ccList, null);
	}

	public static void sendEmail(List<String> targetList, String title, String content) {
		sendEmail(targetList, title, content, null, null);
	}

	public static void sendEmail(List<String> targetList, String title, String content, File[] files) {
		sendEmail(targetList, title, content, null, files);
	}

	public static void sendEmail(String targetEmail, String title, String content, File[] files) {
		List<String> targetList = new ArrayList<String>();
		targetList.add(targetEmail);
		sendEmail(targetList, title, content, null, files);
	}

	public static void sendEmailShowImg(String target, String title, String content) {
		List<String> targetList = new ArrayList<>();
		targetList.add(target);
		sendEmailShowImg(targetList, title, content, null, null);
	}

	public static void sendEmailShowImg(List<String> targetList, String title, String content, List<String> ccList) {
		sendEmailShowImg(targetList, title, content, ccList, null);
	}

	public static void sendEmailShowImg(List<String> targetList, String title, String content) {
		sendEmailShowImg(targetList, title, content, null, null);
	}

	public static void sendEmailShowImg(List<String> targetList, String title, String content, File[] files) {
		sendEmailShowImg(targetList, title, content, null, files);
	}

	public static void sendEmailShowImg(String targetEmail, String title, String content, File[] files) {
		List<String> targetList = new ArrayList<String>();
		targetList.add(targetEmail);
		sendEmailShowImg(targetList, title, content, null, files);
	}

	public static void sendEmail(List<String> targetList, String title, String content, List<String> ccList,
			File[] files) {
		LogUtils.info(MailUtils.class, "Send mail start ...");
		final Map<String, String> smtpMap = CacheSingleton.getInstance().getConfigMap();
		Properties p = System.getProperties();
		boolean flag = StrUtils.checkParam(smtpMap.get(ConfigProperty.EMAIL_USERNAME)) ? true : false;
		p.put("mail.smtp.host", smtpMap.get(ConfigProperty.EMAIL_HOST));
		p.put("mail.smtp.port", smtpMap.get(ConfigProperty.EMAIL_PORT));
		p.put("mail.smtp.auth", flag ? "true" : "false");
		Session session = Session.getInstance(p, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpMap.get(ConfigProperty.EMAIL_USERNAME),
						smtpMap.get(ConfigProperty.EMAIL_PWD));
			}
		});

		Message mimeMessage = new MimeMessage(session);
		try {
			mimeMessage.setFrom(new InternetAddress(smtpMap.get(ConfigProperty.EMAIL_SOURCE_ADDRESS)));
			int sendCount = 0;
			for (String email : targetList) {
				if (StrUtils.checkParam(email) && PatternValidator.emailCheck(email)) {
					mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email)); // 收件人
					sendCount++;
				}
			}
			if (sendCount == 0) {
				LogUtils.info(MailUtils.class, "No send address limit");
				return;
			}
			LogUtils.info(MailUtils.class, "Send mail to " + JSONObject.toJSONString(targetList));
			if (ccList != null && !ccList.isEmpty()) {
				for (String email : ccList) {
					if (StrUtils.checkParam(email) && PatternValidator.emailCheck(email)) {
						mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email)); // 抄送人
					}
				}
				LogUtils.info(MailUtils.class, "CC mail to " + JSONObject.toJSONString(ccList));
			}
			try {
				mimeMessage.setSubject(MimeUtility.encodeText(title, "utf-8", "B"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
//			mimeMessage.setSubject(title);
			mimeMessage.setHeader("X-Mailer", "JavaMail");
			mimeMessage.setSentDate(new Date());

			Multipart mimeMultipart = new MimeMultipart();
			// 内容部分.
			BodyPart contentBodyPart = new MimeBodyPart();
			if(content.indexOf("<")>-1 && content.indexOf(">")>-1 && content.indexOf("<<")<=-1 && content.indexOf(">>")<=-1){
				contentBodyPart.setContent(content, "text/html;charset=UTF-8");
			}else{
				contentBodyPart.setContent(content, "text/plain;charset=UTF-8");
			}
			mimeMultipart.addBodyPart(contentBodyPart);
			BodyPart fileBodyPart = null;
			FileDataSource fileDataSource = null;
			if (files != null && files.length > 0) {
				for (File file : files) {
					if (file != null && file.exists() && file.isFile()) { // 存在且是文件
						fileBodyPart = new MimeBodyPart();
						fileDataSource = new FileDataSource(file);
						fileBodyPart.setDataHandler(new DataHandler(fileDataSource));
						try {
							String fileName = fileDataSource.getName();
							fileName = fileName.contains("_") ? fileName.split("_")[1] : fileName;
							fileBodyPart.setFileName(MimeUtility.encodeWord(fileName, "UTF-8", null));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						mimeMultipart.addBodyPart(fileBodyPart);
						LogUtils.info(MailUtils.class, "Send file:" + file.getAbsoluteFile());
					}
				}
			}

			mimeMessage.setContent(mimeMultipart);
			mimeMessage.setSentDate(new Date());
			mimeMessage.saveChanges();
			if (flag) { // 认证发送
				Transport transport = session.getTransport("smtp");
				transport.connect(smtpMap.get(ConfigProperty.EMAIL_USERNAME), smtpMap.get(ConfigProperty.EMAIL_PWD));
				transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
				transport.close();
			} else { // 无认证发送
				Transport.send(mimeMessage);
			}
			LogUtils.info(MailUtils.class, "Send mail success ...");
		} catch (AddressException e) {
			e.printStackTrace();
			LogUtils.info(MailUtils.class, "from address is null ...");
		} catch (MessagingException e) {
			e.printStackTrace();
			LogUtils.info(MailUtils.class, "Send mail failure ...");
		}
	}

	public static void sendEmailShowImg(List<String> targetList, String title, String content, List<String> ccList,
			File[] files) {
		LogUtils.info(MailUtils.class, "Send mail start ...");
		final Map<String, String> smtpMap = CacheSingleton.getInstance().getConfigMap();
		Properties p = System.getProperties();
		boolean flag = StrUtils.checkParam(smtpMap.get(ConfigProperty.EMAIL_USERNAME)) ? true : false;
		p.put("mail.smtp.host", smtpMap.get(ConfigProperty.EMAIL_HOST));
		p.put("mail.smtp.port", smtpMap.get(ConfigProperty.EMAIL_PORT));
		p.put("mail.smtp.auth", flag ? "true" : "false");
		Session session = Session.getInstance(p, new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpMap.get(ConfigProperty.EMAIL_USERNAME),
						smtpMap.get(ConfigProperty.EMAIL_PWD));
			}
		});

		Message mimeMessage = new MimeMessage(session);
		try {
			mimeMessage.setFrom(new InternetAddress(smtpMap.get(ConfigProperty.EMAIL_SOURCE_ADDRESS)));
			int sendCount = 0;
			for (String email : targetList) {
				if (StrUtils.checkParam(email) && PatternValidator.emailCheck(email)) {
					mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email)); // 收件人
					sendCount++;
				}
			}
			if (sendCount == 0) {
				LogUtils.info(MailUtils.class, "No send address limit");
				return;
			}
			LogUtils.info(MailUtils.class, "Send mail to " + JSONObject.toJSONString(targetList));
			if (ccList != null && !ccList.isEmpty()) {
				for (String email : ccList) {
					if (StrUtils.checkParam(email) && PatternValidator.emailCheck(email)) {
						mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email)); // 抄送人
					}
				}
				LogUtils.info(MailUtils.class, "CC mail to " + JSONObject.toJSONString(ccList));
			}
			try {
				mimeMessage.setSubject(MimeUtility.encodeText(title, "utf-8", "B"));// 设置邮件主题
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			// mimeMessage.setSubject(title);
			mimeMessage.setHeader("X-Mailer", "JavaMail");
			mimeMessage.setSentDate(new Date());

			// 内容部分.
			BodyPart text = new MimeBodyPart();   // 文本body
			text.setContent(content, "text/html;charset=UTF-8"); // 设置内容格式为html格式
			MimeMultipart mimeMultipart = new MimeMultipart();
			mimeMultipart.addBodyPart(text);
			MimeMultipart mimeMultipart2 = new MimeMultipart();
			FileDataSource fileDataSource = null;
			if (files != null && files.length > 0) {
				MimeBodyPart img=null;
				for (File file : files) {// 处理图片
					if (file != null && file.exists() && file.isFile()) { // 存在且是文件
						String fileName = file.getName();  
						// 如果是图片,直接显示在正文
						if ((fileName.indexOf(".jpg") > -1) || (fileName.indexOf(".png") > -1) || (fileName.indexOf(".gif") > -1)) { 
							fileDataSource = new FileDataSource(file);
							img = new MimeBodyPart();  // 图片 body
							img.setDataHandler(new DataHandler(fileDataSource));
							img.setContentID(fileName);   //设置html格式正文调用时的名称
							mimeMultipart.addBodyPart(img);
							LogUtils.info(MailUtils.class, "Send img:" + file.getAbsoluteFile());
						}
					}
				}
				mimeMultipart.setSubType("related");// 设置文本与图片的关系
				
				MimeBodyPart attach=null;
				for (File file : files) {// 处理图片以外的文件 加入附件
					if (file != null && file.exists() && file.isFile()) { // 存在且是文件
						String fileName = file.getName();
						if ((fileName.indexOf(".jpg") > -1) || (fileName.indexOf(".png") > -1) || (fileName.indexOf(".gif") > -1)) { // 非图片
							continue;
						}
						attach = new MimeBodyPart();  // 附件body
						fileDataSource = new FileDataSource(file);
						attach.setDataHandler(new DataHandler(fileDataSource));
						try {

							fileName = fileName.contains("_") ? fileName.split("_")[1] : fileName;
							attach.setFileName(MimeUtility.encodeWord(fileName, "UTF-8", null)); // 设置附件名
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						mimeMultipart2.addBodyPart(attach);
						LogUtils.info(MailUtils.class, "Send file:" + file.getAbsoluteFile());
					}
				}
			}
			// 将正文和图片的部件封装成一个body
			MimeBodyPart bodyContent = new MimeBodyPart();
			bodyContent.setContent(mimeMultipart);

			//加入装有附件的部件
			mimeMultipart2.addBodyPart(bodyContent);
			mimeMultipart2.setSubType("mixed"); // 设置部件内部关系

			mimeMessage.setContent(mimeMultipart2);
			mimeMessage.setSentDate(new Date());
			mimeMessage.saveChanges();
			if (flag) { // 认证发送
				Transport transport = session.getTransport("smtp");
				transport.connect(smtpMap.get(ConfigProperty.EMAIL_USERNAME), smtpMap.get(ConfigProperty.EMAIL_PWD));
				transport.sendMessage(mimeMessage, mimeMessage.getRecipients(Message.RecipientType.TO));
				transport.close();
			} else { // 无认证发送
				Transport.send(mimeMessage);
			}
			LogUtils.info(MailUtils.class, "Send mail success ...");
		} catch (AddressException e) {
			e.printStackTrace();
			LogUtils.info(MailUtils.class, "from address is null ...");
		} catch (MessagingException e) {
			e.printStackTrace();
			LogUtils.info(MailUtils.class, "Send mail failure ...");
		}
	}

}
