/**

时间：	    2017年2月16日

说明： 	  去掉名称和描述不为空

前置版本：	ICloudsV3.6.pdm

当前版本：	ICloudsV3.7.pdm
*/

ALTER TABLE iyun_storage_flavor ALTER COLUMN name DROP NOT NULL;
ALTER TABLE iyun_storage_flavor ALTER COLUMN description DROP NOT NULL;