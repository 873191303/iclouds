/**

时间：	    2017年7月10日

说明： 	  修改监控项分类表

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/
CREATE SEQUENCE PUBLIC.ipm_pft_item2applications_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;

ALTER TABLE PUBLIC.ipm_pft_item2applications
  ALTER COLUMN ID
  SET DEFAULT nextval(
    'public.ipm_pft_item2applications_id_seq'
);

ALTER SEQUENCE PUBLIC.ipm_pft_item2applications_id_seq OWNED BY PUBLIC.ipm_pft_item2applications.ID;

ALTER TABLE public.ipm_pft_applications
  ADD CONSTRAINT ipm_pft_applications_ipm_pft_hosts_hostid_fk
FOREIGN KEY (hostid) REFERENCES ipm_pft_hosts (hostid);
ALTER TABLE public.ipm_pft_applications
  ADD templateid INT8 NULL;
ALTER TABLE public.ipm_pft_applications
  ADD CONSTRAINT ipm_pft_applications_ipm_pft_templates_templateid_fk
FOREIGN KEY (templateid) REFERENCES ipm_pft_templates (templateid);

DROP TABLE public.ipm_pft_template2application;

DROP VIEW IF EXISTS monitor_trigger_info;
CREATE OR REPLACE VIEW monitor_trigger_info AS
  SELECT
    t3.triggerid,
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
    t3.functionid
  FROM (SELECT
          t2.hostid,
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
          t2.type
        FROM ((SELECT
                 i.hostid,
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
                 t1.type
               FROM ((SELECT
                        f.itemid,
                        t.triggerid,
                        t.templateid,
                        t.expression,
                        t.description,
                        t.status,
                        t.priority,
                        t.lastchange,
                        t.comments,
                        t.type,
                        f.functionid
                      FROM (ipm_pft_triggers t
                        LEFT JOIN ipm_pft_functions f ON ((f.triggerid = t.triggerid)))) t1
                 LEFT JOIN ipm_pft_items i ON ((i.itemid = t1.itemid)))) t2
          LEFT JOIN ipm_pft_hosts h ON ((h.hostid = t2.hostid)))) t3
  ORDER BY t3.triggerid DESC;

