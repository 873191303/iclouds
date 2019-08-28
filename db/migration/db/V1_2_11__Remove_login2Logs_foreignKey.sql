
/**

序号：	    22

文件：	    V1_2_11__Remove_login2Logs_foreignKey.sql

时间：	    2016年12月15日

说明： 	  移除登录日志外键

影响对象： iyun_sm_login2logs

前置版本：	ICloudsV2.8.pdm

当前版本：	ICloudsV2.8.pdm

 */

ALTER TABLE public.iyun_sm_login2logs DROP CONSTRAINT iyun_sm_login2logs_userid_fkey;