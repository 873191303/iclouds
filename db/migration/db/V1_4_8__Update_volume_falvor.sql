/**

时间：	    2017年2月16日

说明： 	  云硬盘规格表

前置版本：	ICloudsV3.6.pdm

当前版本：	ICloudsV3.7.pdm
*/

ALTER TABLE iyun_storage_flavor DROP COLUMN azone_uuid;
ALTER TABLE iyun_storage_flavor DROP COLUMN user_name;
ALTER TABLE iyun_storage_flavor DROP COLUMN location;
ALTER TABLE iyun_storage_flavor ADD COLUMN size INT8;
ALTER TABLE iyun_storage_flavor ADD COLUMN volumetype VARCHAR(50);