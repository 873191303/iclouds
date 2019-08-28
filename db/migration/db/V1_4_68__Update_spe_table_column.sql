/**

时间：	    2017年6月23日

说明： 	  去除网络和网卡的queryFlag字段

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/

ALTER TABLE public.iyun_vdc_network DROP queryflag;
ALTER TABLE public.iyun_vdc_ports DROP queryflag;

ALTER TABLE public.iyun_spe_networks ADD port INT2 NULL;