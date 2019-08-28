/**

文件：	    V1_2_21__Update_quotause_column.sql

时间：	    2016年12月20日

说明： 	  	修改配额使用表

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
ALTER TABLE public.iyun_quota_tenant_used ADD version int2 default 0;


