
/**

文件：	    V1_2_15__Add_project_column.sql

时间：	    2016年12月17日

说明： 	  	用户增加cloudos对应的用户id

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
ALTER TABLE public.iyun_sm_user ADD cloudosid varchar(36);
