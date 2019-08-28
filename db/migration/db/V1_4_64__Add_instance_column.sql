/**

时间：	    2017年6月8日

说明： 	  云资源计量对象增加资源名称字段

前置版本：	ICloudsV4.8.pdm

当前版本：	ICloudsV4.8.pdm
*/
ALTER TABLE public.iyun_measure_instaces ADD name VARCHAR(100) NULL;