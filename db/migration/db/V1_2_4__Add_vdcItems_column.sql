
/**

序号：	    15

文件：	    V1_2_4__Add_vdcItems_column.sql

时间：	    2016年12月5日

说明： 	  增加字段

影响对象：	iyun_vdc_items

前置版本：	ICloudsV2.4.pdm

当前版本：	ICloudsV2.4.pdm

 */
 
ALTER TABLE public.iyun_vdc_items ADD status VARCHAR(50) NULL;