
/**

序号：	    9

文件：	    V1_1_8__Update_vdc_column.sql

时间：	    2016年11月26日

说明： 	  增删字段

影响对象：	iyun_vdc_network,iyun_vdc_ipallocations

前置版本：	ICloudsV2.3.pdm

当前版本：	ICloudsV2.3.pdm
 */
ALTER TABLE iyun_vdc_network ADD route_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_ipallocations DROP network_id;