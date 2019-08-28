/**

时间：	    2017年6月26日

说明： 	  修改资产表的序列号字段长度

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/


DROP VIEW IF EXISTS cmdb_asm_stack;
ALTER TABLE public.cmdb_asm_master ALTER COLUMN serial TYPE VARCHAR(100);
CREATE VIEW "public"."cmdb_asm_stack" AS
  SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
         ( SELECT t.codeid
           FROM iyun_base_initcode t
           WHERE ((t.id)::text = (m.assettype)::text)) AS typecode,
         ( SELECT t.codename
           FROM iyun_base_initcode t
           WHERE ((t.id)::text = (m.assettype)::text)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
         ( SELECT t.stackid
           FROM cmdb_switch_groups2items t
           WHERE ((t.masterid)::text = (m.id)::text)) AS stackid,
         ( SELECT t.stackname
           FROM cmdb_switch_groups t
           WHERE ((t.id)::text = (( SELECT gi.stackid
                                    FROM cmdb_switch_groups2items gi
                                    WHERE ((gi.masterid)::text = (m.id)::text)
                                    LIMIT 1))::text)) AS stackname
  FROM cmdb_asm_master m
  WHERE ((m.assettype)::text = '8a8a700d57a30ea80157a30eccf70002'::text)
  UNION ALL
  SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
         ( SELECT t.codeid
           FROM iyun_base_initcode t
           WHERE ((t.id)::text = (m.assettype)::text)) AS typecode,
         ( SELECT t.codename
           FROM iyun_base_initcode t
           WHERE ((t.id)::text = (m.assettype)::text)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
         ( SELECT t.stackid
           FROM cmdb_router_groups2items t
           WHERE ((t.masterid)::text = (m.id)::text)) AS stackid,
         ( SELECT t.stackname
           FROM cmdb_router_groups t
           WHERE ((t.id)::text = (( SELECT gi.stackid
                                    FROM cmdb_router_groups2items gi
                                    WHERE ((gi.masterid)::text = (m.id)::text)
                                    LIMIT 1))::text)) AS stackname
  FROM cmdb_asm_master m
  WHERE ((m.assettype)::text = '8a8a700d57a30ea80157a30eccf60001'::text);

