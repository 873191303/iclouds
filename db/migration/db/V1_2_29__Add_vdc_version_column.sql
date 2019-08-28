/**

时间：	    2017年1月5日

说明： 	  vdc表增加版本控制字段

前置版本：	ICloudsV2.9.pdm

当前版本：	ICloudsV2.9.pdm
*/

ALTER TABLE public.iyun_base_vdc ADD userid VARCHAR(50) NULL;
ALTER TABLE public.iyun_base_vdc ADD sessionid VARCHAR(50) NULL;
ALTER TABLE public.iyun_base_vdc ADD version VARCHAR(50) NULL;