/**

时间：	    2017-07-28 10:05:31

说明： 	  新增自助监控表

前置版本：	ICloudsV5.2.pdm

当前版本：	ICloudsV5.2.pdm
*/

-- ----------------------------
-- Table structure for cmdb_co_selfmonitor
-- ----------------------------
DROP TABLE IF EXISTS "public"."ipm_pft_selfmonitor";
CREATE TABLE "public"."ipm_pft_selfmonitor" (
"id" varchar(32) COLLATE "default" NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"ip" varchar(32) COLLATE "default" DEFAULT NULL::character varying,
"tenantid" varchar(36) COLLATE "default" NOT NULL,
"objectid" varchar(32) COLLATE "default" NOT NULL,
"userid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"hostid" INT8 NOT NULL,
"objtype" varchar(32) COLLATE "default" NOT NULL,
"params" varchar(255) COLLATE "default" NOT NULL,
"createdbyname" varchar(50) COLLATE "default" DEFAULT NULL::character varying
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Alter Sequences Owned By
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table cmdb_co_selfmonitor
-- ----------------------------
ALTER TABLE "public"."ipm_pft_selfmonitor" ADD PRIMARY KEY ("id");
