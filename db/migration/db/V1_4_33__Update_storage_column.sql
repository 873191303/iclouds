/**

时间：	    2017年3月10日

说明： 	  修改存储集群与卷的字段

前置版本：	ICloudsV4.1.pdm

当前版本：	ICloudsV4.2.pdm
*/
ALTER TABLE public.cmdb_storage_clusters ADD type VARCHAR(36) NULL;

ALTER TABLE public.cmdb_storage_volums ADD usedSize FLOAT8 NULL;
ALTER TABLE public.cmdb_storage_volums ADD unit VARCHAR(36) NULL;
ALTER TABLE public.cmdb_storage_volums ADD targetName VARCHAR(36) NULL;
ALTER TABLE public.cmdb_storage_volums ADD wwn VARCHAR(36) NULL;
ALTER TABLE public.cmdb_storage_volums ADD iqn VARCHAR(36) NULL;
ALTER TABLE public.cmdb_storage_volums ADD vip VARCHAR(36) NULL;
ALTER TABLE public.cmdb_storage_volums ALTER COLUMN volumename TYPE VARCHAR(36) USING volumename::VARCHAR(36);