package com.h3c.iclouds.biz.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;

import com.alibaba.fastjson.JSONArray;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.po.MailQuenu;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.MailUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;

import edu.emory.mathcs.backport.java.util.Arrays;

public class EmailReceiver implements MessageListener {
	
	public Logger log = LoggerFactory.getLogger(EmailReceiver.class);
	
	private BaseDAO<MailQuenu> mailQuenuDao = null;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(Message message) {
		mailQuenuDao = SpringContextHolder.getBean("baseDAO");
		String body = null;
		try {
			body = new String(message.getBody(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(body == null) {
			LogUtils.warn(this.getClass(), "Empty body to mail");
			return;
		}
		try {
			// 获取配置信息
			MessageProperties mp = message.getMessageProperties();
			if(StrUtils.checkParam(mp) && StrUtils.checkParam(mp.getHeaders())) {
				Map<String, Object> map = mp.getHeaders();
				for (String key : map.keySet()) {
					LogUtils.info(this.getClass(), "Send mail queen header. key: " + key + ", " + StrUtils.tranString(map.get(key)));
				}
			}
			MailQuenu entity = mailQuenuDao.findById(MailQuenu.class, body);
			if(entity == null) {
				LogUtils.warn(this.getClass(), "Not find mail, id:" + body);
			}
			String title = entity.getMtopic();
			String content = entity.getMcontent();
			List<String> emailList = Arrays.asList(entity.getMto().split(";"));
			// 附件处理
			List<String> attList = null;
			if(StrUtils.checkParam(entity.getAttachments())) {
				attList = Arrays.asList(entity.getAttachments().split(";"));
			}
			
			File[] files = null;
			if(StrUtils.checkCollection(attList)) {
				String path = UploadFileUtils.getUploadPath();
				files = new File[attList.size()];
				int i = 0;
				for (String fileName : attList) {
					File file = new File(path + File.separator + fileName);
					if(file != null && file.exists() && file.isFile()) {
						files[i++] = file;
					}
				}
			}
			log.info("send mails:" + JSONArray.toJSONString(emailList));
			log.info("send mail title:" + title);
			log.info("send mail content:" + content);
			log.info("send mail files:" + JSONArray.toJSONString(files));
			MailUtils.sendEmail(emailList, title, content, files);
		} catch (Exception e) {
			log.error("Send mail failure, message:" + e.getMessage() + "\t----\tbody:" + body);
		}
	}

}
