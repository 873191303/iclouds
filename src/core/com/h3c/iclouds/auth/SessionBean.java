package com.h3c.iclouds.auth;

import com.h3c.iclouds.client.zabbix.ZabbixApi;
import static com.h3c.iclouds.client.zabbix.ZabbixApi.ZABBIX_API_MAP;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.common.UploadFileModal;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.po.Resource;
import com.h3c.iclouds.po.Role;
import com.h3c.iclouds.po.User;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.StrUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

	private User user;

	private String userId;
	
	private String loginName;
	
	private String userName;

	private transient Map<String, Role> roleMap = new HashMap<>();
	
	private Set<String> groupSet;
	
	private String remoteIp;
	
	private String token;
	
	private String vdcId;
	
	/**
	 * 用户归属最高用户
	 */
	private String superRoleId;
	
	private int roleAuthIndex = -1;
	
	private Boolean loginCloudosResult = true;
	
	/**
	 * 是否为租户管理员
	 */
	private Boolean superRole = false;
	
	/**
	 * 是否为云管理员
	 */
	private Boolean superUser = false;
	
	/**
	 * 用户权限key值
	 */
	private Set<String> keySet = new HashSet<>();
	
	private boolean selfAllowed = false;
	
	private String sessionId;

	private transient List<Resource> resources;

	public SessionBean(User user, String token) {
		sessionId = StrUtils.getUUID("SESSIONID");
		this.user = user;
		this.userId = user.getId();
		this.loginName = user.getLoginName();
		this.userName = user.getUserName();
		this.token = token;

        if(StrUtils.checkParam(userId, this.token)) {
            SimpleCache.TOKEN_TO_USER_MAP.put(this.token, userId);
        }
		String cloudosFlag = CacheSingleton.getInstance().getCloudosFlag();
		if("open".equals(cloudosFlag) && StrUtils.checkParam(user.getCloudosId())) {
			try {
				this.getCloudClient();
			} catch (Exception e) {
				LogUtils.exception(this.getClass(), e);
			}
		}
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Set<String> getGroupSet() {
		return groupSet;
	}

	public void setGroupSet(Set<String> groupSet) {
		this.groupSet = groupSet;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public Map<String, Role> getRoleMap() {
		return roleMap;
	}

	public void setRoleMap(Map<String, Role> roleMap) {
		this.superRole = true;
		// 确定最高归属角色
		if(roleMap.containsKey(CacheSingleton.getInstance().getCloudRoleId())) {
			this.superRoleId = CacheSingleton.getInstance().getCloudRoleId();
			this.roleAuthIndex = 0;
		} else if(roleMap.containsKey(CacheSingleton.getInstance().getOperationRoleId())) {
			this.superRoleId = CacheSingleton.getInstance().getOperationRoleId();
			this.roleAuthIndex = 1;
		} else if(roleMap.containsKey(CacheSingleton.getInstance().getCtRoleId())) {
			this.superRoleId = CacheSingleton.getInstance().getCtRoleId();
			this.roleAuthIndex = 2;
		} else if(roleMap.containsKey(CacheSingleton.getInstance().getTenantRoleId())) {
			this.superRoleId = CacheSingleton.getInstance().getTenantRoleId();
			this.roleAuthIndex = 3;
		} else {
			this.superRole = false;	// 不是超级管理员
			Set<String> set = new HashSet<String>();
			for (Role role : roleMap.values()) {
				set.add(role.getProleId());
			}
			if(set.contains(CacheSingleton.getInstance().getCloudRoleId())) {
				this.superRoleId = CacheSingleton.getInstance().getCloudRoleId();
				this.roleAuthIndex = 0;
			} else if(set.contains(CacheSingleton.getInstance().getOperationRoleId())) {
				this.roleAuthIndex = 1;
				this.superRoleId = CacheSingleton.getInstance().getOperationRoleId();
			} else if(set.contains(CacheSingleton.getInstance().getCtRoleId())) {
				this.superRoleId = CacheSingleton.getInstance().getCtRoleId();
				this.roleAuthIndex = 2;
			} else if(set.contains(CacheSingleton.getInstance().getTenantRoleId())) {
				this.superRoleId = CacheSingleton.getInstance().getTenantRoleId();
				this.roleAuthIndex = 3;
			}
		}
		this.roleMap = roleMap;
	}
	
	public String getSuperRoleId() {
		return superRoleId;
	}

	public void setSuperRoleId(String superRoleId) {
		this.superRoleId = superRoleId;
	}
	
	public int getRoleAuthIndex() {
		return roleAuthIndex;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getSuperRole() {
		return superRole;
	}

	public Boolean isLoginCloudosResult() {
		return loginCloudosResult;
	}

	public CloudosClient getCloudClient() {
		CloudosClient cloudClient = null;
		try {
			cloudClient = SimpleCache.CLOUDOS_CLIENT_MAP.get(this.token);
			if (null == cloudClient) {
				cloudClient = CloudosClient.create(user.getCloudosId(), loginName);
				if(null != cloudClient) {
					this.loginCloudosResult = cloudClient.isLoginResult();
					if(!loginCloudosResult) {
						LogUtils.info(this.getClass(), ResultType.system_error);
					} else {
						SimpleCache.CLOUDOS_CLIENT_MAP.put(token, cloudClient);
					}
				} else {
					loginCloudosResult = false;
				}
			}
		} catch (Exception e) {
			LogUtils.exception(getClass(), e, ResultType.system_error);
		}
		return cloudClient;
	}

	public String getVdcId() {
		return vdcId;
	}

	public void setVdcId(String vdcId) {
		this.vdcId = vdcId;
	}

	public Boolean getSuperUser() {
		return superUser;
	}

	public void setSuperUser(Boolean superUser) {
		this.superUser = superUser;
	}

	public String getProjectId() {
		return this.getUser().getProjectId();
	}

	/**
	 * 获取上传文件的key
	 * @param type
	 * @return
	 */
	public String getFileKey(String type) {
		String uuid = UUID.randomUUID().toString();
		// 设置空的
		SimpleCache.UPLOAD_FILE_MAP.put(uuid, UploadFileModal.create(this.token));
		return uuid;
	}

	public Set<String> getKeySet() {
		return keySet;
	}

	public void setKeySet(Set<String> keySet) {
		this.keySet = keySet;
	}

	public boolean getSelfAllowed() {
		return selfAllowed;
	}

	public void setSelfAllowed(boolean selfAllowed) {
		this.selfAllowed = selfAllowed;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public ZabbixApi getZabbixApi() {
        ZabbixApi zabbixApi = ZABBIX_API_MAP.get(this.token);
        if(zabbixApi == null) {
            try {
                String password = PwdUtils.decrypt(user.getPassword(), user.getLoginName() + user.getId());
                zabbixApi = ZabbixApi.create(this.loginName, password);
                if(zabbixApi != null){
                    ZABBIX_API_MAP.put(this.token, zabbixApi);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.exception(this.getClass(), e, "获取ZabbixApi失败");
            }
        }
		return zabbixApi;
	}
}
