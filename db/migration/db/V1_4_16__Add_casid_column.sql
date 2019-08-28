/**

时间：	    2017年2月24日

说明： 	  增加记录资源在cas上的id

当前版本：	ICloudsV3.9.pdm
*/

ALTER TABLE public.cmdb_server_pools2host ADD belongcas VARCHAR(36) NOT NULL;
ALTER TABLE public.cmdb_server_pools2host ADD casid VARCHAR(36) NOT NULL;

ALTER TABLE public.cmdb_server_clusters ADD belongcas VARCHAR(36) NOT NULL;
ALTER TABLE public.cmdb_server_clusters ADD casid VARCHAR(36) NOT NULL;

ALTER TABLE public.cmdb_cap_server2ovelflow ADD belongcas VARCHAR(36) NOT NULL;
ALTER TABLE public.cmdb_cap_server2ovelflow ADD casid VARCHAR(36) NOT NULL;