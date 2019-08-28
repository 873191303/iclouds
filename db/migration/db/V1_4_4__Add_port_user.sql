/**

时间：	    2017年2月9日

说明： 	  增加虚拟网卡和用户的关系

前置版本：	ICloudsV3.6.pdm

当前版本：	ICloudsV3.7.pdm
*/

ALTER TABLE iyun_vdc_ports ADD owner VARCHAR(36) NULL;