/**

时间：	    2016年12月27日

说明： 	  	镜像表增加格式和操作系统类型

前置版本：	ICloudsV2.9.pdm

当前版本：	ICloudsV2.9.pdm
 */

ALTER TABLE public.iyun_base_rules ADD format varchar(100);
ALTER TABLE public.iyun_base_rules ADD ostype varchar(100);