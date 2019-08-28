
/**

文件：	    V1_2_15__Add_project_column.sql

时间：	    2016年12月17日

说明： 	  租户增加创建时间和修改时间

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
ALTER TABLE public.iyun_keystone_project ADD createddate TIMESTAMP;
ALTER TABLE public.iyun_keystone_project ADD updateddate TIMESTAMP;