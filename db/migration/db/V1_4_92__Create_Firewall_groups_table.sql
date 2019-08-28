DROP VIEW IF EXISTS cmdb_asm_stack;
DROP TABLE IF EXISTS "public"."cmdb_firewall_groups2items";
DROP TABLE IF EXISTS "public"."cmdb_firewall_groups";

-- ----------------------------
-- Table structure for cmdb_firewall_groups
-- ----------------------------
CREATE TABLE "public"."cmdb_firewall_groups" (
  "id"          VARCHAR(36) COLLATE "default"  NOT NULL,
  "stackname"   VARCHAR(100) COLLATE "default" NOT NULL,
  "stacktype"   VARCHAR(50) COLLATE "default",
  "pstackid"    VARCHAR(50) COLLATE "default",
  "remark"      VARCHAR(500) COLLATE "default",
  "groupid"     VARCHAR(36) COLLATE "default"  NOT NULL,
  "createdby"   VARCHAR(36) COLLATE "default"  NOT NULL,
  "createddate" TIMESTAMP(6)                   NOT NULL,
  "updatedby"   VARCHAR(36) COLLATE "default"  NOT NULL,
  "updateddate" TIMESTAMP(6)                   NOT NULL
)
WITH (OIDS = FALSE
);
ALTER TABLE "public"."cmdb_firewall_groups"
  ADD PRIMARY KEY ("id");

-- ----------------------------
-- Table structure for cmdb_firewall_groups2items
-- ----------------------------

CREATE TABLE "public"."cmdb_firewall_groups2items" (
  "id"          VARCHAR(50) COLLATE "default" NOT NULL,
  "stackid"     VARCHAR(50) COLLATE "default",
  "masterid"    VARCHAR(50) COLLATE "default",
  "remark"      VARCHAR(500) COLLATE "default",
  "groupid"     VARCHAR(36) COLLATE "default" NOT NULL,
  "createdby"   VARCHAR(36) COLLATE "default" NOT NULL,
  "createddate" TIMESTAMP(6)                  NOT NULL,
  "updatedby"   VARCHAR(36) COLLATE "default" NOT NULL,
  "updateddate" TIMESTAMP(6)                  NOT NULL
)
WITH (OIDS = FALSE
);

ALTER TABLE "public"."cmdb_firewall_groups2items"
  ADD PRIMARY KEY ("id");

ALTER TABLE "public"."cmdb_firewall_groups2items"
  ADD FOREIGN KEY ("stackid") REFERENCES "public"."cmdb_firewall_groups" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_firewall_groups2items"
  ADD FOREIGN KEY ("masterid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- View structure for cmdb_asm_stack
-- ----------------------------
CREATE VIEW cmdb_asm_stack AS (
SELECT
  m.id                       AS mid,
  m.assetid,
  m.serial,
  m.assettype,
  (SELECT t.codeid
   FROM iyun_base_initcode t
   WHERE t.id = m.assettype) AS typecode,
  (SELECT t.codename
   FROM iyun_base_initcode t
   WHERE t.id = m.assettype) AS typename,
  m.depart,
  m.assetuser,
  m.status,
  m.iloip,
  m.mmac,
  (SELECT t.stackid
   FROM cmdb_switch_groups2items t
   WHERE t.masterid = m.id)  AS stackid,
  (SELECT t.stackname
   FROM cmdb_switch_groups t
   WHERE t.id = (SELECT gi.stackid
                 FROM cmdb_switch_groups2items gi
                 WHERE gi.masterid = m.id
                 LIMIT 1))   AS stackname
FROM cmdb_asm_master m
WHERE (SELECT t.codeid
       FROM iyun_base_initcode t
       WHERE t.id = m.assettype) = 'switch'
UNION ALL
SELECT
  m.id                       AS mid,
  m.assetid,
  m.serial,
  m.assettype,
  (SELECT t.codeid
   FROM iyun_base_initcode t
   WHERE t.id = m.assettype) AS typecode,
  (SELECT t.codename
   FROM iyun_base_initcode t
   WHERE t.id = m.assettype) AS typename,
  m.depart,
  m.assetuser,
  m.status,
  m.iloip,
  m.mmac,
  (SELECT t.stackid
   FROM cmdb_router_groups2items t
   WHERE t.masterid = m.id)  AS stackid,
  (SELECT t.stackname
   FROM cmdb_router_groups t
   WHERE t.id = (SELECT gi.stackid
                 FROM cmdb_router_groups2items gi
                 WHERE gi.masterid = m.id
                 LIMIT 1))   AS stackname
FROM cmdb_asm_master m
WHERE (SELECT t.codeid
       FROM iyun_base_initcode t
       WHERE t.id = m.assettype) = 'router'
UNION ALL
SELECT
  m.id                       AS mid,
  m.assetid,
  m.serial,
  m.assettype,
  (SELECT t.codeid
   FROM iyun_base_initcode t
   WHERE t.id = m.assettype) AS typecode,
  (SELECT t.codename
   FROM iyun_base_initcode t
   WHERE t.id = m.assettype) AS typename,
  m.depart,
  m.assetuser,
  m.status,
  m.iloip,
  m.mmac,
  (SELECT t.stackid
   FROM cmdb_firewall_groups2items t
   WHERE t.masterid = m.id)  AS stackid,
  (SELECT t.stackname
   FROM cmdb_firewall_groups t
   WHERE t.id = (SELECT gi.stackid
                 FROM cmdb_firewall_groups2items gi
                 WHERE gi.masterid = m.id
                 LIMIT 1))   AS stackname
FROM cmdb_asm_master m
WHERE (SELECT t.codeid
       FROM iyun_base_initcode t
       WHERE t.id = m.assettype) = 'firewall'
);