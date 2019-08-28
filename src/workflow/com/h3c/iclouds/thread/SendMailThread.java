package com.h3c.iclouds.thread;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import com.alibaba.fastjson.JSON;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.MailQuenu;
import com.h3c.iclouds.utils.LogUtils;

/**
 * 发送邮件线程
 * 先走队列，如果队列异常则程序直接发送
 * @author zkf5485
 *
 */
public class SendMailThread implements Runnable {
	
	public static AmqpTemplate amqpTemplate = null;
	
	public static MessageListener emailReceiver = SpringContextHolder.getBean("emailReceiver");
	
	public static BaseDAO<MailQuenu> mailQuenuDao = SpringContextHolder.getBean("baseDAO");
	
	public static final String EMAIL_QUEEN = "iyun_email_queen";
	
	private String emails;
	
	private String attachment;
	
	private String[] attachments;
	
	private String title;
	
	private String content;
	
	public static SendMailThread create() {
		return new SendMailThread();
	}
	
	@Override
	public void run() {
		// 设置邮件内容，附件则路径的形式
		String attachments_ = "";
		if(attachments != null && attachments.length > 0) {
			for (String temp : attachments) {
				attachments_ += temp + ";";
			}
		} else {
			attachments_ = attachment;
		}
		
		MailQuenu mq = new MailQuenu();
		mq.createdUser(ConfigProperty.SYSTEM_FLAG);
		mq.setMto(emails);
		mq.setMname(title);
		mq.setMfrom(ConfigProperty.EMAIL_SOURCE_ADDRESS);
		mq.setMtopic(title);
		mq.setMcontent(content);
		mq.setAttachments(attachments_);
		String id = mailQuenuDao.add(mq);
		LogUtils.info(this.getClass(), "Send mail address: " + JSON.toJSONString(mq));
		try {
			amqpTemplate = SpringContextHolder.getBean("amqpTemplate");
			// 写进队列，由队列做发送
			amqpTemplate.convertAndSend(EMAIL_QUEEN, id);
		} catch (Exception e) {
			LogUtils.exception(this.getClass(), e, "Write queen error");
			// 写进队列失败，则改用程序发送
			MessageProperties mp = new MessageProperties();
			mp.setHeader("progrem", "true");	// 不是由队列处理的标志
			Message message = new Message(id.getBytes(), mp);
			try {
				emailReceiver.onMessage(message);
			} catch (Exception e2) {
				LogUtils.exception(this.getClass(), e, "Progrem send mail error, data:" + JSON.toJSONString(mq));
			}
		}
	}
	
	public SendMailThread setEmails(String emails) {
		this.emails = emails;
		return this;
	}
	
	public SendMailThread setAttachment(String attachment) {
		this.attachment = attachment;
		return this;
	}
	
	public SendMailThread setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public SendMailThread setContent(String content) {
		this.content = content;
		return this;
	}

	public String[] getAttachments() {
		return attachments;
	}

	public void setAttachments(String[] attachments) {
		this.attachments = attachments;
	}
	
}
