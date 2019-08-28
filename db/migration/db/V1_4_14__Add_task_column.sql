/**

时间：	    2017年2月14日

说明： 	  任务表增加出站IP

当前版本：	ICloudsV3.7.pdm
*/

ALTER TABLE public.iyun_base_tasks ADD stackip VARCHAR(36) NULL;
ALTER TABLE public.iyun_base_tasksh ADD stackip VARCHAR(36) NULL;