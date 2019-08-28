package com.h3c.iclouds.biz.impl;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.biz.UserBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class KeyExpireMessageListener implements MessageListener {
	
	@Resource
	private RedisTemplate<String, SessionBean> redisBiz;

    @Resource
    private UserBiz userBiz;

	@Override
	public void onMessage(Message message, byte[] abyte0) {
		String token = redisBiz.getStringSerializer().deserialize(message.getBody());
		try {
			if(StrUtils.checkParam(token) && token.contains(ConfigProperty.PROJECT_TOKEN_IYUN_PROFIX)) {
				LogUtils.info(this.getClass(), "Token: " + token);
				token = token.substring(token.indexOf(ConfigProperty.PROJECT_TOKEN_IYUN_PROFIX), token.length());
                userBiz.logout(token, true);
			}
		} catch (Exception e) {
			LogUtils.warn(this.getClass(), "Expire handle error, token:" + token);
		}
		
	}

}
