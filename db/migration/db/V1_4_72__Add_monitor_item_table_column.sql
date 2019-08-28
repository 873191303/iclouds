/**

时间：	    2017年7月5日

说明： 	  监控模块新增主机接口表、监控项表格新增字段

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/
DROP TABLE IF EXISTS "public"."ipm_pft_interface";
CREATE TABLE "public"."ipm_pft_interface" (
  "interfaceid" int8 NOT NULL,
  "hostid" int8 NOT NULL,
  "main" int4 DEFAULT 0 NOT NULL,
  "type" int4 DEFAULT 0 NOT NULL,
  "useip" int4 DEFAULT 1 NOT NULL,
  "ip" varchar(64) COLLATE "default" DEFAULT '127.0.0.1'::character varying NOT NULL,
  "dns" varchar(64) COLLATE "default" DEFAULT ''::character varying NOT NULL,
  "port" varchar(64) COLLATE "default" DEFAULT '10050'::character varying NOT NULL,
  "bulk" int4 DEFAULT 1 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Alter Sequences Owned By
-- ----------------------------

-- ----------------------------
-- Indexes structure for table interface
-- ----------------------------
CREATE INDEX "interface_1" ON "public"."ipm_pft_interface" USING btree ("hostid", "type");
CREATE INDEX "interface_2" ON "public"."ipm_pft_interface" USING btree ("ip", "dns");

-- ----------------------------
-- Primary Key structure for table interface
-- ----------------------------
ALTER TABLE "public"."ipm_pft_interface" ADD PRIMARY KEY ("interfaceid");

-- ----------------------------
-- Foreign Key structure for table "public"."interface"
-- ----------------------------
ALTER TABLE "public"."ipm_pft_interface" ADD FOREIGN KEY ("hostid") REFERENCES "public"."ipm_pft_hosts" ("hostid") ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE public.ipm_pft_items ADD interfaceid INT8 NULL;
ALTER TABLE public.ipm_pft_items ADD units VARCHAR(255) NOT NULL;
ALTER TABLE public.ipm_pft_items ADD description VARCHAR(255) NULL;
ALTER TABLE public.ipm_pft_items ADD delay INT4 NOT NULL;
ALTER TABLE "public"."ipm_pft_items" ADD FOREIGN KEY ("interfaceid") REFERENCES "public"."ipm_pft_interface" ("interfaceid") ON DELETE CASCADE ON UPDATE NO ACTION;
