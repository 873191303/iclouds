
/**

时间：	    2017年3月1日

说明： 	创建云运维系统参数设置2接口类表

*/

DROP VIEW IF EXISTS cmdb_asm_netport_link_view;
CREATE VIEW cmdb_asm_netport_link_view AS
  SELECT info.id AS nid,
    info.accessto,
    info.trunkto,
    info.accessvlan,
    info.trunkvlan,
    aa.masterid as accessmasterid,
    aa.seq AS accessseq,
    aa.mac AS accessmac,
    ( SELECT m.assetname
           FROM cmdb_asm_master m
          WHERE ((m.id)::text = (aa.masterid)::text)) AS accessname,
    tt.seq AS trunkseq,
    tt.mac AS trunkmac,
    tt.masterid as trunkmasterid,
    ( SELECT m.assetname
           FROM cmdb_asm_master m
          WHERE ((m.id)::text = (tt.masterid)::text)) AS trunkname
   FROM ((( SELECT lt.accessto,
            lt.vlan AS accessvlan,
            la.trunkto,
            la.vlan AS trunkvlan,
            n.id
           FROM ((cmdb_asm_netports n
             LEFT JOIN cmdb_asm_linkto lt ON (((lt.accessto)::text = (n.id)::text)))
             LEFT JOIN cmdb_asm_linkto la ON (((la.trunkto)::text = (n.id)::text)))) info
     LEFT JOIN cmdb_asm_netports aa ON (((aa.id)::text = (info.accessto)::text)))
     LEFT JOIN cmdb_asm_netports tt ON (((tt.id)::text = (info.trunkto)::text)));