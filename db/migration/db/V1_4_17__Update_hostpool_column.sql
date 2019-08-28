/**

时间：	    2017年2月24日

说明： 	  修改主机池模块字段

当前版本：	ICloudsV3.9.pdm
*/
ALTER TABLE public.cmdb_cloud_server2vm ALTER COLUMN status TYPE VARCHAR(36) USING status::VARCHAR(36);
ALTER TABLE public.cmdb_cap_server2ovelflow ALTER COLUMN cpuoversize DROP NOT NULL;