
/**

序号：	    21

文件：	    V1_2_10__Update_vlbVip_column.sql

时间：	    2016年12月12日

说明： 	  修改负载均衡成员字段名称

影响对象： iyun_vlb_vips

前置版本：	ICloudsV2.8.pdm

当前版本：	ICloudsV2.8.pdm

 */

ALTER TABLE public.iyun_vlb_vips RENAME COLUMN "session_persistence " TO session_persistence;