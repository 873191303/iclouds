
/**

时间：	   2017年3月10日

说明： 	 存储相关表格增加与hp关联id

*/

ALTER TABLE public.cmdb_storage_clusters ADD belonghpid VARCHAR(36) NULL;

ALTER TABLE public.cmdb_storage_clusters ADD hpid VARCHAR(36) NULL;

ALTER TABLE public.cmdb_storage_volums ADD belonghpid VARCHAR(36) NULL;

ALTER TABLE public.cmdb_storage_volums ADD hpid VARCHAR(36) NULL;