
/**

时间：	   2017年3月1日

说明： 	 虚拟机新增casid和锁住cas字段

*/
ALTER TABLE public.cmdb_cloud_server2vm ADD casid VARCHAR(36) NOT NULL;
ALTER TABLE public.cmdb_cloud_server2vm ADD belongcas VARCHAR(36) NOT NULL;