/**

时间：	    2017年2月28日

说明： 	  存储容量管理表新增保存获取的hpId字段

当前版本：	ICloudsV3.9.pdm
*/
ALTER TABLE public.cmdb_cap_storage2ovelflow ADD hpId VARCHAR(36) NOT NULL;
