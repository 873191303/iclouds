package com.h3c.iclouds.quartz;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.common.UploadFileModal;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.RedisUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.UploadFileUtils;

/**
 * 检查系统情况
 * 1.查询上传文件key
 * 2.监测用户生效，失效时间
 * @author zkf5485
 *
 */
public class CheckQuartz {
	
	public Logger log = LoggerFactory.getLogger(CheckQuartz.class);
	
	@Resource
	private UserBiz userBiz;
	
	public void checkFileKey() {
		try {
			Map<String, UploadFileModal> fileMap = SimpleCache.UPLOAD_FILE_MAP;
			Set<String> set = new HashSet<String>();
			Set<String> srcSet = new CopyOnWriteArraySet<String>(set);
			for (String key : srcSet) {
				Object value = RedisUtils.get(key);
				if(null == value) {
					log.info("Remove expire file key: " + key);
					UploadFileUtils.deleteFile(key, fileMap.get(key));
				}
			}
		} catch (Exception e) {
			log.error("Check file key error. Message:" + e.getMessage());
		}
	}
	
	public void checkUsers() {
		List<User> users = userBiz.getAll(User.class);
		if (StrUtils.checkCollection(users)) {
			for (User entity : users) {
				try {
					if(!userBiz.checkUserExpireTime(entity)) {
						if(!entity.getStatus().equals(ConfigProperty.NO)) {
							entity.setStatus(ConfigProperty.NO);	// 设置用户状态为失效
							log.warn("Update user [" + entity.getId() + "][" + entity.getLoginName() + "] status to disabled");
							userBiz.update(entity);
						}
					} else if(!entity.getStatus().equals(ConfigProperty.YES)) {
						entity.setStatus(ConfigProperty.YES);	// 设置用户状态为失效
						log.warn("Update user [" + entity.getId() + "][" + entity.getLoginName() + "] status to enabled");
						userBiz.update(entity);
					}
				} catch (Exception e) {
					log.error("Check user [" + entity.getId() + "] error. Message:" + e.getMessage());
				}
			}	
		}
	}
	
	public void check() {
		log.info("Check file key start");
		this.checkFileKey();	// 检查key是否有效
		log.info("Check file key end");
		
		/** 每天检查用户生效失效情况 */
		long current = System.currentTimeMillis();
		// 取整点时间
		String dateStr = DateUtils.getDate(new Date(), DateUtils.dateFormat) + " 00:00:00";
		long checkTime = DateUtils.getDateByString(dateStr).getTime();
		long devTime = 4 * 60 * 1000;	// 误差时间为4分钟
		
		// 误差时间在4分钟以内
		if(Math.abs(current - devTime) < checkTime) {
			log.info("Check user status start");
			this.checkUsers();
			log.info("Check user status end");
		} else {
			this.checkUsers();
		}
	}

}
