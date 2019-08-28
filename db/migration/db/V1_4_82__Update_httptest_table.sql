/**

时间：	    2017-07-31

说明： 	  修改http监控表

前置版本：	ICloudsV5.2.pdm

当前版本：	ICloudsV5.2.pdm
*/

ALTER TABLE public.ipm_pft_httptest2item DROP CONSTRAINT fk_ipm_pft__reference_ipm_pft_20;

ALTER TABLE public.ipm_pft_httpstep DROP CONSTRAINT fk_ipm_pft__reference_ipm_pft_18;


-- ----------------------------
-- Table structure for ipm_pft_httptest
-- ----------------------------
DROP TABLE IF EXISTS "public"."ipm_pft_httptest";
CREATE TABLE "public"."ipm_pft_httptest" (
"httptestid" INT8 NOT NULL,
"name" VARCHAR(64) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"displayname" VARCHAR(64) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"applicationid" INT8,
"nextcheck" INT4 DEFAULT 0 NOT NULL,
"delay" INT4 DEFAULT 60 NOT NULL,
"status" INT4 DEFAULT 0 NOT NULL,
"variables" TEXT COLLATE "default" DEFAULT ''::TEXT NOT NULL,
"agent" VARCHAR(255) COLLATE "default" DEFAULT 'Zabbix'::CHARACTER VARYING NOT NULL,
"authentication" INT4 DEFAULT 0 NOT NULL,
"http_user" VARCHAR(64) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"http_password" VARCHAR(64) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"hostid" INT8 NOT NULL,
"templateid" INT8,
"http_proxy" VARCHAR(255) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"retries" INT4 DEFAULT 1 NOT NULL,
"ssl_cert_file" VARCHAR(255) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"ssl_key_file" VARCHAR(255) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"ssl_key_password" VARCHAR(64) COLLATE "default" DEFAULT ''::CHARACTER VARYING NOT NULL,
"verify_peer" INT4 DEFAULT 0 NOT NULL,
"verify_host" INT4 DEFAULT 0 NOT NULL,
"headers" TEXT COLLATE "default" DEFAULT ''::TEXT NOT NULL,
"CreatedBy"     VARCHAR(36)  NOT NULL,
"CreatedDate"   TIMESTAMP    NOT NULL,
"UpdatedBy"     VARCHAR(36)  NOT NULL,
"UpdatedDate"   TIMESTAMP    NOT NULL,
"tenantid"    VARCHAR(50) NULL,
"owner"       VARCHAR(36) NULL
)
WITH (OIDS=FALSE)

;
-- ----------------------------
-- Indexes structure for table ipm_pft_httptest
-- ----------------------------
CREATE INDEX "httptest_1" ON "public"."ipm_pft_httptest" USING BTREE ("applicationid");
CREATE UNIQUE INDEX "httptest_2" ON "public"."ipm_pft_httptest" USING BTREE ("hostid", "name");
CREATE INDEX "httptest_3" ON "public"."ipm_pft_httptest" USING BTREE ("status");
CREATE INDEX "httptest_4" ON "public"."ipm_pft_httptest" USING BTREE ("templateid");

-- ----------------------------
-- Primary Key structure for table ipm_pft_httptest
-- ----------------------------
ALTER TABLE "public"."ipm_pft_httptest" ADD PRIMARY KEY ("httptestid");

-- ----------------------------
-- Foreign Key structure for table "public"."ipm_pft_httptest"
-- ----------------------------
ALTER TABLE "public"."ipm_pft_httptest" ADD FOREIGN KEY ("templateid") REFERENCES "public"."ipm_pft_httptest" ("httptestid") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "public"."ipm_pft_httptest" ADD FOREIGN KEY ("applicationid") REFERENCES "public"."ipm_pft_applications" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."ipm_pft_httptest" ADD FOREIGN KEY ("hostid") REFERENCES "public"."ipm_pft_hosts" ("hostid") ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE "public"."ipm_pft_httptest2item" ADD FOREIGN KEY ("httptestid") REFERENCES "public"."ipm_pft_httptest" ("httptestid") ON DELETE CASCADE ON UPDATE NO ACTION;
ALTER TABLE "public"."ipm_pft_httpstep" ADD FOREIGN KEY ("httptestid") REFERENCES "public"."ipm_pft_httptest" ("httptestid") ON DELETE CASCADE ON UPDATE NO ACTION;

ALTER TABLE public.ipm_pft_items ADD serviceid VARCHAR(100) NULL;

ALTER TABLE public.ipm_pft_triggers ADD mode VARCHAR(100) NULL;

ALTER TABLE public.ipm_pft_triggers DROP source_;
ALTER TABLE public.ipm_pft_triggers DROP sourcetype_;