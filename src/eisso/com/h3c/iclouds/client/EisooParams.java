package com.h3c.iclouds.client;

public class EisooParams {
	
	/** 请求ip */
	public static final String EISOO_IP = "eisoo.ip";
	
	/** 文件上传参数: 请求是否为https */
	public static final String EISOO_USEHTTPS = "eisoo.usehttps";
	
	/** 文件上传参数: 是否检查重名 */
	public static final String EISOO_ONDUP = "eisoo.ondup";
	
	/** 用户名 */
	public static final String EISOO_ACCOUNT = "eisoo.account";
	
	/** apiid */
	public static final String EISOO_APPID = "eisoo.appid";
	
	/** appkey */
	public static final String EISOO_APPKEY = "eisoo.appkey";
	
	/** 请求方式：post */
	public static final String POST = "post";
	
	/** 请求方式：get */
	public static final String GET = "get";
	
	/** 请求方式：delete */
	public static final String DELETE = "delete";
	
	/** 请求方式：put */
	public static final String PUT = "put";
	
	/** url： 授权 */
	public static final String EISOO_API_AUTH = "eisoo.api.auth1.profix";
	
	/** url： 文件管理 */
	public static final String EISOO_API_FILE = "eisoo.api.file.profix";
	
	/** url： 用户与组织管理 */
	public static final String EISOO_API_USER_ORG = "eisoo.api.user.org.profix";
	
	/** url： 目录管理 */
	public static final String EISOO_API_DIR = "eisoo.api.dir.profix";
	
	/** 请求方法：文件开始上传 */
	public static final String EISOO_BEGIN_UPLOAD_METHOD = "osbeginupload";
	
	/** 请求方法：文件结束上传 */
	public static final String EISOO_END_UPLOAD_METHOD = "osendupload";
	
	/** 请求方法：删除文件 */
	public static final String EISOO_DELETE_FILE_METHOD = "delete";
	
	/** 请求方法：重命名文件 */
	public static final String EISOO_RENAME_FILE_METHOD = "rename";
	
	/** 请求方法：移动文件 */
	public static final String EISOO_MOVE_FILE_METHOD = "move";
	
	/** 请求方法：复制文件 */
	public static final String EISOO_COPY_FILE_METHOD = "copy";
	
	/** 请求方法：获取文件建议名称 */
	public static final String EISOO_GET_FILE_SUGGEST_NAME_METHOD = "getsuggestname";
	
	/** 请求方法：获取文件历史版本 */
	public static final String EISOO_GET_FILE_HISTORY_METHOD = "revisions";
	
	/** 请求方法：还原文件历史版本 */
	public static final String EISOO_RESTORE_FILE_HISTORY_METHOD = "restorerevision";
	
	/** 请求方法：获取文件属性 */
	public static final String EISOO_GET_FILE_ATTRIBUTE_METHOD = "attribute";
	
	/** 请求方法：获取文件元数据 */
	public static final String EISOO_GET_FILE_METADATA_METHOD = "metadata";
	
	/** 请求方法：设置文件密级 */
	public static final String EISOO_SET_FILE_CSF_LEVEL_METHOD = "setcsflevel";
	
	/** 请求方法：添加文件标签 */
	public static final String EISOO_ADD_FILE_TAG_METHOD = "addtag";
	
	/** 请求方法：删除文件标签 */
	public static final String EISOO_DELETE_FILE_TAG_METHOD = "deletetag";
	
	/** 请求方法：登录授权 */
	public static final String EISOO_AUTH_METHOD = "extloginclient";
	
	/** 请求方法：创建用户 */
	public static final String EISOO_CREATE_USER_METHOD = "createuser";
	
	/** 请求方法： 修改用户 */
	public static final String EISOO_EDIT_USER_METHOD = "edituser";
	
	/** 请求方法：删除用户 */
	public static final String EISOO_DELETE_USER_METHOD = "deleteuser";
	
	/** 请求方法：根据id获取用户 */
	public static final String EISOO_GET_USER_BYID_METHOD = "getuserbyid";
	
	/** 请求方法： 根据登录名获取用户 */
	public static final String EISOO_GET_USER_BYNAME_METHOD = "getuserbyname";
	
	/** 请求方法：获取所有用户数目 */
	public static final String EISOO_GET_USER_COUNT_METHOD = "getallusercount";
	
	/** 请求方法：获取所有用户分页列表 */
	public static final String EISOO_GET_PAGE_USER_METHOD = "getalluser";
	
	/** 请求方法：关闭用户个人文档 */
	public static final String EISOO_CLOSE_USER_METHOD = "deleteuserdoc";
	
	/** 请求方法：创建组织 */
	public static final String EISOO_CREATE_ORG_METHOD = "createorg";
	
	/** 请求方法： 修改组织 */
	public static final String EISOO_EDIT_ORG_METHOD = "editorg";
	
	/** 请求方法：删除组织 */
	public static final String EISOO_DELETE_ORG_METHOD = "deleteorg";
	
	/** 请求方法：根据id获取组织 */
	public static final String EISOO_GET_ORG_BYID_METHOD = "getorgbyid";
	
	/** 请求方法： 根据名称获取组织 */
	public static final String EISOO_GET_ORG_BYNAME_METHOD = "getorgbyname";
	
	/** 请求方法：获取所有组织 */
	public static final String EISOO_GET_ORG_METHOD = "getallorg";
	
	/** 请求方法：获取组织下用户 */
	public static final String EISOO_GET_USER_UNDER_ORG_METHOD = "getsubusersbyorgid";
	
	/** 请求方法：创建目录协议 */
	public static final String EISOO_CREATE_DIR_METHOD = "create";
	
	/** 请求方法： 创建多级目录协议 */
	public static final String EISOO_CREATE_MULTI_DIR_METHOD = "createmultileveldir";
	
	/** 请求方法：删除目录协议 */
	public static final String EISOO_DELETE_DIR_METHOD = "delete";
	
	/** 请求方法：重命名目录协议 */
	public static final String EISOO_RENAME_DIR_METHOD = "rename";
	
	/** 请求方法：移动目录协议 */
	public static final String EISOO_MOVE_DIR_METHOD = "move";
	
	/** 请求方法：复制目录协议 */
	public static final String EISOO_COPY_DIR_METHOD = "copy";
	
	/** 请求方法：浏览目录协议 */
	public static final String EISOO_LIST_DIR_METHOD = "list";
	
	/** 请求方法：查看赋值目录协议进度 */
	public static final String EISOO_GET_DIR_COPY_PROGRESS_METHOD = "copyprogress";
	
	/** 请求方法：查看目录建议名称 */
	public static final String EISOO_GET_DIR_SUGGEST_NAME_METHOD = "getsuggestname";
	
	/** 请求方法：获取目录属性 */
	public static final String EISOO_GET_DIR_ATTRIBUTE_METHOD = "attribute";
	
	/** 请求方法：获取目录大小 */
	public static final String EISOO_GET_DIR_SIZE_METHOD = "size";
	
	/** 请求方法： 设置目录密级 */
	public static final String EISOO_SET_DIR_CSF_LEVEL_METHOD = "setcsflevel";
}
