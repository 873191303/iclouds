/**

时间：	    2017年2月24日

说明： 	  修改主机内存字段长度为bigint;修改云主机的外键主机id

当前版本：	ICloudsV3.9.pdm
*/
DROP VIEW if EXISTS clusters_server2vm;

ALTER TABLE public.cmdb_cap_server2ovelflow ALTER COLUMN ram TYPE BIGINT USING ram::BIGINT;

ALTER TABLE public.cmdb_cloud_server2vm ALTER COLUMN memory TYPE BIGINT USING memory::BIGINT;

ALTER TABLE public.cmdb_cloud_server2vm DROP CONSTRAINT cmdb_cloud_server2vm_hostid_fkey;
ALTER TABLE public.cmdb_cloud_server2vm
  ADD CONSTRAINT cmdb_cloud_server2vm_hostid_fkey
FOREIGN KEY (hostid) REFERENCES cmdb_cap_server2ovelflow (id);

CREATE VIEW clusters_server2vm AS
  (SELECT ic.id,
    ic.hostid,
    ic.cpu,
    ic.memory,
    ic.storage,
    ic.custertid,
    clus.phostid
  FROM ( SELECT serv.id,
           serv.hostid,
           serv.cpu,
           serv.memory,
           serv.storage,
           item.custertid
         FROM cmdb_cloud_server2vm serv,
           cmdb_server_cluster2items item
         WHERE ((item.id)::text = (serv.hostid)::text)) ic,
    cmdb_server_clusters clus
  WHERE ((clus.id)::text = (ic.custertid)::text));