/**

时间：	    2017年1月11日

说明： 	  	修改日志表名，删除groupid字段

当前版本：	ICloudsV3.2.pdm
*/
alter table iyun_sm_logIn2Logs RENAME to iyun_sm_operateLogs;

ALTER TABLE iyun_sm_operateLogs DROP groupid;