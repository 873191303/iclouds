/**

时间：	    2016年12月27日

说明： 	  	增加日志表

前置版本：	ICloudsV2.9.pdm

当前版本：	ICloudsV2.9.pdm
 */

CREATE TABLE "public"."iyun_sm_opelog" (
"id" varchar(32) NOT NULL,
"log_typeid" varchar(100) COLLATE "default" NOT NULL,
"ope_userid" varchar(36) COLLATE "default" NOT NULL,
"ope_username" varchar(100) COLLATE "default" NOT NULL,
"ope_loginname" varchar(100) COLLATE "default" NOT NULL,
"ope_starttime" timestamp(6) NOT NULL,
"ope_endtime" timestamp(6) NOT NULL,
"result" varchar(100) COLLATE "default" NOT NULL,
"ope_url" varchar(200) COLLATE "default" NOT NULL,
"ope_params" varchar(500) COLLATE "default" NOT NULL,
"ope_ip" varchar(32) COLLATE "default" NOT NULL,
"remark" text,
CONSTRAINT "iyun_sm_opelog_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE);