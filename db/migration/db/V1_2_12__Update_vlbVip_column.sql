
/**

序号：	    23

文件：	    V1_2_12__Update_vlbVip_column.sql

时间：	    2016年12月16日

说明： 	  修改会话持久类型字段名称,增加会话名称字段

影响对象： iyun_vlb_vips

前置版本：	ICloudsV2.8.pdm

当前版本：	ICloudsV2.8.pdm

 */

ALTER TABLE public.iyun_vlb_vips ADD cookie_name VARCHAR(50) NULL;
ALTER TABLE public.iyun_vlb_vips RENAME COLUMN session_persistence TO cookie_type;