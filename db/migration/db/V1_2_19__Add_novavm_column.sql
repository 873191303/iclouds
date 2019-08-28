
/**

文件：	    V1_2_19__Add_novavm_column.sql

时间：	    2016年12月20日

说明： 	  	为云主机增加乐观锁

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
ALTER TABLE public.iyun_nova_vm ADD version int2;
