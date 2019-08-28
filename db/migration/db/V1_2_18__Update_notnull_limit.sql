
/**

文件：	    V1_2_18__Update_notnull_limit.sql

时间：	    2016年12月20日

说明： 	  修改数据库的非空限制

前置版本：	ICloudsV2.8.pdm

当前版本：	ICloudsV2.8.pdm
 */

ALTER TABLE public.iyun_vdc_ports ALTER COLUMN mac_address DROP NOT NULL;
ALTER TABLE public.iyun_vdc_ports ALTER COLUMN status DROP NOT NULL;
ALTER TABLE public.iyun_vdc_ports ALTER COLUMN device_id DROP NOT NULL;
ALTER TABLE public.iyun_vdc_ports ALTER COLUMN device_owner DROP NOT NULL;
ALTER TABLE public.iyun_vlb_healthmonitors ALTER COLUMN http_method DROP NOT NULL;
ALTER TABLE public.iyun_vlb_pool2healthmonitors ALTER COLUMN status DROP NOT NULL;
ALTER TABLE public.iyun_vlb_pool2healthmonitors ALTER COLUMN status_descrition DROP NOT NULL;