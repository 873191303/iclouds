/**

时间：	    2017年6月8日

说明： 	  网络和网卡新增字段区分特殊网络

前置版本：	ICloudsV4.8.pdm

当前版本：	ICloudsV4.8.pdm
*/
ALTER TABLE public.iyun_vdc_network ADD queryflag BOOLEAN DEFAULT FALSE NULL;
ALTER TABLE public.iyun_vdc_ports ADD queryflag BOOLEAN DEFAULT FALSE NULL;