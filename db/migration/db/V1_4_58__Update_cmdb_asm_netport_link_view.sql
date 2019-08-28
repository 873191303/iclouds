DROP VIEW cmdb_asm_netport_link_view;
CREATE VIEW cmdb_asm_netport_link_view AS 
SELECT DISTINCT n.id AS nid,
    l.accessto,
    l.trunkto,
    l.vlan AS accessvlan,
    l.vlan AS trunkvlan,
    na.masterid AS accessmasterid,
    na.seq AS accessseq,
    na.mac AS accessmac,
    ( SELECT mt.assetname
           FROM cmdb_asm_master mt
          WHERE ((na.masterid)::text = (mt.id)::text)) AS accessname,
    nt.seq AS trunkseq,
    nt.mac AS trunkmac,
    nt.masterid AS trunkmasterid,
    ( SELECT m.assetname
           FROM cmdb_asm_master m
          WHERE ((nt.masterid)::text = (m.id)::text)) AS trunkname
   FROM (((cmdb_asm_netports n
     LEFT JOIN cmdb_asm_linkto l ON ((((n.id)::text = (l.trunkto)::text) OR ((n.id)::text = (l.accessto)::text))))
     LEFT JOIN cmdb_asm_netports na ON (((l.accessto)::text = (na.id)::text)))
     LEFT JOIN cmdb_asm_netports nt ON (((l.trunkto)::text = (nt.id)::text)));
