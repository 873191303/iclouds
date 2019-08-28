
/**

序号：	    10

文件：	    V1_1_9__Update_vdc_column.sql

时间：	    2016年11月29日

说明： 	  增删字段

影响对象：	iyun_vdc_ports,iyun_vdc_route

前置版本：	ICloudsV2.4.pdm

当前版本：	ICloudsV2.4.pdm
 */
ALTER TABLE public.iyun_vdc_ports DROP network_id;
ALTER TABLE public.iyun_vdc_route ADD fwid VARCHAR(50) NULL;