/**

时间：	    2017-07-31

说明： 	  修改触发器视图

前置版本：	ICloudsV5.2.pdm

当前版本：	ICloudsV5.2.pdm
*/
DROP VIEW IF EXISTS monitor_trigger_info;
CREATE OR REPLACE VIEW monitor_trigger_info AS
   SELECT t3.triggerid,
    t3.hostid,
    t3.itemid,
    t3.expression,
    t3.description,
    t3.status,
    t3.priority,
    t3.lastchange,
    t3.comments,
    t3.type,
    t3.templateid,
    t3.functionid,
    t3.serviceid,
    t3.mode
   FROM ( SELECT t2.hostid,
            t2.itemid,
            t2.triggerid,
            t2.expression,
            t2.description,
            t2.status,
            t2.priority,
            t2.lastchange,
            t2.templateid,
            t2.comments,
            t2.functionid,
            t2.type,
            t2.serviceid,
            t2.mode
           FROM (( SELECT i.hostid,
                    t1.itemid,
                    t1.triggerid,
                    t1.templateid,
                    t1.expression,
                    t1.description,
                    t1.status,
                    t1.priority,
                    t1.lastchange,
                    t1.comments,
                    t1.functionid,
                    t1.type,
                    i.serviceid,
                    t1.mode
                   FROM (( SELECT f.itemid,
                            t.triggerid,
                            t.templateid,
                            t.expression,
                            t.description,
                            t.status,
                            t.priority,
                            t.lastchange,
                            t.comments,
                            t.type,
                            f.functionid,
                            t.mode
                           FROM (ipm_pft_triggers t
                             LEFT JOIN ipm_pft_functions f ON ((f.triggerid = t.triggerid)))) t1
                     LEFT JOIN ipm_pft_items i ON ((i.itemid = t1.itemid)))) t2
             LEFT JOIN ipm_pft_hosts h ON ((h.hostid = t2.hostid)))) t3
  ORDER BY t3.triggerid DESC;

ALTER TABLE public.ipm_pft_items ADD servicetype VARCHAR(100) NULL;

-- ----------------------------
-- Table structure for ipm_pft_events
-- ----------------------------
DROP TABLE IF EXISTS ipm_pft_events;
/*==============================================================*/
/* Table: ipm_pft_events                                        */
/*==============================================================*/
create table ipm_pft_events (
   eventid              int8                 not null,
   source               int4                 not null default 0,
   object               int4                 not null default 0,
   objectid             int8                 not null default 0,
   clock                int4                 not null default 0,
   value                int4                 not null default 0,
   acknowledged         int4                 not null default 0,
   ns                   int4                 not null default 0,
   constraint PK_IPM_PFT_EVENTS primary key (eventid)
);