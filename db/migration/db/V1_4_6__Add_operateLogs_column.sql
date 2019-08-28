/**

时间：	    2017年2月14日

说明： 	  日志表增加资源id和资源名称

当前版本：	ICloudsV3.7.pdm
*/

ALTER TABLE public.iyun_sm_operatelogs ADD resourceid VARCHAR(36) NULL;
ALTER TABLE public.iyun_sm_operatelogs ADD resourcename VARCHAR(50) NULL;