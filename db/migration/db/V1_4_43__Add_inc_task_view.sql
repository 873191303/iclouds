CREATE OR REPLACE VIEW "public"."iyun_inc_master_task" AS 
 SELECT t.id,
    t.incno,
    t.topic,
    t.content,
    t.inctype,
    t.causedtime,
    t.responsible,
    t.step,
    t.company,
    t.customer,
    t.telphone,
    t.email,
    t.reporter,
    t.fromto,
    t.ways,
    t.reqftime,
    t.actftime,
    t.inclevel,
    t.rtuflag,
    t.instanceid,
    t.workflowid,
    t.attachment,
    t.createdby,
    t.createddate,
    t.updatedby,
    t.updateddate,
    ( SELECT u.deptid
           FROM iyun_sm_user u
          WHERE ((u.id)::text = (t.createdby)::text)) AS deptid,
    wr.rolekey,
    wr.processsegment,
    wr.processname,
    wr.roleid
   FROM (inc_req_master t
     LEFT JOIN iyun_flow_workrole wr ON ((((wr.processsegment)::text = (t.step)::text) AND ((wr.workflowid)::text = (t.workflowid)::text))));

