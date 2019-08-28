/**

时间：	    2017年7月4日

说明： 	  监控模块表格修改

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm

内容:     1、	去除 ipm_pft_alerts、ipm_pft_events，以后此业务数据直接来源zabbix表了；
          2、	Action表增加“关联触发器id”
          3、	增加了主机接口定义表ipm_pft_interface
          4、	增加了表：ipm_pft_opmessage2grp、ipm_pft_opmessage2usr  消息发送用户、消息发送用户组，增加对应的sequence
          5、	修正了两个字段错误：ipm_pft_actions
          6、 增加触发器与主机，监控项视图
*/

ALTER TABLE ipm_pft_acknowledges DROP CONSTRAINT fk_ipm_pft__reference_ipm_pft_1;

DROP TABLE IF EXISTS ipm_pft_alerts;

DROP TABLE IF EXISTS ipm_pft_events;

ALTER TABLE public.ipm_pft_actions ADD triggerid int8 NULL;

drop table IF EXISTS ipm_pft_opmessage2usr;

/*==============================================================*/
/* Table: ipm_pft_opmessage2usr                                 */
/*==============================================================*/
CREATE TABLE ipm_pft_opmessage2usr (
   opmessage_usrid      int8                 not null,
   operationid          int8                 not null,
   userid               int8                 null,
   constraint opmessage_usr_pkey primary key (opmessage_usrid)
);

ALTER TABLE ipm_pft_opmessage2usr
   add constraint FK_IPM_PFT__REFERENCE_IPM_PFT_OM1 foreign key (operationid)
      references ipm_pft_operations (operationid)
      on delete restrict on update restrict;

ALTER TABLE ipm_pft_opmessage2usr
   add constraint c_opmessage_usr_2_om2 foreign key (userid)
      references ipm_pft_users (userid)
      on delete restrict on update restrict;


DROP TABLE IF EXISTS ipm_pft_opmessage2grp;

/*==============================================================*/
/* Table: ipm_pft_opmessage2grp                                 */
/*==============================================================*/
CREATE TABLE ipm_pft_opmessage2grp (
   opmessage_grpid      int8                 not null,
   operationid          int8                 not null,
   usrgrpid             int8                 not null,
   constraint opmessage_grp_pkey primary key (opmessage_grpid)
);

ALTER TABLE ipm_pft_opmessage2grp
   add constraint FK_IPM_PFT__REFERENCE_IPM_PFT_GM1 foreign key (operationid)
      references ipm_pft_operations (operationid)
      on delete restrict on update restrict;

ALTER TABLE ipm_pft_opmessage2grp
   add constraint FK_IPM_PFT__REFERENCE_IPM_PFT_GM2 foreign key (usrgrpid)
      references ipm_pft_usrgrp (usrgrpid)
      on delete restrict on update restrict;

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
    t3.templateid
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
            t2.type
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
                    t1.type
                   FROM (( SELECT f.itemid,
                            t.triggerid,
                            t.templateid,
                            t.expression,
                            t.description,
                            t.status,
                            t.priority,
                            t.lastchange,
                            t.comments,
                            t.type
                           FROM (ipm_pft_triggers t
                             LEFT JOIN ipm_pft_functions f ON ((f.triggerid = t.triggerid)))) t1
                     LEFT JOIN ipm_pft_items i ON ((i.itemid = t1.itemid)))) t2
             LEFT JOIN ipm_pft_hosts h ON ((h.hostid = t2.hostid)))) t3
  ORDER BY t3.triggerid DESC;
