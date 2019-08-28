/*
Navicat PGSQL Data Transfer

Source Server         : 10.74.41.222
Source Server Version : 90214
Source Host           : 10.74.41.222:5432
Source Database       : iyun
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90214
File Encoding         : 65001

Date: 2016-11-01 19:00:43
*/


-- ----------------------------
-- Table structure for auto_sync_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."auto_sync_log";
CREATE TABLE "public"."auto_sync_log" (
"id" varchar(50) COLLATE "default" NOT NULL,
"logcollectid" varchar(50) COLLATE "default",
"syncid" varchar(50) COLLATE "default" NOT NULL,
"devicetype" varchar(50) COLLATE "default",
"deviceid" varchar(50) COLLATE "default",
"result" varchar(50) COLLATE "default",
"failreason" varchar(50) COLLATE "default",
"syncdate" timestamp(6)
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."auto_sync_log"."result" IS '0:成功，1:失败';

-- ----------------------------
-- Records of auto_sync_log
-- ----------------------------

-- ----------------------------
-- Table structure for auto_sync_logcollect
-- ----------------------------
DROP TABLE IF EXISTS "public"."auto_sync_logcollect";
CREATE TABLE "public"."auto_sync_logcollect" (
"id" varchar(36) COLLATE "default" NOT NULL,
"syncid" varchar(50) COLLATE "default" NOT NULL,
"syncstarttime" timestamp(6),
"syncendtime" timestamp(6),
"status" varchar(50) COLLATE "default",
"synctimes" int2,
"syncsecond" float4,
"synccollectcontent" varchar(500) COLLATE "default"
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."auto_sync_logcollect"."status" IS '0:成功，1:失败';
COMMENT ON COLUMN "public"."auto_sync_logcollect"."synccollectcontent" IS '成功条数，失败条数，';

-- ----------------------------
-- Records of auto_sync_logcollect
-- ----------------------------

-- ----------------------------
-- Table structure for auto_sync_taskdispactchh
-- ----------------------------
DROP TABLE IF EXISTS "public"."auto_sync_taskdispactchh";
CREATE TABLE "public"."auto_sync_taskdispactchh" (
"id" varchar(36) COLLATE "default" NOT NULL,
"syncid" varchar(50) COLLATE "default" NOT NULL,
"starttime" timestamp(6),
"startdate" date,
"endtype" varchar(50) COLLATE "default",
"enddate" date,
"endtimes" int2,
"safetype" varchar(50) COLLATE "default",
"safetimes" int2,
"periodtype" int2,
"dayinterval" int2,
"weekinterval" varchar(50) COLLATE "default",
"monthinterval" int2,
"monthtype" varchar(50) COLLATE "default",
"monthday" varchar(50) COLLATE "default",
"yearinterval" int2,
"yeartype" varchar(50) COLLATE "default",
"yearday" varchar(50) COLLATE "default",
"previousupdateby" varchar(36) COLLATE "default",
"previousupdatedate" timestamp(6),
"remark" varchar(50) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of auto_sync_taskdispactchh
-- ----------------------------

-- ----------------------------
-- Table structure for auto_sync_taskdispatch
-- ----------------------------
DROP TABLE IF EXISTS "public"."auto_sync_taskdispatch";
CREATE TABLE "public"."auto_sync_taskdispatch" (
"id" varchar(36) COLLATE "default" NOT NULL,
"dbkey" varchar(50) COLLATE "default",
"synctype" varchar(50) COLLATE "default",
"starttime" timestamp(6),
"startdate" date,
"endtype" varchar(50) COLLATE "default",
"enddate" date,
"endtimes" int2,
"endovertimes" int2,
"safetype" varchar(50) COLLATE "default",
"safetimes" int2,
"periodtype" int2,
"dayinterval" int2,
"weekinterval" varchar(50) COLLATE "default",
"monthinterval" int2,
"monthtype" varchar(50) COLLATE "default",
"monthday" varchar(50) COLLATE "default",
"yearinterval" int2,
"yeartype" varchar(50) COLLATE "default",
"yearday" varchar(50) COLLATE "default",
"status" varchar(50) COLLATE "default",
"nextsynctime" timestamp(6),
"remark" varchar(50) COLLATE "default",
"mail" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of auto_sync_taskdispatch
-- ----------------------------

-- ----------------------------
-- Table structure for bus_cis_contact
-- ----------------------------
DROP TABLE IF EXISTS "public"."bus_cis_contact";
CREATE TABLE "public"."bus_cis_contact" (
"id" varchar(36) COLLATE "default" NOT NULL,
"cusid" varchar(36) COLLATE "default",
"cname" varchar(50) COLLATE "default",
"sex" char(1) COLLATE "default",
"age" int4,
"birthday" timestamp(6),
"tel" varchar(50) COLLATE "default",
"mobile" varchar(50) COLLATE "default",
"wxh" varchar(50) COLLATE "default",
"email" varchar(50) COLLATE "default",
"position" varchar(500) COLLATE "default",
"busiscope" varchar(500) COLLATE "default",
"disposition" varchar(500) COLLATE "default",
"interest" varchar(500) COLLATE "default",
"owner" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for bus_cis_custom
-- ----------------------------
DROP TABLE IF EXISTS "public"."bus_cis_custom";
CREATE TABLE "public"."bus_cis_custom" (
"id" varchar(36) COLLATE "default" NOT NULL,
"cusname" varchar(500) COLLATE "default" NOT NULL,
"lvls" varchar(36) COLLATE "default",
"custintroduction" text COLLATE "default",
"manbusiness" varchar(100) COLLATE "default",
"adress" varchar(50) COLLATE "default",
"tel" varchar(50) COLLATE "default",
"url" varchar(50) COLLATE "default",
"fax" varchar(50) COLLATE "default",
"email" varchar(50) COLLATE "default",
"turnover" float8,
"owner" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"status" varchar(1) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for bus_req_items
-- ----------------------------
DROP TABLE IF EXISTS "public"."bus_req_items";
CREATE TABLE "public"."bus_req_items" (
"id" varchar(36) COLLATE "default" NOT NULL,
"reqid" varchar(36) COLLATE "default" NOT NULL,
"classid" varchar(36) COLLATE "default",
"reqtype" varchar(36) COLLATE "default" NOT NULL,
"ajson" varchar(1000) COLLATE "default" NOT NULL,
"oitemid" varchar(36) COLLATE "default",
"status" char(1) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"nums" int2 NOT NULL,
"reqtime" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for bus_req_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."bus_req_master";
CREATE TABLE "public"."bus_req_master" (
"id" varchar(36) COLLATE "default" NOT NULL,
"reqcode" varchar(50) COLLATE "default",
"step" varchar(36) COLLATE "default",
"responsible" varchar(36) COLLATE "default",
"projectname" varchar(100) COLLATE "default",
"issign" varchar(36) COLLATE "default",
"contract" varchar(50) COLLATE "default",
"amount" float8,
"projectdesc" varchar(500) COLLATE "default",
"cusid" varchar(36) COLLATE "default",
"contact" varchar(50) COLLATE "default",
"iphone" varchar(50) COLLATE "default",
"email" varchar(50) COLLATE "default",
"status" varchar(36) COLLATE "default",
"chgflag" char(1) COLLATE "default",
"srcreqid" varchar(36) COLLATE "default",
"instanceid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"workflowid" varchar(36) COLLATE "default",
"version" int2,
"slaflag" varchar(36) COLLATE "default",
"priority" varchar(36) COLLATE "default",
"slalvl" varchar(36) COLLATE "default",
"reqftime" timestamp(6),
"actftime" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for bus_req_master2approvelog
-- ----------------------------
DROP TABLE IF EXISTS "public"."bus_req_master2approvelog";
CREATE TABLE "public"."bus_req_master2approvelog" (
"id" varchar(36) COLLATE "default" NOT NULL,
"reqid" varchar(36) COLLATE "default",
"insid" varchar(36) COLLATE "default" NOT NULL,
"step" varchar(100) COLLATE "default" NOT NULL,
"taskid" varchar(36) COLLATE "default" NOT NULL,
"option" varchar(36) COLLATE "default" NOT NULL,
"comment" varchar(500) COLLATE "default",
"approver" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for chg_base_class
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_base_class";
CREATE TABLE "public"."chg_base_class" (
"id" varchar(36) COLLATE "default" NOT NULL,
"itemdesc" varchar(100) COLLATE "default",
"shortname" varchar(100) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"isdisable" char(1) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_base_class
-- ----------------------------

-- ----------------------------
-- Table structure for chg_base_class2approver
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_base_class2approver";
CREATE TABLE "public"."chg_base_class2approver" (
"id" varchar(36) COLLATE "default" NOT NULL,
"itemid" varchar(36) COLLATE "default" NOT NULL,
"authstandardor" varchar(100) COLLATE "default" NOT NULL,
"authbdirctor" varchar(100) COLLATE "default" NOT NULL,
"authdispatchor" varchar(100) COLLATE "default" NOT NULL,
"authmonitor" varchar(100) COLLATE "default" NOT NULL,
"authaccounter" varchar(100) COLLATE "default" NOT NULL,
"authbakuper" varchar(100) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_base_class2approver
-- ----------------------------

-- ----------------------------
-- Table structure for chg_ins_exttask
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_ins_exttask";
CREATE TABLE "public"."chg_ins_exttask" (
"id" varchar(36) COLLATE "default" NOT NULL,
"chgid" varchar(36) COLLATE "default",
"ismonitor" char(1) COLLATE "default" NOT NULL,
"mcontect" varchar(100) COLLATE "default",
"iscmdb" char(1) COLLATE "default" NOT NULL,
"ccontect" varchar(100) COLLATE "default" NOT NULL,
"isaccount" char(1) COLLATE "default",
"acontect" varchar(100) COLLATE "default",
"isbakup" char(1) COLLATE "default",
"bcontect" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_ins_exttask
-- ----------------------------

-- ----------------------------
-- Table structure for chg_ins_statmachin
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_ins_statmachin";
CREATE TABLE "public"."chg_ins_statmachin" (
"id" varchar(36) COLLATE "default" NOT NULL,
"changeid" varchar(36) COLLATE "default" NOT NULL,
"state" varchar(36) COLLATE "default" NOT NULL,
"seq" int2 NOT NULL,
"approver" varchar(200) COLLATE "default" NOT NULL,
"emails" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_ins_statmachin
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_app2attach
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_app2attach";
CREATE TABLE "public"."chg_req_app2attach" (
"id" varchar(36) COLLATE "default" NOT NULL,
"chgid" varchar(36) COLLATE "default",
"attachid" varchar(36) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_app2attach
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_app2director
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_app2director";
CREATE TABLE "public"."chg_req_app2director" (
"id" varchar(36) COLLATE "default" NOT NULL,
"changeid" varchar(36) COLLATE "default" NOT NULL,
"isapprovescheme" char(1) COLLATE "default",
"isapprovetime" char(1) COLLATE "default",
"isapproveventure" char(1) COLLATE "default",
"isapproveprevent" char(1) COLLATE "default",
"grade" char(1) COLLATE "default",
"opinion" char(1) COLLATE "default",
"otherdirtector" varchar(100) COLLATE "default",
"emails" varchar(100) COLLATE "default",
"state" varchar(36) COLLATE "default" NOT NULL,
"flag" timestamp(6) NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_app2director
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_app2dispatch
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_app2dispatch";
CREATE TABLE "public"."chg_req_app2dispatch" (
"id" varchar(36) COLLATE "default" NOT NULL,
"chgid" varchar(36) COLLATE "default" NOT NULL,
"cabopinion" varchar(100) COLLATE "default",
"cabattache" varchar(100) COLLATE "default",
"levels" char(1) COLLATE "default",
"result" varchar(100) COLLATE "default",
"isrelease" char(1) COLLATE "default",
"assiger" varchar(100) COLLATE "default",
"begtime" timestamp(6),
"endtime" timestamp(6),
"supporter" varchar(100) COLLATE "default",
"isimpartconfadmin" char(1) COLLATE "default",
"emails" varchar(100) COLLATE "default",
"state" varchar(36) COLLATE "default" NOT NULL,
"flag" int2 NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_app2dispatch
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_app2execute
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_app2execute";
CREATE TABLE "public"."chg_req_app2execute" (
"id" varchar(36) COLLATE "default" NOT NULL,
"changeid" varchar(36) COLLATE "default" NOT NULL,
"excbegtime" timestamp(6),
"excendtime" timestamp(6),
"excresult" char(1) COLLATE "default",
"result" varchar(100) COLLATE "default",
"excowner" varchar(36) COLLATE "default",
"bakattach" varchar(36) COLLATE "default",
"tester" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_app2execute
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_app2standard
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_app2standard";
CREATE TABLE "public"."chg_req_app2standard" (
"id" varchar(36) COLLATE "default" NOT NULL,
"chgid" varchar(36) COLLATE "default" NOT NULL,
"opnino" varchar(100) COLLATE "default",
"isneedcab" char(1) COLLATE "default",
"submittime" timestamp(6),
"state" varchar(36) COLLATE "default" NOT NULL,
"flag" int2 NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_app2standard
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_app2validate
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_app2validate";
CREATE TABLE "public"."chg_req_app2validate" (
"id" varchar(36) COLLATE "default" NOT NULL,
"chgid" varchar(36) COLLATE "default" NOT NULL,
"chstatus" varchar(36) COLLATE "default",
"isfuntion" char(1) COLLATE "default",
"issecurity" char(1) COLLATE "default",
"isperformance" char(1) COLLATE "default",
"isinitdate" char(1) COLLATE "default",
"opinion" varchar(36) COLLATE "default",
"estimate" varchar(36) COLLATE "default",
"cause" varchar(100) COLLATE "default",
"ccto" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_app2validate
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_application
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_application";
CREATE TABLE "public"."chg_req_application" (
"id" varchar(36) COLLATE "default" NOT NULL,
"chgid" varchar(36) COLLATE "default" NOT NULL,
"applicant" varchar(36) COLLATE "default",
"apptime" timestamp(6),
"dept" varchar(36) COLLATE "default",
"chgtopic" varchar(500) COLLATE "default",
"chgcause" varchar(4000) COLLATE "default",
"chgtype" varchar(36) COLLATE "default",
"chgcompany" varchar(500) COLLATE "default",
"chgaddress" varchar(100) COLLATE "default",
"chglevel" varchar(36) COLLATE "default",
"rfcsource" varchar(36) COLLATE "default" NOT NULL,
"rfccode" varchar(500) COLLATE "default",
"reapproves" varchar(100) COLLATE "default",
"redotime" timestamp(6),
"rereturntime" timestamp(6),
"redousers" varchar(100) COLLATE "default",
"bakupsteps" varchar(100) COLLATE "default",
"bakattach" varchar(100) COLLATE "default",
"dosteps" varchar(100) COLLATE "default",
"doattache" varchar(100) COLLATE "default",
"returnsteps" varchar(100) COLLATE "default",
"rstepattache" varchar(100) COLLATE "default",
"retestusers" varchar(100) COLLATE "default",
"befortreport" varchar(100) COLLATE "default",
"btrattache" varchar(100) COLLATE "default",
"aftertreport" varchar(100) COLLATE "default",
"isstop" char(1) COLLATE "default",
"isrisk" varchar(100) COLLATE "default",
"effect" varchar(100) COLLATE "default",
"strategy" varchar(100) COLLATE "default",
"isbakstrategy" char(1) COLLATE "default",
"bstrategycause" varchar(100) COLLATE "default",
"isstopmon" char(1) COLLATE "default",
"smoncause" varchar(100) COLLATE "default",
"status" varchar(36) COLLATE "default" NOT NULL,
"israccount" char(1) COLLATE "default",
"raccountcause" varchar(100) COLLATE "default",
"isconfig" char(1) COLLATE "default",
"configcause" varchar(100) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"state" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_application
-- ----------------------------

-- ----------------------------
-- Table structure for chg_req_attach2detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."chg_req_attach2detail";
CREATE TABLE "public"."chg_req_attach2detail" (
"id" varchar(36) COLLATE "default" NOT NULL,
"attachdesc" varchar(100) COLLATE "default",
"contenttype" varchar(50) COLLATE "default",
"attpath" varchar(100) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of chg_req_attach2detail
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_asset2drawer
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_asset2drawer";
CREATE TABLE "public"."cmdb_asm_asset2drawer" (
"id" varchar(50) COLLATE "default" NOT NULL,
"drawsid" varchar(36) COLLATE "default",
"unumb" int2,
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_class2items
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_class2items";
CREATE TABLE "public"."cmdb_asm_class2items" (
"id" varchar(36) COLLATE "default" NOT NULL,
"itemid" varchar(36) COLLATE "default" NOT NULL,
"itemname" varchar(500) COLLATE "default" NOT NULL,
"restype" varchar(36) COLLATE "default",
"unum" int2 NOT NULL,
"flag" char(1) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_extavalue
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_extavalue";
CREATE TABLE "public"."cmdb_asm_extavalue" (
"id" varchar(36) COLLATE "default" NOT NULL,
"extid" varchar(36) COLLATE "default",
"extvalue" varchar(1000) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_extavalue
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_extcolumns
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_extcolumns";
CREATE TABLE "public"."cmdb_asm_extcolumns" (
"id" varchar(36) COLLATE "default" NOT NULL,
"asstype" varchar(36) COLLATE "default",
"xcname" varchar(36) COLLATE "default",
"xctype" varchar(36) COLLATE "default",
"xclength" int4,
"defaultvalue" varchar(36) COLLATE "default",
"seq" int2,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_linkto
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_linkto";
CREATE TABLE "public"."cmdb_asm_linkto" (
"id" varchar(36) COLLATE "default" NOT NULL,
"trunkto" varchar(36) COLLATE "default" NOT NULL,
"accessto" varchar(36) COLLATE "default" NOT NULL,
"remark" varchar(600) COLLATE "default",
"vlan" varchar(50) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_maintenancs
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_maintenancs";
CREATE TABLE "public"."cmdb_asm_maintenancs" (
"id" varchar(36) COLLATE "default" NOT NULL,
"assid" varchar(36) COLLATE "default" NOT NULL,
"contract" varchar(50) COLLATE "default",
"depth" varchar(50) COLLATE "default",
"expense" float4,
"begtime" timestamp(6),
"endtime" timestamp(6),
"msupid" varchar(500) COLLATE "default",
"contact" varchar(20) COLLATE "default",
"owner" varchar(50) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"attach" varchar(50) COLLATE "default",
"warntime" int2,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_maintenancs
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master";
CREATE TABLE "public"."cmdb_asm_master" (
"id" varchar(36) COLLATE "default" NOT NULL,
"assetid" varchar(36) COLLATE "default" NOT NULL,
"serial" varchar(50) COLLATE "default" NOT NULL,
"assetname" varchar(100) COLLATE "default" NOT NULL,
"assettype" varchar(36) COLLATE "default" NOT NULL,
"assmode" varchar(36) COLLATE "default",
"depart" varchar(50) COLLATE "default" NOT NULL,
"assetuser" varchar(36) COLLATE "default" NOT NULL,
"assuser" varchar(36) COLLATE "default",
"sysuser" varchar(36) COLLATE "default",
"userid" varchar(36) COLLATE "default",
"useflag" varchar(50) COLLATE "default",
"os" varchar(50) COLLATE "default",
"provide" varchar(50) COLLATE "default" NOT NULL,
"parentid" varchar(50) COLLATE "default",
"status" varchar(50) COLLATE "default" NOT NULL,
"mmac" varchar(50) COLLATE "default",
"iloip" varchar(50) COLLATE "default",
"begdate" timestamp(6) NOT NULL,
"enddate" timestamp(6),
"lifeyears" float4,
"retirementdate" timestamp(6),
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_master2boards
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2boards";
CREATE TABLE "public"."cmdb_asm_master2boards" (
"id" varchar(36) COLLATE "default" NOT NULL,
"belongto" varchar(50) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_master2boards
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_master2log
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2log";
CREATE TABLE "public"."cmdb_asm_master2log" (
"id" varchar(36) COLLATE "default" NOT NULL,
"assetid" varchar(36) COLLATE "default",
"chgcause" varchar(100) COLLATE "default" NOT NULL,
"fpropvalue" varchar(100) COLLATE "default" NOT NULL,
"apropvalue" varchar(100) COLLATE "default" NOT NULL,
"chowner" varchar(50) COLLATE "default" NOT NULL,
"chdate" timestamp(6),
"chgid" varchar(50) COLLATE "default",
"flag" char(1) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_master2log
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_master2other
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2other";
CREATE TABLE "public"."cmdb_asm_master2other" (
"id" varchar(36) COLLATE "default" NOT NULL,
"otherdesc" varchar(1000) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_master2other
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_master2router
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2router";
CREATE TABLE "public"."cmdb_asm_master2router" (
"id" varchar(36) COLLATE "default" NOT NULL,
"cpu" varchar(50) COLLATE "default",
"ram" varchar(50) COLLATE "default",
"ipv4rrate" varchar(50) COLLATE "default",
"ipv6rrate" varchar(50) COLLATE "default",
"mpuslots" varchar(50) COLLATE "default",
"baseslots" varchar(50) COLLATE "default",
"swcapacity" varchar(50) COLLATE "default",
"pacrate" varchar(50) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_master2server
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2server";
CREATE TABLE "public"."cmdb_asm_master2server" (
"id" varchar(36) COLLATE "default" NOT NULL,
"processors" int2,
"cpuform" varchar(100) COLLATE "default",
"memtotal" float4,
"memorys" varchar(100) COLLATE "default",
"disktotal" int2,
"disks" varchar(100) COLLATE "default",
"havecdroom" char(1) COLLATE "default",
"linetype" varchar(50) COLLATE "default",
"powers" int2,
"pinboard" int2,
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_master2server
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_master2stock
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2stock";
CREATE TABLE "public"."cmdb_asm_master2stock" (
"id" varchar(36) COLLATE "default" NOT NULL,
"disktotal" float4,
"disks" varchar(50) COLLATE "default",
"disktype" varchar(50) COLLATE "default",
"pinboard" int2,
"switchport" int2,
"linetype" varchar(50) COLLATE "default",
"powers" int2,
"controls" int2,
"contremark" varchar(100) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_master2stock
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_master2switch
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_master2switch";
CREATE TABLE "public"."cmdb_asm_master2switch" (
"id" varchar(36) COLLATE "default" NOT NULL,
"cpu" varchar(50) COLLATE "default",
"ram" varchar(50) COLLATE "default",
"ipv4rrate" varchar(50) COLLATE "default",
"ipv6rrate" varchar(50) COLLATE "default",
"baseslots" varchar(50) COLLATE "default",
"netslots" varchar(50) COLLATE "default",
"swcapacity" varchar(50) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"pacrate" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_asm_master2switch
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_asm_netports
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_netports";
CREATE TABLE "public"."cmdb_asm_netports" (
"id" varchar(36) COLLATE "default" NOT NULL,
"masterid" varchar(50) COLLATE "default",
"seq" int2,
"mac" varchar(50) COLLATE "default",
"porttype" varchar(50) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"ethtype" char(1) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_asm_sware
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_asm_sware";
CREATE TABLE "public"."cmdb_asm_sware" (
"id" varchar(36) COLLATE "default" NOT NULL,
"scode" varchar(36) COLLATE "default" NOT NULL,
"shortname" varchar(50) COLLATE "default",
"sname" varchar(200) COLLATE "default",
"softwareversion" varchar(50) COLLATE "default",
"status" varchar(50) COLLATE "default",
"sowner" varchar(50) COLLATE "default",
"position" varchar(50) COLLATE "default",
"totalauth" int2 NOT NULL,
"totaluser" int2,
"releasedate" timestamp(6),
"stype" varchar(50) COLLATE "default",
"supid" varchar(500) COLLATE "default",
"patch" varchar(500) COLLATE "default",
"remark" varchar(200) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_cap_freeserver
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cap_freeserver";
CREATE TABLE "public"."cmdb_cap_freeserver" (
"id" varchar(36) COLLATE "default" NOT NULL,
"hostid" varchar(36) COLLATE "default" NOT NULL,
"hostname" varchar(100) COLLATE "default",
"ip" varchar(100) COLLATE "default",
"year" varchar(4) COLLATE "default" NOT NULL,
"month" varchar(2) COLLATE "default" NOT NULL,
"day" varchar(2) COLLATE "default" NOT NULL,
"tenant" varchar(36) COLLATE "default",
"syncdate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_cap_freeserver
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_cap_netflow
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cap_netflow";
CREATE TABLE "public"."cmdb_cap_netflow" (
"id" varchar(36) COLLATE "default" NOT NULL,
"assetname" varchar(100) COLLATE "default" NOT NULL,
"port" varchar(100) COLLATE "default",
"ip" varchar(100) COLLATE "default",
"year" varchar(4) COLLATE "default" NOT NULL,
"month" varchar(2) COLLATE "default" NOT NULL,
"day" varchar(2) COLLATE "default" NOT NULL,
"nquota" int2,
"nuser" float4 NOT NULL,
"tenant" varchar(36) COLLATE "default",
"syncdate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_cap_netflow
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_cap_server
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cap_server";
CREATE TABLE "public"."cmdb_cap_server" (
"id" varchar(36) COLLATE "default" NOT NULL,
"hostid" varchar(36) COLLATE "default" NOT NULL,
"hostname" varchar(100) COLLATE "default",
"year" varchar(4) COLLATE "default" NOT NULL,
"month" varchar(2) COLLATE "default" NOT NULL,
"day" varchar(2) COLLATE "default" NOT NULL,
"cpus" int2,
"cpuoversize" float4 NOT NULL,
"ram" int2,
"ramoversize" float4,
"vms" int4,
"tenant" varchar(36) COLLATE "default",
"syncdate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_cap_server
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_cap_storages
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cap_storages";
CREATE TABLE "public"."cmdb_cap_storages" (
"id" varchar(36) COLLATE "default" NOT NULL,
"storageid" varchar(36) COLLATE "default" NOT NULL,
"storagename" varchar(100) COLLATE "default",
"year" varchar(4) COLLATE "default" NOT NULL,
"month" varchar(2) COLLATE "default" NOT NULL,
"day" varchar(2) COLLATE "default" NOT NULL,
"volumeid" varchar(100) COLLATE "default",
"vquota" int8,
"vuse" int8,
"tenant" varchar(36) COLLATE "default",
"syncdate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_cap_storages
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_cap_tablespace
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cap_tablespace";
CREATE TABLE "public"."cmdb_cap_tablespace" (
"id" varchar(36) COLLATE "default" NOT NULL,
"tablespacename" varchar(100) COLLATE "default" NOT NULL,
"database" varchar(100) COLLATE "default",
"year" varchar(4) COLLATE "default" NOT NULL,
"month" varchar(2) COLLATE "default" NOT NULL,
"day" varchar(2) COLLATE "default" NOT NULL,
"tquota" int2,
"tuser" float4 NOT NULL,
"tenant" varchar(36) COLLATE "default",
"syncdate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_cap_tablespace
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_cloud_server2vm
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cloud_server2vm";
CREATE TABLE "public"."cmdb_cloud_server2vm" (
"id" varchar(36) COLLATE "default" NOT NULL,
"hostid" varchar(50) COLLATE "default",
"serilno" varchar(50) COLLATE "default",
"vmname" varchar(50) COLLATE "default",
"uuid" varchar(50) COLLATE "default" NOT NULL,
"status" int2,
"os" varchar(50) COLLATE "default",
"cpu" int2,
"memory" int2,
"storage" int4,
"sysuser" varchar(36) COLLATE "default",
"userid" varchar(36) COLLATE "default",
"note" varchar(100) COLLATE "default",
"synctype" varchar(50) COLLATE "default",
"lastsyncdate" timestamp(6),
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."cmdb_cloud_server2vm"."synctype" IS '页面首次添加为USER_SAVE，若同步后名称相同，则修改为对应同步类型';

-- ----------------------------
-- Records of cmdb_cloud_server2vm
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_cloud_vnetports
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_cloud_vnetports";
CREATE TABLE "public"."cmdb_cloud_vnetports" (
"id" varchar(36) COLLATE "default" NOT NULL,
"vmid" varchar(50) COLLATE "default",
"seq" int2,
"mac" varchar(50) COLLATE "default",
"porttype" varchar(50) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"peth" varchar(36) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_cloud_vnetports
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_dc_draws
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_dc_draws";
CREATE TABLE "public"."cmdb_dc_draws" (
"id" varchar(36) COLLATE "default" NOT NULL,
"roomid" varchar(50) COLLATE "default",
"rownum" int2 NOT NULL,
"colnum" int2 NOT NULL,
"maxu" int2 NOT NULL,
"remark" varchar(100) COLLATE "default",
"isuse" char(1) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_dc_iprelation
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_dc_iprelation";
CREATE TABLE "public"."cmdb_dc_iprelation" (
"id" varchar(36) COLLATE "default" NOT NULL,
"ip" varchar(36) COLLATE "default" NOT NULL,
"assetid" varchar(50) COLLATE "default",
"classid" varchar(36) COLLATE "default",
"ncid" varchar(36) COLLATE "default",
"isilop" int2,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_dc_iprelationh
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_dc_iprelationh";
CREATE TABLE "public"."cmdb_dc_iprelationh" (
"id" varchar(50) COLLATE "default" NOT NULL,
"ip" varchar(36) COLLATE "default" NOT NULL,
"assetid" varchar(50) COLLATE "default",
"classid" varchar(36) COLLATE "default",
"ncid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"operation" varchar(32) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_dc_rooms
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_dc_rooms";
CREATE TABLE "public"."cmdb_dc_rooms" (
"id" varchar(36) COLLATE "default" NOT NULL,
"roomname" varchar(100) COLLATE "default" NOT NULL,
"shortname" varchar(100) COLLATE "default",
"maxrows" int2 NOT NULL,
"maxcols" int2 NOT NULL,
"defaultu" int2 DEFAULT 0 NOT NULL,
"capacity" int2,
"telephone" varchar(20) COLLATE "default",
"roomonwer" varchar(50) COLLATE "default",
"localadmin" varchar(50) COLLATE "default",
"localadmintel" varchar(50) COLLATE "default",
"admin" varchar(50) COLLATE "default",
"contact" varchar(100) COLLATE "default",
"region" varchar(50) COLLATE "default" NOT NULL,
"address" varchar(100) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6),
"admintel" varchar(20) COLLATE "default",
"supplier" varchar(100) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_router_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_router_groups";
CREATE TABLE "public"."cmdb_router_groups" (
"id" varchar(36) COLLATE "default" NOT NULL,
"stackname" varchar(100) COLLATE "default",
"stacktype" varchar(50) COLLATE "default",
"pstackid" varchar(50) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default",
"createdby" varchar(36) COLLATE "default",
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default",
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_router_groups
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_router_groups2items
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_router_groups2items";
CREATE TABLE "public"."cmdb_router_groups2items" (
"id" varchar(50) COLLATE "default" NOT NULL,
"stackid" varchar(50) COLLATE "default",
"masterid" varchar(50) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default",
"createdby" varchar(36) COLLATE "default",
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default",
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_router_groups2items
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_server_cluster2items
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_server_cluster2items";
CREATE TABLE "public"."cmdb_server_cluster2items" (
"id" varchar(36) COLLATE "default" NOT NULL,
"assid" varchar(36) COLLATE "default",
"custertid" varchar(50) COLLATE "default",
"cvk_version" varchar(25) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default",
"createdby" varchar(36) COLLATE "default",
"createddate" timestamp(6),
"updatedby" varchar(36) COLLATE "default",
"updateddate" timestamp(6),
"phostid" varchar(36) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_server_cluster2items
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_server_clusters
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_server_clusters";
CREATE TABLE "public"."cmdb_server_clusters" (
"id" varchar(36) COLLATE "default" NOT NULL,
"cname" varchar(50) COLLATE "default",
"phostid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default",
"createdby" varchar(36) COLLATE "default",
"createddate" timestamp(6),
"updatedby" varchar(36) COLLATE "default",
"updateddate" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_server_clusters
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_server_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_server_groups";
CREATE TABLE "public"."cmdb_server_groups" (
"id" varchar(36) COLLATE "default" NOT NULL,
"cname" varchar(50) COLLATE "default",
"note" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_server_groups
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_server_pools2host
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_server_pools2host";
CREATE TABLE "public"."cmdb_server_pools2host" (
"id" varchar(36) COLLATE "default" NOT NULL,
"poolname" varchar(100) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default",
"createdby" varchar(36) COLLATE "default",
"createddate" timestamp(6),
"updatedby" varchar(36) COLLATE "default",
"updateddate" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_server_pools2host
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_storage_clusters
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_storage_clusters";
CREATE TABLE "public"."cmdb_storage_clusters" (
"id" varchar(36) COLLATE "default" NOT NULL,
"name" varchar(50) COLLATE "default",
"gid" varchar(50) COLLATE "default",
"ip" varchar(40) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_storage_clusters
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_storage_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_storage_groups";
CREATE TABLE "public"."cmdb_storage_groups" (
"id" varchar(36) COLLATE "default" NOT NULL,
"name" varchar(50) COLLATE "default",
"manager" varchar(50) COLLATE "default" NOT NULL,
"status" int2,
"remark" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_storage_groups
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_storage_groups2items
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_storage_groups2items";
CREATE TABLE "public"."cmdb_storage_groups2items" (
"id" varchar(36) COLLATE "default" NOT NULL,
"masterid" varchar(50) COLLATE "default",
"cid" varchar(36) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_storage_groups2items
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_storage_manage
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_storage_manage";
CREATE TABLE "public"."cmdb_storage_manage" (
"id" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(50) COLLATE "default",
"name" varchar(50) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"groupid2" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_storage_manage
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_storage_volume2host
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_storage_volume2host";
CREATE TABLE "public"."cmdb_storage_volume2host" (
"id" varchar(36) COLLATE "default" NOT NULL,
"volumeid" varchar(50) COLLATE "default",
"type" varchar(50) COLLATE "default",
"hostid" varchar(50) COLLATE "default",
"targetip" varchar(50) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_storage_volume2host
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_storage_volums
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_storage_volums";
CREATE TABLE "public"."cmdb_storage_volums" (
"id" varchar(36) COLLATE "default" NOT NULL,
"sid" varchar(50) COLLATE "default",
"storytype" varchar(36) COLLATE "default",
"volumename" int2,
"size" float4,
"raidmethod" int2,
"remark" varchar(100) COLLATE "default",
"ip" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_storage_volums
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_switch_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_switch_groups";
CREATE TABLE "public"."cmdb_switch_groups" (
"id" varchar(36) COLLATE "default" NOT NULL,
"stackname" varchar(100) COLLATE "default" NOT NULL,
"stacktype" varchar(50) COLLATE "default",
"pstackid" varchar(50) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_switch_groups2items
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_switch_groups2items";
CREATE TABLE "public"."cmdb_switch_groups2items" (
"id" varchar(50) COLLATE "default" NOT NULL,
"stackid" varchar(50) COLLATE "default",
"masterid" varchar(50) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for cmdb_www_iprelation
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_www_iprelation";
CREATE TABLE "public"."cmdb_www_iprelation" (
"id" varchar(36) COLLATE "default" NOT NULL,
"ip" varchar(36) COLLATE "default" NOT NULL,
"assetid" varchar(50) COLLATE "default" NOT NULL,
"classid" varchar(36) COLLATE "default",
"ncid" varchar(36) COLLATE "default" NOT NULL,
"cisid" int2,
"btime" timestamp(6),
"etime" timestamp(6),
"status" char(1) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6),
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_www_iprelation
-- ----------------------------

-- ----------------------------
-- Table structure for cmdb_www_iprelationh
-- ----------------------------
DROP TABLE IF EXISTS "public"."cmdb_www_iprelationh";
CREATE TABLE "public"."cmdb_www_iprelationh" (
"id" varchar(50) COLLATE "default" NOT NULL,
"ip" varchar(36) COLLATE "default" NOT NULL,
"assetid" varchar(50) COLLATE "default" NOT NULL,
"classid" varchar(36) COLLATE "default",
"ncid" varchar(36) COLLATE "default" NOT NULL,
"cisid" int2,
"btime" timestamp(6),
"etime" timestamp(6),
"rtime" timestamp(6),
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of cmdb_www_iprelationh
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_account_accounts
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_account_accounts";
CREATE TABLE "public"."iyun_account_accounts" (
"id" varchar(36) COLLATE "default" NOT NULL,
"aname" varchar(50) COLLATE "default",
"apwd" varchar(50) COLLATE "default",
"ismanaged" varchar(5) COLLATE "default",
"responsibleperson" varchar(50) COLLATE "default",
"accountuse" varchar(500) COLLATE "default",
"hostid" varchar(36) COLLATE "default",
"belongto" varchar(50) COLLATE "default",
"accounttype" varchar(50) COLLATE "default",
"status" varchar(5) COLLATE "default",
"recoverymark" varchar(50) COLLATE "default",
"recoverydate" timestamp(6),
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_account_accounts
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_account_app2detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_account_app2detail";
CREATE TABLE "public"."iyun_account_app2detail" (
"id" varchar(36) COLLATE "default" NOT NULL,
"appid" varchar(36) COLLATE "default",
"accountid" varchar(36) COLLATE "default" NOT NULL,
"username" varchar(36) COLLATE "default",
"userowner" varchar(36) COLLATE "default",
"begtime" timestamp(6),
"endtime" timestamp(6),
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_account_app2detail
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_account_application
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_account_application";
CREATE TABLE "public"."iyun_account_application" (
"id" varchar(36) COLLATE "default" NOT NULL,
"asker" varchar(36) COLLATE "default" NOT NULL,
"user" varchar(36) COLLATE "default" NOT NULL,
"changeid" varchar(36) COLLATE "default",
"appdesc" varchar(500) COLLATE "default",
"isspeci" char(1) COLLATE "default" DEFAULT '0'::bpchar NOT NULL,
"remark" varchar(200) COLLATE "default",
"insid" varchar(36) COLLATE "default",
"status" char(1) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_account_application
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_account_auth2user
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_account_auth2user";
CREATE TABLE "public"."iyun_account_auth2user" (
"id" varchar(36) COLLATE "default" NOT NULL,
"account" varchar(36) COLLATE "default",
"userid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_account_auth2user
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_account_pwdrecord
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_account_pwdrecord";
CREATE TABLE "public"."iyun_account_pwdrecord" (
"id" varchar(50) COLLATE "default" NOT NULL,
"accountid" varchar(50) COLLATE "default",
"pwdrecord" varchar(50) COLLATE "default",
"updateddate" timestamp(6),
"updatedby" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_account_pwdrecord
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_base_emailquenu
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_emailquenu";
CREATE TABLE "public"."iyun_base_emailquenu" (
"id" varchar(36) COLLATE "default" NOT NULL,
"mname" varchar(50) COLLATE "default" NOT NULL,
"mfrom" varchar(100) COLLATE "default" NOT NULL,
"mto" varchar(100) COLLATE "default" NOT NULL,
"mcc" varchar(100) COLLATE "default",
"mbcc" varchar(100) COLLATE "default",
"mtopic" varchar(100) COLLATE "default" NOT NULL,
"mcontent" varchar(1000) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_emailquenu
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_base_flavor
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_flavor";
CREATE TABLE "public"."iyun_base_flavor" (
"id" varchar(32) COLLATE "default" NOT NULL,
"name" varchar(100) COLLATE "default" NOT NULL,
"vcpus" int8 NOT NULL,
"ram" int8 NOT NULL,
"disk" int8 NOT NULL,
"disktype" varchar(100) COLLATE "default" NOT NULL,
"swap" int8 NOT NULL,
"is_public" char(1) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_flavor
-- ----------------------------
INSERT INTO "public"."iyun_base_flavor" VALUES ('01f649845bdd4075b592a4872b554f3d', '4*12*80', '4', '12288', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('03c411606e364ffcb5ce6a533cea1393', '2*8*90', '2', '8192', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('064a67920e3147fd9192d547d1a6b6af', '4*16*90', '4', '16384', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('09f30226e5ae4e77b5b5e0001f9a6471', '1*4*70', '1', '4096', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('0f8239a9167c42a99ba9acf435d56fb3', '2*4*50', '2', '4096', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('116ebe2db2d2436a961c01f9dff68415', '4*8*90', '4', '8192', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('140c6bb76a6849f39cd69dff0aca6d6b', '4*16*50', '4', '16384', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('14196eb21fba4296b3c403e688a4c61d', '4*12*40', '4', '12288', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('15164ac750e04bde8ca50cdf4eeb5a8a', '8*24*60', '8', '24576', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('159edb7f7a5b4979abd9c7123f4f8f96', '4*16*80', '4', '16384', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('19218e88b081466eb34e21da5326dbf3', '2*8*40', '2', '8192', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('1d5fcd6f1034492e8525b1df09904134', '2*4*90', '2', '4096', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('1f6523b8b3af4528947177fdae7ed6fd', '2*8*60', '2', '8192', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('249e525897c8442baf39e240dd78b198', '1*4*60', '1', '4096', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('26bf7ac5c87a421f97f1076e1d3974d3', '1*2*80', '1', '2048', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('283fb181bf414da8b285446fc9fd80df', '8*24*70', '8', '24576', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('289fce3ab3644aaa82b357c5033455f8', '1*4*50', '1', '4096', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('2b697d18634943f385b95c3ccfb7ae4a', '8*24*50', '8', '24576', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('33f803fb1743453d9293f274f3c926ed', '2*4*70', '2', '4096', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('34a56d8501b64c5a84314fc009b4dac5', '1*2*70', '1', '2048', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('35b7b9e4953b4410a9de2a7d4b05ad0b', '4*16*60', '4', '16384', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('3694a5f17001466dacfb267532a36f9c', '8*32*80', '8', '32768', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('36da74c811b646899e76a565c34925f5', '8*16*40', '8', '16384', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('38907f88360a43f884221b36a5f7fa33', '8*32*100', '8', '32768', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('3ec2125fd144443f94a28a5ddd355a30', '1*2*90', '1', '2048', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('4613da6a89d847b289c5b11cb997dd27', '2*4*40', '2', '4096', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('47e68f8cb0f849269b49d2bb73f0826e', '2*4*80', '2', '4096', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('53fa68b9428d4d438f862107f09e8b29', '1*4*100', '1', '4096', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('569d9fdc9306425c8777a11b20e4df73', '4*12*100', '4', '12288', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('596a072d3715470a8e61efd9fa79ecd2', '8*32*90', '8', '32768', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('686411da5d0246b285a2d8908f164b85', '8*16*80', '8', '16384', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('695a782d61da4be7b701b2b12012081a', '8*32*40', '8', '32768', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('6fcdb91db07941fba56285f4b137984c', '4*12*50', '4', '12288', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('70d210908530483a94f70f95d3eb7a03', '8*16*50', '8', '16384', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('7209020c73984a28a3dbab9c771dbff2', '4*12*90', '4', '12288', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('77b011b4a0954eb9975e0f57e19a1113', '4*16*40', '4', '16384', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('7a29b0e2b9cc408eab179fb2a9a27cba', '1*2*50', '1', '2048', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('80203b5e6e774a8196c2c272c8f3ac5b', '8*32*50', '8', '32768', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('81a443f4c22b401bb06ec787820a6e17', '8*32*60', '8', '32768', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('85ba31f5fb004a6dbc971a12a5738585', '1*4*90', '1', '4096', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('8872ae2b691545d6bcef554d1acc9a35', '2*8*70', '2', '8192', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('890a1d8e2e7b4867980ef78d95367a44', '1*2*100', '1', '2048', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('93f04f3c890940e7997aa10575b12168', '1*8*40', '1', '8192', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('96596b70f885403e8a975dc114e81287', '4*8*50', '4', '8192', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('997499810139499785f432c31e46de3a', '8*24*90', '8', '24576', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('9bdc7616e8b1415dbf7ed9ccb5947ba3', '4*16*70', '4', '16384', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('9c09462f88af4c1dbde44d60c0ee5b21', '4*8*80', '4', '8192', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('a39ce05633ac4039ad036def8c5573b0', '8*32*70', '8', '32768', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('a78b2992e7e84999821dcf15d7720d91', '4*8*40', '4', '8192', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('a8e78e45d63644df899f66443aa3d3db', '8*16*90', '8', '16384', '90', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('a95ed81432354738b0fcce06663d9ec9', '8*24*100', '8', '24576', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('af8633768a8b454aa9867a29fa7c592f', '8*16*60', '8', '16384', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('afecc264e9b845c48552aff965e8b554', '4*8*60', '4', '8192', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('b0485d4a9f454ab8a41e5f0fc5a88c72', '4*8*100', '4', '8192', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('b16fe67c30674fe597cadca1d1057c59', '8*24*80', '8', '24576', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('b6eecfb69eb14a10a36f458ad9f404af', '1*4*80', '1', '4096', '80', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('c2d5d7f3cb514b3dbe4c6aafcd04691b', '1*2*40', '1', '2048', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('c52a117a63204ac5ac88e989799306e4', '2*4*60', '2', '4096', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('c8c8af10df744eb09790f246493c5626', '4*16*100', '4', '16384', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('c973698a00a447e69977887237991548', '2*8*100', '2', '8192', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('ca8cb70c7f6e461682dc2191f0120fb7', '4*12*60', '4', '12288', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('d756bd191cd7438896bd499b20f43508', '4*12*70', '4', '12288', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('db64e1a0490e4f2e8cd13fd8009dd6b1', '2*8*50', '2', '8192', '50', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('dcd1d3e620594cae93910e196dcf2807', '1*3*40', '1', '3072', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('e70d6192fb9c4b6d8790eb6062d02470', '8*16*70', '8', '16384', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('e8da13f4936c43b6bac4667b1fc79726', '8*16*100', '8', '16384', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('eae604e401024259a660ba9cac4ee74f', '1*4*40', '1', '4096', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('f07a11e2ccb94d79a1d319265491c8cb', '1*2*60', '1', '2048', '60', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('f2b3cb7bf4d6432196956b8d9ab21475', '2*4*100', '2', '4096', '100', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('faf1a3a63e2f4a5db375664a998328cf', '8*24*40', '8', '24576', '40', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('fc1394eba30e4296a39633b1ce582735', '4*8*70', '4', '8192', '70', '0', '0', '0');
INSERT INTO "public"."iyun_base_flavor" VALUES ('fdf53afe97eb497eadaed10e44274c63', '2*8*80', '2', '8192', '80', '0', '0', '0');

-- ----------------------------
-- Table structure for iyun_base_initcode
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_initcode";
CREATE TABLE "public"."iyun_base_initcode" (
"id" varchar(36) COLLATE "default" NOT NULL,
"codetypeid" varchar(36) COLLATE "default" NOT NULL,
"typedesc" varchar(100) COLLATE "default" NOT NULL,
"codeid" varchar(36) COLLATE "default" NOT NULL,
"codename" varchar(100) COLLATE "default",
"codeseq" int2,
"status" char(1) COLLATE "default" NOT NULL,
"effectivedate" timestamp(6),
"expirydate" timestamp(6),
"syscode" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_initcode
-- ----------------------------
INSERT INTO "public"."iyun_base_initcode" VALUES ('8a8a700d57a30ea80157a30eccdf0000', 'cmdb.assert.type', '资产类型', 'server', '服务器', '1', '1', '2016-10-08 14:50:38.618', '2016-10-08 14:50:38.618', null, '', 'junitTest', '2016-10-08 14:50:38.684', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-31 17:30:24.408');
INSERT INTO "public"."iyun_base_initcode" VALUES ('8a8a700d57a30ea80157a30eccf60001', 'cmdb.assert.type', '资产类型', 'router', '路由器', '2', '1', '2016-10-08 14:50:38.708', '2016-10-08 14:50:38.708', null, '', 'junitTest', '2016-10-08 14:50:38.71', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-31 17:45:46.928');
INSERT INTO "public"."iyun_base_initcode" VALUES ('8a8a700d57a30ea80157a30eccf70002', 'cmdb.assert.type', '资产类型', 'switch', '交换机', '3', '1', '2016-10-08 14:50:38.711', '2016-10-08 14:50:38.711', null, '', 'junitTest', '2016-10-08 14:50:38.711', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-28 17:08:20.025');
INSERT INTO "public"."iyun_base_initcode" VALUES ('8a8a700d57a30ea80157a30eccf80003', 'cmdb.assert.type', '资产类型', 'stock', '存储', '4', '0', '2016-10-08 14:50:38.711', '2016-10-08 14:50:38.711', null, '', 'junitTest', '2016-10-08 14:50:38.712', 'junitTest', '2016-10-08 14:50:38.712');
INSERT INTO "public"."iyun_base_initcode" VALUES ('8a8a700d57a30ea80157a30eccf90004', 'cmdb.assert.type', '资产类型', 'boards', '板卡', '5', '0', '2016-10-08 14:50:38.713', '2016-10-08 14:50:38.713', null, '', 'junitTest', '2016-10-08 14:50:38.713', 'junitTest', '2016-10-08 14:50:38.713');
INSERT INTO "public"."iyun_base_initcode" VALUES ('8a8a700d57a30ea80157a30eccfa0005', 'cmdb.assert.type', '资产类型(有扩展字段)', 'other', '其他', '6', '0', '2016-10-08 14:50:38.713', '2016-10-08 14:50:38.713', null, '', 'junitTest', '2016-10-08 14:50:38.714', 'junitTest', '2016-10-08 14:50:38.714');

-- ----------------------------
-- Table structure for iyun_base_mattach
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_mattach";
CREATE TABLE "public"."iyun_base_mattach" (
"id" varchar(36) COLLATE "default" NOT NULL,
"mid" varchar(50) COLLATE "default",
"aname" varchar(100) COLLATE "default",
"nencoding" varchar(50) COLLATE "default",
"atype" varchar(50) COLLATE "default",
"fpath" varchar(100) COLLATE "default",
"tencoding" varchar(50) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_mattach
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_base_prd2templates
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_prd2templates";
CREATE TABLE "public"."iyun_base_prd2templates" (
"id" varchar(36) COLLATE "default" NOT NULL,
"classid" varchar(36) COLLATE "default",
"key" varchar(36) COLLATE "default",
"keyname" varchar(100) COLLATE "default",
"orderby" int2,
"validate" varchar(200) COLLATE "default",
"datatype" varchar(1) COLLATE "default",
"isshow" varchar(1) COLLATE "default",
"ismust" varchar(1) COLLATE "default",
"units" varchar(16) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_prd2templates
-- ----------------------------
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('1', '1', 'vcpu', 'CPU', '1', null, '1', '0', '0', '核');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('10', '2', 'diskBrain', '容量', '3', null, '2', '0', '0', 'GB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('11', '2', 'bandWidth', '带宽', '4', null, '2', '0', '1', 'MB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('12', '2', 'internetWidth', '互联网出口', '5', null, '2', '0', '1', 'MB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('13', '1', 'os', '系统类型', '8', null, '3', '0', '0', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('14', '1', 'flavorId', '规格id', '9', null, '3', '1', '0', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('15', '1', 'publicIp', '公网IP', '10', 'IP', '6', '0', '1', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('16', '1', 'privateIp', '私网IP', '11', 'IP', '6', '0', '1', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('17', '1', 'needPublicIp', '绑定公网IP', '12', 'contain(1,0)', '1', '0', '1', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('18', '1', 'month', '购买市场', '12', null, '1', '0', '0', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('2', '1', 'ram', '内存', '2', null, '2', '0', '0', 'GB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('3', '1', 'systemDisk', '系统盘', '3', null, '1', '0', '0', 'GB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('4', '1', 'dataDisk', '数据盘', '4', null, '4', '0', '1', 'GB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('5', '1', 'bandWidth', '带宽', '5', null, '2', '0', '1', 'MB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('6', '1', 'loadBalance', '负载均衡', '6', null, '2', '0', '1', 'MB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('7', '1', 'internetWidth', '互联网出口', '7', null, '2', '0', '1', 'MB');
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('8', '2', 'diskName', '硬盘名称', '1', null, '3', '0', '0', null);
INSERT INTO "public"."iyun_base_prd2templates" VALUES ('9', '2', 'diskType', '硬盘类型', '2', 'contain(1,2)', '1', '0', '0', null);

-- ----------------------------
-- Table structure for iyun_base_prdclass
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_prdclass";
CREATE TABLE "public"."iyun_base_prdclass" (
"id" varchar(36) COLLATE "default" NOT NULL,
"classname" varchar(100) COLLATE "default" NOT NULL,
"flag" char(1) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_prdclass
-- ----------------------------
INSERT INTO "public"."iyun_base_prdclass" VALUES ('1', '云主机', '1', '1', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-18 16:42:14', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-18 16:42:18');
INSERT INTO "public"."iyun_base_prdclass" VALUES ('2', '云硬盘', '1', '1', '1', '2016-09-19 15:54:44', '1', '2016-09-19 15:54:48');

-- ----------------------------
-- Table structure for iyun_base_prdclass2detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_prdclass2detail";
CREATE TABLE "public"."iyun_base_prdclass2detail" (
"id" varchar(36) COLLATE "default" NOT NULL,
"classid" varchar(36) COLLATE "default",
"cdetailname" varchar(100) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_prdclass2detail
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_base_rules
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_rules";
CREATE TABLE "public"."iyun_base_rules" (
"id" varchar(36) COLLATE "default" NOT NULL,
"osmirid" varchar(36) COLLATE "default" NOT NULL,
"osmirname" varchar(100) COLLATE "default" NOT NULL,
"vcpu" int2 NOT NULL,
"mindisk" int8 NOT NULL,
"minram" int8 NOT NULL,
"minswap" int8,
"isdefault" char(1) COLLATE "default",
"synctime" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_rules
-- ----------------------------
INSERT INTO "public"."iyun_base_rules" VALUES ('1', '1', 'Windows', '1', '50', '1024', '1', '0', null);
INSERT INTO "public"."iyun_base_rules" VALUES ('2', '2', 'Linux', '1', '50', '1024', '1', '0', null);

-- ----------------------------
-- Table structure for iyun_base_sharedoc
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_sharedoc";
CREATE TABLE "public"."iyun_base_sharedoc" (
"id" varchar(36) COLLATE "default" NOT NULL,
"doccode" varchar(50) COLLATE "default" NOT NULL,
"fname" varchar(100) COLLATE "default" NOT NULL,
"fdesc" varchar(500) COLLATE "default",
"ftype" varchar(50) COLLATE "default" NOT NULL,
"fpath" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_sharedoc
-- ----------------------------

-- ----------------------------
-- Table structure for iyun_base_singleflavors
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_base_singleflavors";
CREATE TABLE "public"."iyun_base_singleflavors" (
"id" varchar(32) COLLATE "default" NOT NULL,
"flavortype" varchar(100) COLLATE "default" NOT NULL,
"value" varchar(100) COLLATE "default" NOT NULL,
"is_public" char(1) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_base_singleflavors
-- ----------------------------
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('1', 'cpu', '1', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('10', 'ram', '24', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('11', 'ram', '32', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('12', 'disk', '50', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('13', 'disk', '60', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('14', 'disk', '70', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('15', 'disk', '80', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('16', 'disk', '90', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('17', 'disk', '100', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('18', 'storageType', '1', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('19', 'storageType', '2', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('2', 'cpu', '2', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('3', 'cpu', '4', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('4', 'cpu', '8', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('5', 'ram', '2', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('6', 'ram', '4', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('7', 'ram', '8', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('8', 'ram', '12', '0');
INSERT INTO "public"."iyun_base_singleflavors" VALUES ('9', 'ram', '16', '0');

-- ----------------------------
-- Table structure for iyun_flow_workflow
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_flow_workflow";
CREATE TABLE "public"."iyun_flow_workflow" (
"id" varchar(36) COLLATE "default" NOT NULL,
"name" varchar(50) COLLATE "default",
"key" varchar(50) COLLATE "default",
"filename" varchar(200) COLLATE "default",
"version" int2 NOT NULL,
"status" varchar(50) COLLATE "default",
"remark" varchar(600) COLLATE "default",
"deployer" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"deploymentid" varchar(50) COLLATE "default",
"uploaddate" timestamp(6),
"deploydate" timestamp(6),
"startdate" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_flow_workflow
-- ----------------------------
INSERT INTO "public"."iyun_flow_workflow" VALUES ('1', '业务办理流程', 'business', 'business.bpmn', '1', '1', '1', '2016-09-08 10:50:47', '1', '2016-09-08 10:50:51', '2016-09-08 10:49:40', '2016-09-08 11:22:42.872', '2016-09-08 10:49:47', '1', '2016-09-18 17:29:39', '2016-09-22 17:44:10.091', '2016-09-18 17:29:47');

-- ----------------------------
-- Table structure for iyun_flow_workrole
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_flow_workrole";
CREATE TABLE "public"."iyun_flow_workrole" (
"id" varchar(36) COLLATE "default" NOT NULL,
"workflowid" varchar(36) COLLATE "default",
"rolekey" varchar(50) COLLATE "default",
"processsegment" varchar(50) COLLATE "default",
"processname" varchar(50) COLLATE "default",
"level" varchar(50) COLLATE "default",
"roleid" varchar(50) COLLATE "default",
"issamedept" varchar(50) COLLATE "default",
"remark" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_flow_workrole
-- ----------------------------
INSERT INTO "public"."iyun_flow_workrole" VALUES ('8a924084575145da01575147d0740000', '1', 'ba_step2_department', 'usertask2', '区域经理审批', '0', '8a924084574fd67f01575086b7af0006', null, '部署新流程时,系统分析部署文件自动导入。', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.441', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.441');
INSERT INTO "public"."iyun_flow_workrole" VALUES ('8a924084575145da01575147d09b0001', '1', 'ba_step3', 'usertask3', '省公司权签审批', '0', '8a924084574fd67f0157508964c50009', null, '部署新流程时,系统分析部署文件自动导入。', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.482', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.482');
INSERT INTO "public"."iyun_flow_workrole" VALUES ('8a924084575145da01575147d09c0002', '1', 'ba_step4', 'usertask4', '需求调度', '0', '8a924084574fd67f015750886de30007', null, '部署新流程时,系统分析部署文件自动导入。', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.484', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.484');
INSERT INTO "public"."iyun_flow_workrole" VALUES ('8a924084575145da01575147d09d0003', '1', 'ba_step5', 'usertask5', '需求处理', '0', '8a924084574fd67f01575088a5cb0008', null, '部署新流程时,系统分析部署文件自动导入。', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.485', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.485');
INSERT INTO "public"."iyun_flow_workrole" VALUES ('8a924084575145da01575147d0a10004', '1', 'ba_step6', 'usertask6', '测试验证', '0', '8a924084574fd67f015750898904000a', null, '部署新流程时,系统分析部署文件自动导入。', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.486', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 17:44:03.486');

-- ----------------------------
-- Table structure for iyun_sm_department
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_department";
CREATE TABLE "public"."iyun_sm_department" (
"id" varchar(36) COLLATE "default" NOT NULL,
"projectid" varchar(50) COLLATE "default" NOT NULL,
"deptcode" varchar(36) COLLATE "default" NOT NULL,
"deptdesc" varchar(500) COLLATE "default",
"parentid" varchar(36) COLLATE "default",
"depth" int2,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"deptname" varchar(36) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_department
-- ----------------------------
INSERT INTO "public"."iyun_sm_department" VALUES ('8a92408456ba52ab0156ba5339330000', '1', '华三通信', '', '-1', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-08-24 10:13:51.515', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-24 13:53:06.055', '华三通信');
INSERT INTO "public"."iyun_sm_department" VALUES ('8a92408456ba52ab0156ba5339330001', '2', '10000', '浙江电信省公司', '-1', '1', ' ', '1', '2016-10-13 18:30:54', '1', '2016-10-13 18:30:57', '浙江电信省公司');
INSERT INTO "public"."iyun_sm_department" VALUES ('8a92408456ba52ab0156ba5339330002', '2', '10001', '政企部', '8a92408456ba52ab0156ba5339330001', '2', ' ', '1', '2016-10-13 18:32:18', '1', '2016-10-13 18:32:22', '政企部');
INSERT INTO "public"."iyun_sm_department" VALUES ('ff808081580059bd0158006388040003', '1', '123123', '1', '8a92408456ba52ab0156ba5339330000', '2', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-26 17:47:52.708', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-26 17:47:52.708', '123');

-- ----------------------------
-- Table structure for iyun_sm_group
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_group";
CREATE TABLE "public"."iyun_sm_group" (
"id" varchar(36) COLLATE "default" NOT NULL,
"groupname" varchar(50) COLLATE "default" NOT NULL,
"blongtoid" varchar(36) COLLATE "default",
"description" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"flag" varchar(1) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_group
-- ----------------------------
INSERT INTO "public"."iyun_sm_group" VALUES ('ff80808157bf09090157d2603ed2005e', '默认群组', ' ', '默认群组', ' ', '1', '2016-10-20 19:06:46', '1', '2016-10-20 19:06:50', '0');

-- ----------------------------
-- Table structure for iyun_sm_login2logs
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_login2logs";
CREATE TABLE "public"."iyun_sm_login2logs" (
"id" varchar(36) COLLATE "default" NOT NULL,
"userid" varchar(36) COLLATE "default" NOT NULL,
"logtypeid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"remark" varchar(500) COLLATE "default",
"result" varchar(32) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for iyun_sm_logtype
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_logtype";
CREATE TABLE "public"."iyun_sm_logtype" (
"id" varchar(36) COLLATE "default" NOT NULL,
"description" varchar(100) COLLATE "default" NOT NULL,
"remark" varchar(100) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_logtype
-- ----------------------------
INSERT INTO "public"."iyun_sm_logtype" VALUES ('1', '用户登录日志', '登录日志', '1', '1', '2016-10-29 11:13:33', '1', '2016-10-29 11:13:35');

-- ----------------------------
-- Table structure for iyun_sm_project
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_project";
CREATE TABLE "public"."iyun_sm_project" (
"id" varchar(64) COLLATE "default" NOT NULL,
"name" varchar(64) COLLATE "default",
"extra" text COLLATE "default",
"description" text COLLATE "default",
"enabled" bool,
"domain_id" varchar(64) COLLATE "default",
"parent_id" varchar(64) COLLATE "default",
"flag" int2
)
WITH (OIDS=FALSE)

;
COMMENT ON COLUMN "public"."iyun_sm_project"."enabled" IS '0-用户新建1-从cloudos同步';
COMMENT ON COLUMN "public"."iyun_sm_project"."flag" IS '-1-已删除0-待执行1执行中2-已执行3-执行失败';

-- ----------------------------
-- Records of iyun_sm_project
-- ----------------------------
INSERT INTO "public"."iyun_sm_project" VALUES ('1', 'H3C', null, null, null, null, null, null);
INSERT INTO "public"."iyun_sm_project" VALUES ('2', '电信', null, null, null, null, '1', null);

-- ----------------------------
-- Table structure for iyun_sm_resources
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_resources";
CREATE TABLE "public"."iyun_sm_resources" (
"id" varchar(36) COLLATE "default" NOT NULL,
"resname" varchar(100) COLLATE "default" NOT NULL,
"modename" varchar(100) COLLATE "default",
"systype" varchar(100) COLLATE "default" NOT NULL,
"respath" varchar(100) COLLATE "default" NOT NULL,
"parentid" varchar(36) COLLATE "default" NOT NULL,
"depth" int2,
"defaultroleid" varchar(36) COLLATE "default" NOT NULL,
"description" varchar(50) COLLATE "default",
"itemseq" int2,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"authtype" varchar(100) COLLATE "default",
"funtype" varchar(100) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_resources
-- ----------------------------
INSERT INTO "public"."iyun_sm_resources" VALUES ('1', '行业云', '行业云配置库1111', '0000', '/', '-1', '1', '', null, '-1', '', '1', '2016-08-22 15:46:13', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-22 16:56:50.57', null, null);

-- ----------------------------
-- Table structure for iyun_sm_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_role";
CREATE TABLE "public"."iyun_sm_role" (
"id" varchar(36) COLLATE "default" NOT NULL,
"projectid" varchar(50) COLLATE "default" NOT NULL,
"rolename" varchar(100) COLLATE "default" NOT NULL,
"roledesc" varchar(100) COLLATE "default",
"proleid" varchar(36) COLLATE "default",
"flag" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_role
-- ----------------------------
INSERT INTO "public"."iyun_sm_role" VALUES ('8a8a700d57b83c3f0157b848b33b0000', '1', '租户管理员', '租户管理员', '8a8a700d57b83c3f0157b848b33b0000', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:45:54.741', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:45:54.741');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a8a700d57b83c3f0157b848cba00001', '1', '云运维管理员', '云运维管理员', '8a8a700d57b83c3f0157b848cba00001', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:46:00.986', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:46:00.986');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a8a700d57b83c3f0157b848d2c20002', '1', '电信管理员', '电信管理员', '8a8a700d57b83c3f0157b848d2c20002', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:46:02.81', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:46:02.81');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a8a700d57b83c3f0157b8499de00003', '1', '云运营管理员', '云运营管理员', '8a8a700d57b83c3f0157b8499de00003', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-12 17:46:54.812', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-21 19:07:15.305');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a924084574fd67f01575086b7af0006', '1', '区域经理审批', null, '8a8a700d57b83c3f0157b848cba00001', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:13:08.648', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:13:08.648');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a924084574fd67f015750886de30007', '1', '需求调度', null, '8a8a700d57b83c3f0157b848cba00001', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:15:00.83', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:15:00.83');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a924084574fd67f01575088a5cb0008', '1', '需求处理', '123', '8a8a700d57b83c3f0157b848cba00001', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:15:15.141', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-18 19:35:50.701');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a924084574fd67f0157508964c50009', '1', '省公司权签审批', null, '8a8a700d57b83c3f0157b848cba00001', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:16:04.031', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:16:04.031');
INSERT INTO "public"."iyun_sm_role" VALUES ('8a924084574fd67f015750898904000a', '1', '测试验证', null, '8a8a700d57b83c3f0157b848cba00001', '1', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-09-22 14:16:13.312', '8a8a700d57bf0a370157bf0afe770000', '2016-10-18 15:21:09.45');

-- ----------------------------
-- Table structure for iyun_sm_role2res
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_role2res";
CREATE TABLE "public"."iyun_sm_role2res" (
"id" varchar(36) COLLATE "default" NOT NULL,
"roleid" varchar(36) COLLATE "default" NOT NULL,
"resid" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for iyun_sm_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_user";
CREATE TABLE "public"."iyun_sm_user" (
"id" varchar(36) COLLATE "default" NOT NULL,
"deptid" varchar(36) COLLATE "default",
"loginname" varchar(100) COLLATE "default" NOT NULL,
"username" varchar(100) COLLATE "default" NOT NULL,
"isadmin" varchar(1) COLLATE "default",
"idcard" varchar(20) COLLATE "default",
"password" varchar(100) COLLATE "default",
"remark" varchar(100) COLLATE "default",
"email" varchar(100) COLLATE "default",
"telephone" varchar(100) COLLATE "default",
"status" varchar(1) COLLATE "default",
"effectivedate" timestamp(6),
"expiredate" timestamp(6),
"indomain" varchar(1) COLLATE "default" NOT NULL,
"defaultgroupid" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"projectid" varchar(36) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_user
-- ----------------------------
INSERT INTO "public"."iyun_sm_user" VALUES ('8a8a700d581e810401581ed080be0008', '8a92408456ba52ab0156ba5339330000', 'y001', 'y001', '1', '000000', 'A/CgjoZauw0=', null, '', '15165861570', '0', null, null, '0', '', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f47ccbb9000a', '2016-11-01 15:35:30.743', 'ff80808157f460230157f47ccbb9000a', '2016-11-01 15:35:30.743', '1');
INSERT INTO "public"."iyun_sm_user" VALUES ('989116e3-79a2-426b-bfbe-668165104885', '8a92408456ba52ab0156ba5339330000', 'admin', 'admine18', '0', '12311', 'vZfiy9xgM9Q=', '111', null, null, '0', null, null, '1', '', '1', '1', '2016-08-22 12:00:40', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-10 11:35:59.11', '1');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157b2855b0157b30496c30025', '8a92408456ba52ab0156ba5339330002', 'ctUser2', '电信区域经理', '1', null, '4g3IFalZ5hc=', null, null, null, '0', null, null, '0', '', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', 'ff80808157b2855b0157b30496c30027', '2016-10-24 10:29:19.981', '2');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157b2855b0157b30496c30026', '8a92408456ba52ab0156ba5339330002', 'ctUser1', '客户经理', '1', null, 'jKLd6/5/R/k=', null, null, null, '0', null, null, '0', '', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '2');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157b2855b0157b30496c30027', '8a92408456ba52ab0156ba5339330001', 'ctUser3', '省公司权签审批', '1', null, 'Vum82GuM5KI=', null, null, null, '0', null, null, '0', '', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '2');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157b2855b0157b30496c30029', '8a92408456ba52ab0156ba5339330000', 'h3cUser2', 'H3C需求处理人', '1', null, 'LIjf+ty0U1nLuxrtqPnuLQ==', null, null, null, '0', null, null, '0', '', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '1');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157b2855b0157b30496c30036', '8a92408456ba52ab0156ba5339330001', 'ctAdmin', '电信租户管理员', '1', null, 'hnwoS8V0w2Q=', null, null, null, '0', null, null, '0', '', '', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-11 17:13:24.931', '2');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157f460230157f47ccbb9000a', '8a92408456ba52ab0156ba5339330000', 'h3cUser1', 'H3C需求调度人', '1', '', '/SdIpQ+odW0UKKATly1euQ==', null, '', '', '0', null, null, '0', '', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-24 10:20:01.849', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-24 10:20:01.849', '1');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff80808157f460230157f4d12fcf0029', '8a92408456ba52ab0156ba5339330000', 'h3cUser3', 'H3C测试验证', '1', '', 'LIVzMz1NbmHLuxrtqPnuLQ==', null, '', '', '0', null, null, '0', '', 'ff80808157bf09090157d2603ed2005e', 'ff80808157b2855b0157b30496c30029', '2016-10-24 11:52:12.495', 'ff80808157b2855b0157b30496c30029', '2016-10-24 11:52:12.495', '1');
INSERT INTO "public"."iyun_sm_user" VALUES ('ff8080815819e81801581dabee9d0034', '8a92408456ba52ab0156ba5339330000', 'testUser1', 'testUser1', '1', 'testUser1', 'aF+VgO7c2UH+I2WyV3l4pg==', null, '123@qq.com', '15555555555', '0', '2016-11-01 12:11:16', '2016-11-24 12:11:19', '0', '', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f4d12fcf0029', '2016-11-01 10:15:56.827', 'ff80808157f460230157f4d12fcf0029', '2016-11-01 10:15:56.827', '1');

-- ----------------------------
-- Table structure for iyun_sm_user2group
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_user2group";
CREATE TABLE "public"."iyun_sm_user2group" (
"id" varchar(36) COLLATE "default" NOT NULL,
"userid" varchar(36) COLLATE "default" NOT NULL,
"gid" varchar(36) COLLATE "default",
"isdefault" varchar(1) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_user2group
-- ----------------------------
INSERT INTO "public"."iyun_sm_user2group" VALUES ('8a8a700d57e1f2cc0157e605171a0029', 'ff80808157b2855b0157b30496c30025', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-21 14:54:35.802', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-21 14:54:35.802');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('8a8a700d57e1f2cc0157e6051725002b', 'ff80808157b2855b0157b30496c30026', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-21 14:54:35.813', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-21 14:54:35.813');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('8a8a700d57e1f2cc0157ebb8b33c0086', 'ff80808157b2855b0157b30496c30029', 'ff80808157bf09090157d2603ed2005e', '1', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-22 17:28:52.796', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-22 17:28:52.796');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('8a8a700d581e810401581ed080ce0009', '8a8a700d581e810401581ed080be0008', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f47ccbb9000a', '2016-11-01 15:35:30.763', 'ff80808157f460230157f47ccbb9000a', '2016-11-01 15:35:30.763');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('ff80808157ea62a10157eb093de6002b', '989116e3-79a2-426b-bfbe-668165104885', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-22 14:17:13.958', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-22 14:17:13.958');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('ff80808157f460230157f47ccbbe000b', 'ff80808157f460230157f47ccbb9000a', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-24 10:20:01.854', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-24 10:20:01.854');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('ff80808157f460230157f4d12fda002a', 'ff80808157f460230157f4d12fcf0029', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', 'ff80808157b2855b0157b30496c30029', '2016-10-24 11:52:12.506', 'ff80808157b2855b0157b30496c30029', '2016-10-24 11:52:12.506');
INSERT INTO "public"."iyun_sm_user2group" VALUES ('ff8080815819e81801581dabeea40035', 'ff8080815819e81801581dabee9d0034', 'ff80808157bf09090157d2603ed2005e', '0', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f4d12fcf0029', '2016-11-01 10:15:56.836', 'ff80808157f460230157f4d12fcf0029', '2016-11-01 10:15:56.836');

-- ----------------------------
-- Table structure for iyun_sm_user2role
-- ----------------------------
DROP TABLE IF EXISTS "public"."iyun_sm_user2role";
CREATE TABLE "public"."iyun_sm_user2role" (
"id" varchar(36) COLLATE "default" NOT NULL,
"userid" varchar(36) COLLATE "default" NOT NULL,
"roleid" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of iyun_sm_user2role
-- ----------------------------
INSERT INTO "public"."iyun_sm_user2role" VALUES ('22', '989116e3-79a2-426b-bfbe-668165104885', '8a924084574fd67f0157508964c50009', '11', '1', '2016-10-26 15:17:25', '1', '2016-10-26 15:17:29');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('8a8a700d57ff8c8b0157ffc296e00011', '989116e3-79a2-426b-bfbe-668165104885', '8a8a700d57b83c3f0157b848cba00001', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f47ccbb9000a', '2016-10-26 14:52:05.194', 'ff80808157f460230157f47ccbb9000a', '2016-10-26 14:52:05.194');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('8a8a700d57ff8c8b0157ffd7fa2c0018', 'ff80808157b2855b0157b30496c30025', '8a924084574fd67f01575086b7af0006', 'ff80808157bf09090157d2603ed2005e', 'ff80808157b2855b0157b30496c30025', '2016-10-26 15:15:26.886', 'ff80808157b2855b0157b30496c30025', '2016-10-26 15:15:26.886');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('8a8a700d57ff8c8b0157ffda1bfd001d', 'ff80808157b2855b0157b30496c30027', '8a924084574fd67f0157508964c50009', 'ff80808157bf09090157d2603ed2005e', 'ff80808157b2855b0157b30496c30025', '2016-10-26 15:17:46.615', 'ff80808157b2855b0157b30496c30025', '2016-10-26 15:17:46.615');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('8a8a700d57fff3fe0158001066ea0004', 'ff80808157b2855b0157b30496c30036', '8a8a700d57b83c3f0157b8499de00003', 'ff80808157bf09090157d2603ed2005e', 'ff80808157b2855b0157b30496c30036', '2016-10-26 16:17:04.728', 'ff80808157b2855b0157b30496c30036', '2016-10-26 16:17:04.728');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('8a8a700d57fff3fe0158001c74920005', 'ff80808157f460230157f4d12fcf0029', '8a924084574fd67f015750898904000a', 'ff80808157bf09090157d2603ed2005e', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-26 16:30:14.674', '989116e3-79a2-426b-bfbe-668165104885', '2016-10-26 16:30:14.674');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('ff80808157f460230157f47f8775000f', 'ff80808157f460230157f47ccbb9000a', '8a924084574fd67f015750886de30007', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f47ccbb9000a', '2016-10-24 10:23:00.981', 'ff80808157f460230157f47ccbb9000a', '2016-10-24 10:23:00.981');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('ff80808157f460230157f47fa4890010', 'ff80808157b2855b0157b30496c30029', '8a924084574fd67f01575088a5cb0008', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f47ccbb9000a', '2016-10-24 10:23:08.425', 'ff80808157f460230157f47ccbb9000a', '2016-10-24 10:23:08.425');
INSERT INTO "public"."iyun_sm_user2role" VALUES ('ff8080815819e81801581dac23a80036', 'ff8080815819e81801581dabee9d0034', '8a924084574fd67f015750886de30007', 'ff80808157bf09090157d2603ed2005e', 'ff80808157f460230157f4d12fcf0029', '2016-11-01 10:16:10.408', 'ff80808157f460230157f4d12fcf0029', '2016-11-01 10:16:10.408');

-- ----------------------------
-- Table structure for rtu_base_sla
-- ----------------------------
DROP TABLE IF EXISTS "public"."rtu_base_sla";
CREATE TABLE "public"."rtu_base_sla" (
"id" varchar(36) COLLATE "default" NOT NULL,
"levelname" varchar(100) COLLATE "default" NOT NULL,
"finishtimes" float4 NOT NULL,
"closetimes" float4 NOT NULL,
"belongto" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of rtu_base_sla
-- ----------------------------

-- ----------------------------
-- Table structure for rtu_base_type
-- ----------------------------
DROP TABLE IF EXISTS "public"."rtu_base_type";
CREATE TABLE "public"."rtu_base_type" (
"id" varchar(36) COLLATE "default" NOT NULL,
"typename" varchar(100) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of rtu_base_type
-- ----------------------------

-- ----------------------------
-- Table structure for rtu_jobs_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."rtu_jobs_master";
CREATE TABLE "public"."rtu_jobs_master" (
"id" varchar(36) COLLATE "default" NOT NULL,
"jobname" varchar(100) COLLATE "default" NOT NULL,
"jobcode" varchar(100) COLLATE "default" NOT NULL,
"responsible" varchar(36) COLLATE "default",
"slaflag" varchar(36) COLLATE "default",
"step" varchar(36) COLLATE "default",
"company" varchar(36) COLLATE "default",
"customer" varchar(100) COLLATE "default",
"telphone" varchar(100) COLLATE "default",
"email" varchar(100) COLLATE "default",
"reporter" varchar(100) COLLATE "default",
"fromto" varchar(36) COLLATE "default",
"ways" varchar(36) COLLATE "default",
"causedtime" timestamp(6),
"reqftime" timestamp(6),
"actftime" timestamp(6),
"priority" varchar(36) COLLATE "default",
"sla级别" varchar(36) COLLATE "default",
"type" varchar(36) COLLATE "default" NOT NULL,
"topic" varchar(100) COLLATE "default",
"content" varchar(500) COLLATE "default",
"rtuflag" varchar(100) COLLATE "default",
"srcto" varchar(100) COLLATE "default",
"instanceid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL,
"workflowid" varchar(36) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of rtu_jobs_master
-- ----------------------------

-- ----------------------------
-- Table structure for rtu_jobs_master2actions
-- ----------------------------
DROP TABLE IF EXISTS "public"."rtu_jobs_master2actions";
CREATE TABLE "public"."rtu_jobs_master2actions" (
"id" varchar(36) COLLATE "default" NOT NULL,
"jobid" varchar(36) COLLATE "default",
"actor" varchar(36) COLLATE "default",
"acttime" timestamp(6),
"step" varchar(100) COLLATE "default",
"content" varchar(500) COLLATE "default",
"speadtime" float4,
"upflag" varchar(100) COLLATE "default",
"upcause" varchar(500) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of rtu_jobs_master2actions
-- ----------------------------

-- ----------------------------
-- Table structure for rtu_jobs_master2approvelog
-- ----------------------------
DROP TABLE IF EXISTS "public"."rtu_jobs_master2approvelog";
CREATE TABLE "public"."rtu_jobs_master2approvelog" (
"id" varchar(36) COLLATE "default" NOT NULL,
"jobid" varchar(36) COLLATE "default",
"insid" varchar(36) COLLATE "default" NOT NULL,
"step" varchar(100) COLLATE "default" NOT NULL,
"taskid" varchar(36) COLLATE "default" NOT NULL,
"option" varchar(36) COLLATE "default" NOT NULL,
"comment" varchar(500) COLLATE "default",
"approver" varchar(36) COLLATE "default" NOT NULL,
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of rtu_jobs_master2approvelog
-- ----------------------------

-- ----------------------------
-- Table structure for rtu_jobs_master2classes
-- ----------------------------
DROP TABLE IF EXISTS "public"."rtu_jobs_master2classes";
CREATE TABLE "public"."rtu_jobs_master2classes" (
"id" varchar(36) COLLATE "default" NOT NULL,
"jobid" varchar(36) COLLATE "default",
"cdetailid" varchar(36) COLLATE "default",
"groupid" varchar(36) COLLATE "default" NOT NULL,
"createdby" varchar(36) COLLATE "default" NOT NULL,
"createddate" timestamp(6) NOT NULL,
"updatedby" varchar(36) COLLATE "default" NOT NULL,
"updateddate" timestamp(6) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of rtu_jobs_master2classes
-- ----------------------------

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table auto_sync_log
-- ----------------------------
ALTER TABLE "public"."auto_sync_log" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table auto_sync_logcollect
-- ----------------------------
ALTER TABLE "public"."auto_sync_logcollect" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table auto_sync_taskdispactchh
-- ----------------------------
ALTER TABLE "public"."auto_sync_taskdispactchh" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table auto_sync_taskdispatch
-- ----------------------------
ALTER TABLE "public"."auto_sync_taskdispatch" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table bus_cis_contact
-- ----------------------------
ALTER TABLE "public"."bus_cis_contact" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table bus_cis_custom
-- ----------------------------
ALTER TABLE "public"."bus_cis_custom" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table bus_req_items
-- ----------------------------
ALTER TABLE "public"."bus_req_items" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table bus_req_master
-- ----------------------------
ALTER TABLE "public"."bus_req_master" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table bus_req_master2approvelog
-- ----------------------------
ALTER TABLE "public"."bus_req_master2approvelog" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_base_class
-- ----------------------------
ALTER TABLE "public"."chg_base_class" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_base_class2approver
-- ----------------------------
ALTER TABLE "public"."chg_base_class2approver" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_ins_exttask
-- ----------------------------
ALTER TABLE "public"."chg_ins_exttask" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_ins_statmachin
-- ----------------------------
ALTER TABLE "public"."chg_ins_statmachin" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_app2attach
-- ----------------------------
ALTER TABLE "public"."chg_req_app2attach" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_app2director
-- ----------------------------
ALTER TABLE "public"."chg_req_app2director" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_app2dispatch
-- ----------------------------
ALTER TABLE "public"."chg_req_app2dispatch" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_app2execute
-- ----------------------------
ALTER TABLE "public"."chg_req_app2execute" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_app2standard
-- ----------------------------
ALTER TABLE "public"."chg_req_app2standard" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_app2validate
-- ----------------------------
ALTER TABLE "public"."chg_req_app2validate" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_application
-- ----------------------------
ALTER TABLE "public"."chg_req_application" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chg_req_attach2detail
-- ----------------------------
ALTER TABLE "public"."chg_req_attach2detail" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_asset2drawer
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_asset2drawer" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_class2items
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_class2items" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_extavalue
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_extavalue" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_extcolumns
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_extcolumns" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_linkto
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_linkto" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_maintenancs
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_maintenancs" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2boards
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2boards" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2log
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2log" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2other
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2other" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2router
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2router" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2server
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2server" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2stock
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2stock" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_master2switch
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2switch" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_netports
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_netports" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_asm_sware
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_sware" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cap_freeserver
-- ----------------------------
ALTER TABLE "public"."cmdb_cap_freeserver" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cap_netflow
-- ----------------------------
ALTER TABLE "public"."cmdb_cap_netflow" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cap_server
-- ----------------------------
ALTER TABLE "public"."cmdb_cap_server" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cap_storages
-- ----------------------------
ALTER TABLE "public"."cmdb_cap_storages" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cap_tablespace
-- ----------------------------
ALTER TABLE "public"."cmdb_cap_tablespace" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cloud_server2vm
-- ----------------------------
ALTER TABLE "public"."cmdb_cloud_server2vm" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_cloud_vnetports
-- ----------------------------
ALTER TABLE "public"."cmdb_cloud_vnetports" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_dc_draws
-- ----------------------------
ALTER TABLE "public"."cmdb_dc_draws" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_dc_iprelation
-- ----------------------------
ALTER TABLE "public"."cmdb_dc_iprelation" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_dc_iprelationh
-- ----------------------------
ALTER TABLE "public"."cmdb_dc_iprelationh" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_dc_rooms
-- ----------------------------
ALTER TABLE "public"."cmdb_dc_rooms" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_router_groups
-- ----------------------------
ALTER TABLE "public"."cmdb_router_groups" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_router_groups2items
-- ----------------------------
ALTER TABLE "public"."cmdb_router_groups2items" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_server_cluster2items
-- ----------------------------
ALTER TABLE "public"."cmdb_server_cluster2items" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_server_clusters
-- ----------------------------
ALTER TABLE "public"."cmdb_server_clusters" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_server_groups
-- ----------------------------
ALTER TABLE "public"."cmdb_server_groups" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_server_pools2host
-- ----------------------------
ALTER TABLE "public"."cmdb_server_pools2host" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_storage_clusters
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_clusters" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_storage_groups
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_groups" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_storage_groups2items
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_groups2items" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_storage_manage
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_manage" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_storage_volume2host
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_volume2host" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_storage_volums
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_volums" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_switch_groups
-- ----------------------------
ALTER TABLE "public"."cmdb_switch_groups" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_switch_groups2items
-- ----------------------------
ALTER TABLE "public"."cmdb_switch_groups2items" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_www_iprelation
-- ----------------------------
ALTER TABLE "public"."cmdb_www_iprelation" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table cmdb_www_iprelationh
-- ----------------------------
ALTER TABLE "public"."cmdb_www_iprelationh" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_account_accounts
-- ----------------------------
ALTER TABLE "public"."iyun_account_accounts" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_account_app2detail
-- ----------------------------
ALTER TABLE "public"."iyun_account_app2detail" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table iyun_account_application
-- ----------------------------
ALTER TABLE "public"."iyun_account_application" ADD CHECK ((((isspeci >= '0'::bpchar) AND (isspeci <= '1'::bpchar)) AND (isspeci = ANY (ARRAY['0'::bpchar, '1'::bpchar]))));

-- ----------------------------
-- Primary Key structure for table iyun_account_application
-- ----------------------------
ALTER TABLE "public"."iyun_account_application" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_account_auth2user
-- ----------------------------
ALTER TABLE "public"."iyun_account_auth2user" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_account_pwdrecord
-- ----------------------------
ALTER TABLE "public"."iyun_account_pwdrecord" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_emailquenu
-- ----------------------------
ALTER TABLE "public"."iyun_base_emailquenu" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_flavor
-- ----------------------------
ALTER TABLE "public"."iyun_base_flavor" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Checks structure for table iyun_base_initcode
-- ----------------------------
ALTER TABLE "public"."iyun_base_initcode" ADD CHECK ((((status >= '0'::bpchar) AND (status <= '1'::bpchar)) AND (status = ANY (ARRAY['0'::bpchar, '1'::bpchar]))));

-- ----------------------------
-- Primary Key structure for table iyun_base_initcode
-- ----------------------------
ALTER TABLE "public"."iyun_base_initcode" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_mattach
-- ----------------------------
ALTER TABLE "public"."iyun_base_mattach" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_prd2templates
-- ----------------------------
ALTER TABLE "public"."iyun_base_prd2templates" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_prdclass
-- ----------------------------
ALTER TABLE "public"."iyun_base_prdclass" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_prdclass2detail
-- ----------------------------
ALTER TABLE "public"."iyun_base_prdclass2detail" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_rules
-- ----------------------------
ALTER TABLE "public"."iyun_base_rules" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_sharedoc
-- ----------------------------
ALTER TABLE "public"."iyun_base_sharedoc" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_base_singleflavors
-- ----------------------------
ALTER TABLE "public"."iyun_base_singleflavors" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_flow_workflow
-- ----------------------------
ALTER TABLE "public"."iyun_flow_workflow" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_flow_workrole
-- ----------------------------
ALTER TABLE "public"."iyun_flow_workrole" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_department
-- ----------------------------
ALTER TABLE "public"."iyun_sm_department" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_group
-- ----------------------------
ALTER TABLE "public"."iyun_sm_group" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_login2logs
-- ----------------------------
ALTER TABLE "public"."iyun_sm_login2logs" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_logtype
-- ----------------------------
ALTER TABLE "public"."iyun_sm_logtype" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_project
-- ----------------------------
ALTER TABLE "public"."iyun_sm_project" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_resources
-- ----------------------------
ALTER TABLE "public"."iyun_sm_resources" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_role
-- ----------------------------
ALTER TABLE "public"."iyun_sm_role" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_role2res
-- ----------------------------
ALTER TABLE "public"."iyun_sm_role2res" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_user
-- ----------------------------
ALTER TABLE "public"."iyun_sm_user" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_user2group
-- ----------------------------
ALTER TABLE "public"."iyun_sm_user2group" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table iyun_sm_user2role
-- ----------------------------
ALTER TABLE "public"."iyun_sm_user2role" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rtu_base_sla
-- ----------------------------
ALTER TABLE "public"."rtu_base_sla" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rtu_base_type
-- ----------------------------
ALTER TABLE "public"."rtu_base_type" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rtu_jobs_master
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rtu_jobs_master2actions
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master2actions" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rtu_jobs_master2approvelog
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master2approvelog" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table rtu_jobs_master2classes
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master2classes" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."auto_sync_log"
-- ----------------------------
ALTER TABLE "public"."auto_sync_log" ADD FOREIGN KEY ("id") REFERENCES "public"."auto_sync_logcollect" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."auto_sync_logcollect"
-- ----------------------------
ALTER TABLE "public"."auto_sync_logcollect" ADD FOREIGN KEY ("syncid") REFERENCES "public"."auto_sync_taskdispatch" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."auto_sync_taskdispactchh"
-- ----------------------------
ALTER TABLE "public"."auto_sync_taskdispactchh" ADD FOREIGN KEY ("syncid") REFERENCES "public"."auto_sync_taskdispatch" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."bus_cis_contact"
-- ----------------------------
ALTER TABLE "public"."bus_cis_contact" ADD FOREIGN KEY ("cusid") REFERENCES "public"."bus_cis_custom" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."bus_req_items"
-- ----------------------------
ALTER TABLE "public"."bus_req_items" ADD FOREIGN KEY ("classid") REFERENCES "public"."iyun_base_prdclass" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."bus_req_items" ADD FOREIGN KEY ("reqid") REFERENCES "public"."bus_req_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."bus_req_master"
-- ----------------------------
ALTER TABLE "public"."bus_req_master" ADD FOREIGN KEY ("cusid") REFERENCES "public"."bus_cis_custom" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."bus_req_master2approvelog"
-- ----------------------------
ALTER TABLE "public"."bus_req_master2approvelog" ADD FOREIGN KEY ("reqid") REFERENCES "public"."bus_req_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_base_class2approver"
-- ----------------------------
ALTER TABLE "public"."chg_base_class2approver" ADD FOREIGN KEY ("itemid") REFERENCES "public"."chg_base_class" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_ins_exttask"
-- ----------------------------
ALTER TABLE "public"."chg_ins_exttask" ADD FOREIGN KEY ("chgid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_ins_statmachin"
-- ----------------------------
ALTER TABLE "public"."chg_ins_statmachin" ADD FOREIGN KEY ("changeid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_app2attach"
-- ----------------------------
ALTER TABLE "public"."chg_req_app2attach" ADD FOREIGN KEY ("attachid") REFERENCES "public"."chg_req_attach2detail" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."chg_req_app2attach" ADD FOREIGN KEY ("chgid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_app2director"
-- ----------------------------
ALTER TABLE "public"."chg_req_app2director" ADD FOREIGN KEY ("changeid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_app2dispatch"
-- ----------------------------
ALTER TABLE "public"."chg_req_app2dispatch" ADD FOREIGN KEY ("chgid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_app2execute"
-- ----------------------------
ALTER TABLE "public"."chg_req_app2execute" ADD FOREIGN KEY ("changeid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_app2standard"
-- ----------------------------
ALTER TABLE "public"."chg_req_app2standard" ADD FOREIGN KEY ("chgid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_app2validate"
-- ----------------------------
ALTER TABLE "public"."chg_req_app2validate" ADD FOREIGN KEY ("chgid") REFERENCES "public"."chg_req_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."chg_req_application"
-- ----------------------------
ALTER TABLE "public"."chg_req_application" ADD FOREIGN KEY ("chgtype") REFERENCES "public"."chg_base_class" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_asset2drawer"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_asset2drawer" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_asm_asset2drawer" ADD FOREIGN KEY ("drawsid") REFERENCES "public"."cmdb_dc_draws" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_extavalue"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_extavalue" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_asm_extavalue" ADD FOREIGN KEY ("extid") REFERENCES "public"."cmdb_asm_extcolumns" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_linkto"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_linkto" ADD FOREIGN KEY ("trunkto") REFERENCES "public"."cmdb_asm_netports" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_asm_linkto" ADD FOREIGN KEY ("accessto") REFERENCES "public"."cmdb_asm_netports" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master" ADD FOREIGN KEY ("assmode") REFERENCES "public"."cmdb_asm_class2items" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2boards"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2boards" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2log"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2log" ADD FOREIGN KEY ("assetid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2other"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2other" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2router"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2router" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2server"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2server" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2stock"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2stock" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_master2switch"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_master2switch" ADD FOREIGN KEY ("id") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_asm_netports"
-- ----------------------------
ALTER TABLE "public"."cmdb_asm_netports" ADD FOREIGN KEY ("masterid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_cloud_server2vm"
-- ----------------------------
ALTER TABLE "public"."cmdb_cloud_server2vm" ADD FOREIGN KEY ("hostid") REFERENCES "public"."cmdb_server_cluster2items" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_cloud_vnetports"
-- ----------------------------
ALTER TABLE "public"."cmdb_cloud_vnetports" ADD FOREIGN KEY ("vmid") REFERENCES "public"."cmdb_cloud_server2vm" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_dc_draws"
-- ----------------------------
ALTER TABLE "public"."cmdb_dc_draws" ADD FOREIGN KEY ("roomid") REFERENCES "public"."cmdb_dc_rooms" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_router_groups2items"
-- ----------------------------
ALTER TABLE "public"."cmdb_router_groups2items" ADD FOREIGN KEY ("masterid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_router_groups2items" ADD FOREIGN KEY ("stackid") REFERENCES "public"."cmdb_router_groups" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_server_cluster2items"
-- ----------------------------
ALTER TABLE "public"."cmdb_server_cluster2items" ADD FOREIGN KEY ("assid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_server_cluster2items" ADD FOREIGN KEY ("phostid") REFERENCES "public"."cmdb_server_pools2host" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_server_cluster2items" ADD FOREIGN KEY ("custertid") REFERENCES "public"."cmdb_server_clusters" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_server_clusters"
-- ----------------------------
ALTER TABLE "public"."cmdb_server_clusters" ADD FOREIGN KEY ("phostid") REFERENCES "public"."cmdb_server_pools2host" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_storage_clusters"
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_clusters" ADD FOREIGN KEY ("gid") REFERENCES "public"."cmdb_storage_groups" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_storage_groups2items"
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_groups2items" ADD FOREIGN KEY ("cid") REFERENCES "public"."cmdb_storage_clusters" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_storage_groups2items" ADD FOREIGN KEY ("masterid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_storage_manage"
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_manage" ADD FOREIGN KEY ("groupid") REFERENCES "public"."cmdb_storage_groups" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_storage_volume2host"
-- ----------------------------
ALTER TABLE "public"."cmdb_storage_volume2host" ADD FOREIGN KEY ("volumeid") REFERENCES "public"."cmdb_storage_volums" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_switch_groups2items"
-- ----------------------------
ALTER TABLE "public"."cmdb_switch_groups2items" ADD FOREIGN KEY ("masterid") REFERENCES "public"."cmdb_asm_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."cmdb_switch_groups2items" ADD FOREIGN KEY ("stackid") REFERENCES "public"."cmdb_switch_groups" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."cmdb_www_iprelationh"
-- ----------------------------
ALTER TABLE "public"."cmdb_www_iprelationh" ADD FOREIGN KEY ("ip") REFERENCES "public"."cmdb_www_iprelation" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_account_app2detail"
-- ----------------------------
ALTER TABLE "public"."iyun_account_app2detail" ADD FOREIGN KEY ("appid") REFERENCES "public"."iyun_account_application" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."iyun_account_app2detail" ADD FOREIGN KEY ("accountid") REFERENCES "public"."iyun_account_accounts" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_account_auth2user"
-- ----------------------------
ALTER TABLE "public"."iyun_account_auth2user" ADD FOREIGN KEY ("account") REFERENCES "public"."iyun_account_accounts" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_account_pwdrecord"
-- ----------------------------
ALTER TABLE "public"."iyun_account_pwdrecord" ADD FOREIGN KEY ("accountid") REFERENCES "public"."iyun_account_accounts" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_base_mattach"
-- ----------------------------
ALTER TABLE "public"."iyun_base_mattach" ADD FOREIGN KEY ("mid") REFERENCES "public"."iyun_base_emailquenu" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_base_prd2templates"
-- ----------------------------
ALTER TABLE "public"."iyun_base_prd2templates" ADD FOREIGN KEY ("classid") REFERENCES "public"."iyun_base_prdclass" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_base_prdclass2detail"
-- ----------------------------
ALTER TABLE "public"."iyun_base_prdclass2detail" ADD FOREIGN KEY ("classid") REFERENCES "public"."iyun_base_prdclass" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_flow_workrole"
-- ----------------------------
ALTER TABLE "public"."iyun_flow_workrole" ADD FOREIGN KEY ("workflowid") REFERENCES "public"."iyun_flow_workflow" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_department"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_department" ADD FOREIGN KEY ("projectid") REFERENCES "public"."iyun_sm_project" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_login2logs"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_login2logs" ADD FOREIGN KEY ("userid") REFERENCES "public"."iyun_sm_user" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."iyun_sm_login2logs" ADD FOREIGN KEY ("logtypeid") REFERENCES "public"."iyun_sm_logtype" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_role"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_role" ADD FOREIGN KEY ("projectid") REFERENCES "public"."iyun_sm_project" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_role2res"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_role2res" ADD FOREIGN KEY ("resid") REFERENCES "public"."iyun_sm_resources" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."iyun_sm_role2res" ADD FOREIGN KEY ("roleid") REFERENCES "public"."iyun_sm_role" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_user"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_user" ADD FOREIGN KEY ("deptid") REFERENCES "public"."iyun_sm_department" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."iyun_sm_user" ADD FOREIGN KEY ("projectid") REFERENCES "public"."iyun_sm_project" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_user2group"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_user2group" ADD FOREIGN KEY ("userid") REFERENCES "public"."iyun_sm_user" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."iyun_sm_user2group" ADD FOREIGN KEY ("gid") REFERENCES "public"."iyun_sm_group" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."iyun_sm_user2role"
-- ----------------------------
ALTER TABLE "public"."iyun_sm_user2role" ADD FOREIGN KEY ("roleid") REFERENCES "public"."iyun_sm_role" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."iyun_sm_user2role" ADD FOREIGN KEY ("userid") REFERENCES "public"."iyun_sm_user" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."rtu_base_sla"
-- ----------------------------
ALTER TABLE "public"."rtu_base_sla" ADD FOREIGN KEY ("belongto") REFERENCES "public"."rtu_base_type" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."rtu_jobs_master"
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master" ADD FOREIGN KEY ("type") REFERENCES "public"."rtu_base_type" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."rtu_jobs_master" ADD FOREIGN KEY ("sla级别") REFERENCES "public"."rtu_base_sla" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."rtu_jobs_master2actions"
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master2actions" ADD FOREIGN KEY ("jobid") REFERENCES "public"."rtu_jobs_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."rtu_jobs_master2approvelog"
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master2approvelog" ADD FOREIGN KEY ("jobid") REFERENCES "public"."rtu_jobs_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ----------------------------
-- Foreign Key structure for table "public"."rtu_jobs_master2classes"
-- ----------------------------
ALTER TABLE "public"."rtu_jobs_master2classes" ADD FOREIGN KEY ("jobid") REFERENCES "public"."rtu_jobs_master" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE "public"."rtu_jobs_master2classes" ADD FOREIGN KEY ("cdetailid") REFERENCES "public"."iyun_base_prdclass2detail" ("id") ON DELETE RESTRICT ON UPDATE RESTRICT;

CREATE VIEW cmdb_asm_stack AS
    SELECT m.id AS mid, m.assetid, m.serial, m.assettype, (SELECT t.codeid FROM iyun_base_initcode t WHERE ((t.id)::text = (m.assettype)::text)) AS typecode, (SELECT t.codename FROM iyun_base_initcode t WHERE ((t.id)::text = (m.assettype)::text)) AS typename, m.depart, m.assetuser, m.status, m.iloip, m.mmac, (SELECT t.stackid FROM cmdb_switch_groups2items t WHERE ((t.masterid)::text = (m.id)::text)) AS stackid, (SELECT t.stackname FROM cmdb_switch_groups t WHERE ((t.id)::text = ((SELECT gi.stackid FROM cmdb_switch_groups2items gi WHERE ((gi.masterid)::text = (m.id)::text) LIMIT 1))::text)) AS stackname FROM cmdb_asm_master m WHERE ((m.assettype)::text = '8a8a700d57a30ea80157a30eccf70002'::text) UNION ALL SELECT m.id AS mid, m.assetid, m.serial, m.assettype, (SELECT t.codeid FROM iyun_base_initcode t WHERE ((t.id)::text = (m.assettype)::text)) AS typecode, (SELECT t.codename FROM iyun_base_initcode t WHERE ((t.id)::text = (m.assettype)::text)) AS typename, m.depart, m.assetuser, m.status, m.iloip, m.mmac, (SELECT t.stackid FROM cmdb_router_groups2items t WHERE ((t.masterid)::text = (m.id)::text)) AS stackid, (SELECT t.stackname FROM cmdb_router_groups t WHERE ((t.id)::text = ((SELECT gi.stackid FROM cmdb_router_groups2items gi WHERE ((gi.masterid)::text = (m.id)::text) LIMIT 1))::text)) AS stackname FROM cmdb_asm_master m WHERE ((m.assettype)::text = '8a8a700d57a30ea80157a30eccf60001'::text);

CREATE VIEW iyun_req_master_ongoing AS
    SELECT t.id, t.reqcode, t.step, t.responsible, t.projectname, t.issign, t.contract, t.amount, t.projectdesc, t.cusid, t.contact, t.iphone, t.email, t.status, t.chgflag, t.srcreqid, t.instanceid, t.groupid, t.createdby, t.createddate, t.updatedby, t.updateddate, t.workflowid, t.version, t.slaflag, t.priority, t.slalvl, wr.rolekey, wr.processsegment, wr.processname, wr.roleid, (SELECT u.deptid FROM iyun_sm_user u WHERE ((u.id)::text = (t.createdby)::text)) AS deptid FROM (bus_req_master t LEFT JOIN iyun_flow_workrole wr ON ((((wr.processsegment)::text = (t.step)::text) AND ((wr.workflowid)::text = (t.workflowid)::text)))) WHERE (length((t.step)::text) > 1);
