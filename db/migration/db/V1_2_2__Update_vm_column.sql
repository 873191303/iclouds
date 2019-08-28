
/**

序号：	    13

文件：	    V1_2_12__Add_vm_column.sql

时间：	    2016年12月1日

说明： 	  修改字段

影响对象：	iyun_vm_vlb

前置版本：	ICloudsV2.4.pdm

当前版本：	ICloudsV2.4.pdm
 */

ALTER TABLE public.iyun_nova_vm RENAME COLUMN memory_mb TO memory;
ALTER TABLE public.iyun_nova_vm RENAME COLUMN ramdisk_mb TO ramdisk;