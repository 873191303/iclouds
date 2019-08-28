ALTER TABLE public.cmdb_netdevice_groups ADD isstandalone CHAR NULL;
ALTER TABLE public.cmdb_netdevice_vPorts ADD passet VARCHAR(100) NULL;
ALTER TABLE public.cmdb_netdevice_vPorts ADD pport VARCHAR(100) NULL;

ALTER TABLE public.iyun_instance_bilings ALTER COLUMN num TYPE BIGINT USING num::BIGINT;

DROP VIEW IF EXISTS cmdb_asm_stack;
CREATE VIEW cmdb_asm_stack AS (
 SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
    ( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typecode,
    ( SELECT t.codename
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
    ( SELECT t.stackid
           FROM cmdb_switch_groups2items t
          WHERE ((t.masterid)::TEXT = (m.id)::TEXT)) AS stackid,
    ( SELECT t.stackname
           FROM cmdb_switch_groups t
          WHERE ((t.id)::TEXT = (( SELECT gi.stackid
                   FROM cmdb_switch_groups2items gi
                  WHERE ((gi.masterid)::TEXT = (m.id)::TEXT)
                 LIMIT 1))::TEXT)) AS stackname
   FROM cmdb_asm_master m
  WHERE ((( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)))::TEXT = 'switch'::TEXT)
UNION ALL
 SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
    ( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typecode,
    ( SELECT t.codename
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
    ( SELECT t.stackid
           FROM cmdb_router_groups2items t
          WHERE ((t.masterid)::TEXT = (m.id)::TEXT)) AS stackid,
    ( SELECT t.stackname
           FROM cmdb_router_groups t
          WHERE ((t.id)::TEXT = (( SELECT gi.stackid
                   FROM cmdb_router_groups2items gi
                  WHERE ((gi.masterid)::TEXT = (m.id)::TEXT)
                 LIMIT 1))::TEXT)) AS stackname
   FROM cmdb_asm_master m
  WHERE ((( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)))::TEXT = 'router'::TEXT)
UNION ALL
 SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
    ( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typecode,
    ( SELECT t.codename
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
    ( SELECT t.stackid
           FROM cmdb_netdevice_group2items t
          WHERE ((t.masterid)::TEXT = (m.id)::TEXT)) AS stackid,
    ( SELECT t.stackname
           FROM cmdb_netdevice_groups t
          WHERE ((t.id)::TEXT = (( SELECT gi.stackid
                   FROM cmdb_firewall_groups2items gi
                  WHERE ((gi.masterid)::TEXT = (m.id)::TEXT)
                 LIMIT 1))::TEXT)) AS stackname
   FROM cmdb_asm_master m
  WHERE ((( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)))::TEXT = 'firewall'::TEXT)
UNION ALL
 SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
    ( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typecode,
    ( SELECT t.codename
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
    ( SELECT t.cid
           FROM cmdb_storagedevice_group2items t
          WHERE ((t.masterid)::TEXT = (m.id)::TEXT)) AS stackid,
    ( SELECT t.name
           FROM cmdb_storagedevice_clusters t
          WHERE ((t.id)::TEXT = (( SELECT gi.cid
                   FROM cmdb_storagedevice_group2items gi
                  WHERE ((gi.masterid)::TEXT = (m.id)::TEXT)
                 LIMIT 1))::TEXT)) AS stackname
   FROM cmdb_asm_master m
  WHERE ((( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::TEXT = (m.assettype)::TEXT)))::TEXT = 'stock'::TEXT)
);