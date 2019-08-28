
/**

序号：	    18

文件：	    V1_2_7__Update_task_column.sql

时间：	    2016年12月7日

说明： 	  修改队列任务表字段

影响对象： iyun_base_tasksh,iyun_base_task2exec

前置版本：	ICloudsV2.6.pdm

当前版本：	ICloudsV2.6.pdm

 */
ALTER TABLE public.iyun_base_tasksh DROP CONSTRAINT fk_iyun_bas_reference_iyun_bas2;
ALTER TABLE public.iyun_base_task2exec DROP CONSTRAINT fk_iyun_bas_reference_iyun_bas1;
ALTER TABLE public.iyun_base_task2exec DROP hid;
ALTER TABLE public.iyun_base_tasksh DROP tid;

