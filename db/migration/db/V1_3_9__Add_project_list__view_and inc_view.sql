DROP VIEW IF EXISTS clusters_server2vm;
CREATE VIEW "public"."clusters_server2vm" AS
 SELECT ic.id,
    ic.hostid,
    ic.cpu,
    ic.memory,
    ic.storage,
    ic.custertid,
    clus.phostid
   FROM ( SELECT serv.id,
            serv.hostid,
            serv.cpu,
            serv.memory,
            serv.storage,
            item.custertid
           FROM cmdb_cloud_server2vm serv,
            cmdb_server_cluster2items item
          WHERE ((item.id)::text = (serv.hostid)::text)) ic,
    cmdb_server_clusters clus
  WHERE ((clus.id)::text = (ic.custertid)::text);

DROP VIEW IF EXISTS cmdb_asm_stack;
  CREATE VIEW "public"."cmdb_asm_stack" AS 
 SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
    ( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::text = (m.assettype)::text)) AS typecode,
    ( SELECT t.codename
           FROM iyun_base_initcode t
          WHERE ((t.id)::text = (m.assettype)::text)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
    ( SELECT t.stackid
           FROM cmdb_switch_groups2items t
          WHERE ((t.masterid)::text = (m.id)::text)) AS stackid,
    ( SELECT t.stackname
           FROM cmdb_switch_groups t
          WHERE ((t.id)::text = (( SELECT gi.stackid
                   FROM cmdb_switch_groups2items gi
                  WHERE ((gi.masterid)::text = (m.id)::text)
                 LIMIT 1))::text)) AS stackname
   FROM cmdb_asm_master m
  WHERE ((m.assettype)::text = '8a8a700d57a30ea80157a30eccf70002'::text)
UNION ALL
 SELECT m.id AS mid,
    m.assetid,
    m.serial,
    m.assettype,
    ( SELECT t.codeid
           FROM iyun_base_initcode t
          WHERE ((t.id)::text = (m.assettype)::text)) AS typecode,
    ( SELECT t.codename
           FROM iyun_base_initcode t
          WHERE ((t.id)::text = (m.assettype)::text)) AS typename,
    m.depart,
    m.assetuser,
    m.status,
    m.iloip,
    m.mmac,
    ( SELECT t.stackid
           FROM cmdb_router_groups2items t
          WHERE ((t.masterid)::text = (m.id)::text)) AS stackid,
    ( SELECT t.stackname
           FROM cmdb_router_groups t
          WHERE ((t.id)::text = (( SELECT gi.stackid
                   FROM cmdb_router_groups2items gi
                  WHERE ((gi.masterid)::text = (m.id)::text)
                 LIMIT 1))::text)) AS stackname
   FROM cmdb_asm_master m
  WHERE ((m.assettype)::text = '8a8a700d57a30ea80157a30eccf60001'::text);

DROP VIEW IF EXISTS device_ip_list_group;
  DROP VIEW IF EXISTS device_ip_info;
  CREATE VIEW "public"."device_ip_info" AS
 SELECT v.device_id,
    i.ip_address,
    s.cidr,
    s.name AS subnetname,
    n.externalnetworks
   FROM (((iyun_vdc_ports v
     LEFT JOIN iyun_vdc_ipallocations i ON (((i.port_id)::text = (v.id)::text)))
     LEFT JOIN iyun_vdc_subnet s ON (((i.subnet_id)::text = (s.id)::text)))
     LEFT JOIN iyun_vdc_network n ON (((n.id)::text = (s.network_id)::text)));
     
CREATE VIEW "public"."device_ip_list_group" AS
 SELECT view1.device_id,
    string_agg(concat(view1.ip_address,
        CASE
            WHEN (view1.externalnetworks = true) THEN 'T'::text
            ELSE 'F'::text
        END), ''::text) AS ips,
    string_agg((view1.subnetname)::text, ','::text) AS subnetnames,
    string_agg((view1.cidr)::text, ','::text) AS cidrs
   FROM device_ip_info view1
  GROUP BY view1.device_id;
  
  DROP VIEW IF EXISTS  iyun_inc_master_ongoing;
CREATE VIEW "public"."iyun_inc_master_ongoing" AS 
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
     LEFT JOIN iyun_flow_workrole wr ON ((((wr.processsegment)::text = (t.step)::text) AND ((wr.workflowid)::text = (t.workflowid)::text))))
  WHERE (length((t.step)::text) > 1);

  DROP VIEW IF EXISTS  iyun_req_master_ongoing;
 CREATE VIEW "public"."iyun_req_master_ongoing" AS 
 SELECT t.id,
    t.reqcode,
    t.step,
    t.responsible,
    t.projectname,
    t.issign,
    t.contract,
    t.amount,
    t.projectdesc,
    t.cusid,
    t.contact,
    t.iphone,
    t.email,
    t.status,
    t.chgflag,
    t.srcreqid,
    t.instanceid,
    t.groupid,
    t.createdby,
    t.createddate,
    t.updatedby,
    t.updateddate,
    t.workflowid,
    t.version,
    t.slaflag,
    t.priority,
    t.slalvl,
    wr.rolekey,
    wr.processsegment,
    wr.processname,
    wr.roleid,
    ( SELECT u.deptid
           FROM iyun_sm_user u
          WHERE ((u.id)::text = (t.createdby)::text)) AS deptid
   FROM (bus_req_master t
     LEFT JOIN iyun_flow_workrole wr ON ((((wr.processsegment)::text = (t.step)::text) AND ((wr.workflowid)::text = (t.workflowid)::text))))
  WHERE (length((t.step)::text) > 1);

    DROP VIEW IF EXISTS  project_list;
CREATE VIEW "public"."project_list" AS 
 SELECT p.id,
    p.name,
    p.createddate,
    u.username,
    r.roleid
   FROM ((iyun_keystone_project p
     LEFT JOIN iyun_sm_user u ON (((p.id)::text = (u.projectid)::text)))
     LEFT JOIN iyun_sm_user2role r ON (((r.userid)::text = (u.id)::text)))
  WHERE ((p.flag = 0) AND ((u.status)::text = '0'::text));
