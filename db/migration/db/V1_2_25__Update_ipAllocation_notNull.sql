/**

时间：	    2016年12月30日

说明： 	  修改ip使用表ip字段可以为空

前置版本：	ICloudsV2.9.pdm

当前版本：	ICloudsV2.9.pdm
 */

ALTER TABLE public.iyun_vdc_ipallocations ALTER COLUMN ip_address DROP NOT NULL;