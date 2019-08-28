/**

时间：	    2017-08-15

说明： 	  修改自助监控字段长度

前置版本：	ICloudsV5.3.pdm

当前版本：	ICloudsV5.3.pdm
*/
ALTER TABLE public.ipm_pft_selfmonitor ALTER COLUMN ip TYPE VARCHAR(500);
DROP VIEW IF EXISTS trigger_event_view;
DROP VIEW IF EXISTS trigger_service_view;

CREATE OR REPLACE VIEW trigger_service_view AS
 SELECT
        CASE
            WHEN (((v1.servicetype)::text = 'host'::text) AND ((v1.mode)::text = 'single'::text)) THEN ( SELECT h.displayname
               FROM ipm_pft_hosts h
              WHERE ((h.hostid)::numeric = to_number((v1.serviceid)::text, '9999999999999999999'::text)))
            WHEN (((v1.servicetype)::text = 'http'::text) AND ((v1.mode)::text = 'single'::text)) THEN ( SELECT h.displayname
               FROM ipm_pft_httptest h
              WHERE ((h.httptestid)::numeric = to_number((v1.serviceid)::text, '9999999999999999999'::text)))
            WHEN ((v1.mode)::text = 'multi'::text) THEN '多资源触发器'::character varying
            ELSE ( SELECT h.name
               FROM ipm_pft_selfmonitor h
              WHERE ((h.id)::text = (v1.serviceid)::text))
        END AS servicename,
    v1.triggerid,
    v1.hostid,
    v1.itemid,
    v1.expression,
    v1.description,
    v1.status,
    v1.priority,
    v1.lastchange,
    v1.comments,
    v1.type,
    v1.templateid,
    v1.functionid,
    v1.serviceid,
    v1.mode,
    v1.servicetype,
    v1.updateddate,
        CASE
            WHEN (((v1.servicetype)::text = 'host'::text) AND ((v1.mode)::text = 'single'::text)) THEN ( SELECT h.tenantid
               FROM ipm_pft_hosts h
              WHERE ((h.hostid)::numeric = to_number((v1.serviceid)::text, '9999999999999999999'::text)))
            WHEN (((v1.servicetype)::text = 'http'::text) AND ((v1.mode)::text = 'single'::text)) THEN ( SELECT h.tenantid
               FROM ipm_pft_httptest h
              WHERE ((h.httptestid)::numeric = to_number((v1.serviceid)::text, '9999999999999999999'::text)))
            WHEN ((v1.mode)::text = 'multi'::text) THEN '多资源触发器'::character varying
            ELSE ( SELECT h.tenantid
               FROM ipm_pft_selfmonitor h
              WHERE ((h.id)::text = (v1.serviceid)::text))
        END AS tenantid
   FROM ( SELECT DISTINCT ON (mt.triggerid) mt.triggerid,
            mt.hostid,
            mt.itemid,
            mt.expression,
            mt.description,
            mt.status,
            mt.priority,
            mt.lastchange,
            mt.comments,
            mt.type,
            mt.templateid,
            mt.functionid,
            mt.serviceid,
            mt.mode,
            mt.servicetype,
            mt.updateddate
           FROM monitor_trigger_info mt
          WHERE ((mt.mode IS NOT NULL) AND (mt.hostid IS NOT NULL))) v1;

CREATE OR REPLACE VIEW trigger_event_view AS
 SELECT v1.eventid,
    v1.source,
    v1.object,
    v1.objectid,
    v1.clock,
    v1.value,
    v1.acknowledged,
    v1.ns,
    v1.servicename,
    v1.triggerid,
    v1.hostid,
    v1.itemid,
    v1.expression,
    v1.description,
    v1.status,
    v1.priority,
    v1.lastchange,
    v1.comments,
    v1.type,
    v1.templateid,
    v1.functionid,
    v1.serviceid,
    v1.mode,
    v1.servicetype,
    v1.updateddate,
    ( SELECT t.clock
           FROM ipm_pft_events t
          WHERE ((((t.source = 0) AND (t.object = 0)) AND (t.objectid = v1.triggerid)) AND (t.clock < v1.clock))
          ORDER BY t.clock DESC
         LIMIT 1) AS duration,
    v1.tenantid
   FROM ( SELECT e.eventid,
            e.source,
            e.object,
            e.objectid,
            e.clock,
            e.value,
            e.acknowledged,
            e.ns,
            mt.servicename,
            mt.triggerid,
            mt.hostid,
            mt.itemid,
            mt.expression,
            mt.description,
            mt.status,
            mt.priority,
            mt.lastchange,
            mt.comments,
            mt.type,
            mt.templateid,
            mt.functionid,
            mt.serviceid,
            mt.mode,
            mt.servicetype,
            mt.updateddate,
            mt.tenantid
           FROM (ipm_pft_events e
             LEFT JOIN trigger_service_view mt ON ((mt.triggerid = e.objectid)))
          WHERE (((e.source = 0) AND (e.object = 0)) AND (mt.triggerid IS NOT NULL))) v1;