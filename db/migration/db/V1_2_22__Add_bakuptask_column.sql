/**

文件：	    V1_2_21__Update_quotause_column.sql

时间：	    2016年12月20日

说明： 	  	增加备份任务表字段

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
ALTER TABLE public.iyun_partner_bakup_node2tasks ADD tasktypename varchar(64);
ALTER TABLE public.iyun_partner_bakup_node2tasks ADD datasourcetypename varchar(64);
ALTER TABLE public.iyun_partner_bakup_node2tasks ADD taskstatusname varchar(64);