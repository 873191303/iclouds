/**

时间：	    2017年6月29日

说明： 	  新增监控模块表格

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/

DROP TABLE IF EXISTS isso_auth_operatelogs;

DROP TABLE IF EXISTS isso_auth_logtype;

DROP TABLE IF EXISTS isso_sm_user2role;

DROP TABLE IF EXISTS isso_sm_role2res;

DROP TABLE IF EXISTS isso_auth_user2system;

DROP TABLE IF EXISTS isso_sm_role;

DROP TABLE IF EXISTS isso_sm_resources;

DROP TABLE IF EXISTS isso_auth_user;

DROP TABLE IF EXISTS isso_auth_subsystem;

DROP TABLE IF EXISTS ipm_pft_acknowledges;

DROP TABLE IF EXISTS ipm_pft_actions;

DROP TABLE IF EXISTS ipm_pft_alerts;

DROP TABLE IF EXISTS ipm_pft_applications;

DROP TABLE IF EXISTS ipm_pft_conditions;

DROP TABLE IF EXISTS ipm_pft_dchecks;

DROP TABLE IF EXISTS ipm_pft_dhosts;

DROP TABLE IF EXISTS ipm_pft_drules;

DROP TABLE IF EXISTS ipm_pft_dservices;

DROP TABLE IF EXISTS ipm_pft_events;

DROP TABLE IF EXISTS ipm_pft_functions;

DROP TABLE IF EXISTS ipm_pft_groups;

DROP TABLE IF EXISTS ipm_pft_host2group;

DROP TABLE IF EXISTS ipm_pft_host2template;

DROP TABLE IF EXISTS ipm_pft_hosts;

DROP INDEX IF EXISTS httpstep_2;

DROP TABLE IF EXISTS ipm_pft_httpstep;

DROP INDEX IF EXISTS httpstepitem_2;

DROP INDEX IF EXISTS httpstepitem_1;

DROP TABLE IF EXISTS ipm_pft_httpstepitem;

DROP TABLE IF EXISTS ipm_pft_httptest;

DROP TABLE IF EXISTS ipm_pft_httptest2item;

DROP TABLE IF EXISTS ipm_pft_item2applications;

DROP INDEX IF EXISTS item_condition_1;

DROP TABLE IF EXISTS ipm_pft_item2condition;

DROP TABLE IF EXISTS ipm_pft_items;

DROP TABLE IF EXISTS ipm_pft_media;

DROP TABLE IF EXISTS ipm_pft_mediaType;

DROP TABLE IF EXISTS ipm_pft_opcommand;

DROP TABLE IF EXISTS ipm_pft_operations;

DROP TABLE IF EXISTS ipm_pft_opgroup;

DROP TABLE IF EXISTS ipm_pft_opmessage;

DROP TABLE IF EXISTS ipm_pft_rights;

DROP TABLE IF EXISTS ipm_pft_scripts;

DROP TABLE IF EXISTS ipm_pft_template2application;

DROP TABLE IF EXISTS ipm_pft_templates;

DROP TABLE IF EXISTS ipm_pft_trigger2discovery;

DROP TABLE IF EXISTS ipm_pft_triggers;

DROP TABLE IF EXISTS ipm_pft_users;

DROP TABLE IF EXISTS ipm_pft_users2groups;

DROP TABLE IF EXISTS ipm_pft_usrgrp;

/*==============================================================*/
/* Table: ipm_pft_acknowledges                                  */
/*==============================================================*/
CREATE TABLE ipm_pft_acknowledges (
  acknowledgeid INT8         NOT NULL,
  userid        INT8         NOT NULL,
  eventid       INT8         NOT NULL,
  clock         INT4         NOT NULL DEFAULT 0,
  message       VARCHAR(255) NOT NULL DEFAULT '',
  CONSTRAINT PK_IPM_PFT_ACKNOWLEDGES PRIMARY KEY (acknowledgeid)
);

/*==============================================================*/
/* Table: ipm_pft_actions                                       */
/*==============================================================*/
CREATE TABLE ipm_pft_actions (
  actionid      INT8         NOT NULL,
  name          VARCHAR(255) NOT NULL DEFAULT '',
  eventsource   INT4         NOT NULL DEFAULT 0,
  evaltype      INT4         NOT NULL DEFAULT 0,
  status        INT4         NOT NULL DEFAULT 0,
  esc_period    INT4         NOT NULL DEFAULT 0,
  def_shortdata VARCHAR(255) NOT NULL DEFAULT '',
  def_longdata  TEXT         NOT NULL DEFAULT '',
  recovery_msg  INT4         NOT NULL DEFAULT 0,
  r_shortdata   VARCHAR(255) NOT NULL DEFAULT '',
  r_longdata    TEXT         NOT NULL DEFAULT '',
  formula       VARCHAR(255) NOT NULL DEFAULT '',
  tenantid      VARCHAR(50)  NULL,
  CreatedBy     VARCHAR(36)  NOT NULL,
  CreatedDate   TIMESTAMP    NOT NULL,
  UpdatedBy     VARCHAR(36)  NOT NULL,
  UpdatedDate   TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_ACTIONS PRIMARY KEY (actionid)
);

/*==============================================================*/
/* Table: ipm_pft_alerts                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_alerts (
  alertid     INT8         NOT NULL,
  actionid    INT8         NOT NULL,
  eventid     INT8         NOT NULL,
  userid      INT8         NULL,
  clock       INT4         NOT NULL DEFAULT 0,
  mediatypeid INT8         NULL,
  sendto      VARCHAR(100) NOT NULL DEFAULT '',
  subject     VARCHAR(255) NOT NULL DEFAULT '',
  message     TEXT         NOT NULL DEFAULT '',
  status      INT4         NOT NULL DEFAULT 0,
  retries     INT4         NOT NULL DEFAULT 0,
  error       VARCHAR(128) NOT NULL DEFAULT '',
  esc_step    INT4         NOT NULL DEFAULT 0,
  alerttype   INT4         NOT NULL DEFAULT 0,
  CONSTRAINT PK_IPM_PFT_ALERTS PRIMARY KEY (alertid)
);

/*==============================================================*/
/* Table: ipm_pft_applications                                  */
/*==============================================================*/
CREATE TABLE ipm_pft_applications (
  id          INT8         NOT NULL,
  hostid      INT8         NULL,
  name        VARCHAR(255) NOT NULL,
  displayname VARCHAR(255) NOT NULL,
  flags       INT4         NULL,
  uuid        VARCHAR(36)  NULL,
  tenantid    VARCHAR(50)  NOT NULL,
  owner       VARCHAR(36)  NOT NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_APPLICATIONS PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_conditions                                    */
/*==============================================================*/
CREATE TABLE ipm_pft_conditions (
  conditionid   INT8         NOT NULL,
  actionid      INT8         NOT NULL,
  conditiontype INT4         NOT NULL DEFAULT 0,
  operator      INT4         NOT NULL DEFAULT 0,
  value         VARCHAR(255) NOT NULL DEFAULT '',
  CONSTRAINT PK_IPM_PFT_CONDITIONS PRIMARY KEY (conditionid)
);

/*==============================================================*/
/* Table: ipm_pft_dchecks                                       */
/*==============================================================*/
CREATE TABLE ipm_pft_dchecks (
  dcheckid              INT8         NOT NULL,
  druleid               INT8         NULL,
  dhostid               INT8         NULL,
  type                  INT4         NULL,
  key_                  VARCHAR(255) NULL,
  snmp_community        VARCHAR(255) NULL,
  ports                 VARCHAR(255) NULL,
  snmpv3_securityname   VARCHAR(64)  NULL,
  snmpv3_securitylevel  INT8         NULL,
  snmpv3_authpassphrase VARCHAR(64)  NULL,
  snmpv3_privpassphrase VARCHAR(64)  NULL,
  uniq                  INT4         NULL,
  snmpv3_authprotocol   INT4         NULL,
  snmpv3_privprotocol   INT4         NULL,
  snmpv3_contextname    VARCHAR(255) NULL,
  CONSTRAINT PK_IPM_PFT_DCHECKS PRIMARY KEY (dcheckid)
);

/*==============================================================*/
/* Table: ipm_pft_dhosts                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_dhosts (
  dhostid     INT8        NOT NULL,
  druleid     INT8        NULL,
  status      INT4        NULL,
  lastup      INT4        NULL,
  lastdown    INT4        NULL,
  tenantid    VARCHAR(50) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_DHOSTS PRIMARY KEY (dhostid)
);

/*==============================================================*/
/* Table: ipm_pft_drules                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_drules (
  druleid      INT8         NOT NULL,
  proxy_hostid INT8         NULL,
  name         VARCHAR(255) NULL,
  iprange      VARCHAR(255) NULL,
  delay        INT4         NULL,
  nextcheck    INT4         NULL,
  status       INT4         NULL,
  tenantid     VARCHAR(50)  NULL,
  CreatedBy    VARCHAR(36)  NOT NULL,
  CreatedDate  TIMESTAMP    NOT NULL,
  UpdatedBy    VARCHAR(36)  NOT NULL,
  UpdatedDate  TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_DRULES PRIMARY KEY (druleid)
);

/*==============================================================*/
/* Table: ipm_pft_dservices                                     */
/*==============================================================*/
CREATE TABLE ipm_pft_dservices (
  dserviceid INT8         NOT NULL,
  hostid     INT8         NULL,
  type       INT4         NULL,
  key_       VARCHAR(255) NULL,
  value      VARCHAR(255) NULL,
  port       INT4         NULL,
  status     INT4         NULL,
  lastup     INT4         NULL,
  lastdown   INT4         NULL,
  CONSTRAINT PK_IPM_PFT_DSERVICES PRIMARY KEY (dserviceid)
);

/*==============================================================*/
/* Table: ipm_pft_events                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_events (
  eventid      INT8 NOT NULL,
  source       INT4 NOT NULL DEFAULT 0,
  object       INT4 NOT NULL DEFAULT 0,
  objectid     INT8 NOT NULL DEFAULT 0,
  clock        INT4 NOT NULL DEFAULT 0,
  value        INT4 NOT NULL DEFAULT 0,
  acknowledged INT4 NOT NULL DEFAULT 0,
  ns           INT4 NOT NULL DEFAULT 0,
  CONSTRAINT PK_IPM_PFT_EVENTS PRIMARY KEY (eventid)
);

/*==============================================================*/
/* Table: ipm_pft_functions                                     */
/*==============================================================*/
CREATE TABLE ipm_pft_functions (
  functionid INT8         NOT NULL,
  itemid     INT8         NOT NULL,
  triggerid  INT8         NOT NULL,
  function   VARCHAR(12)  NOT NULL DEFAULT '',
  parameter  VARCHAR(255) NOT NULL DEFAULT '0',
  CONSTRAINT PK_IPM_PFT_FUNCTIONS PRIMARY KEY (functionid)
);

/*==============================================================*/
/* Table: ipm_pft_groups                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_groups (
  groupid     INT8        NOT NULL,
  name        VARCHAR(64) NULL,
  displayname VARCHAR(64) NULL,
  internal    INT4        NULL,
  flags       INT4        NULL,
  tenantid    VARCHAR(50) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_GROUPS PRIMARY KEY (groupid)
);

/*==============================================================*/
/* Table: ipm_pft_host2group                                    */
/*==============================================================*/
CREATE TABLE ipm_pft_host2group (
  hostgroupid INT8        NOT NULL,
  groupid     INT8        NOT NULL,
  hostid      INT8        NOT NULL,
  tenantid    VARCHAR(50) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_HOST2GROUP PRIMARY KEY (hostgroupid)
);

/*==============================================================*/
/* Table: ipm_pft_host2template                                 */
/*==============================================================*/
CREATE TABLE ipm_pft_host2template (
  id          INT8        NOT NULL,
  hostid      INT8        NOT NULL,
  templateid  INT8        NOT NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_HOST2TEMPLATE PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_hosts                                         */
/*==============================================================*/
CREATE TABLE ipm_pft_hosts (
  hostid       INT8         NOT NULL,
  proxy_hostid INT8         NULL,
  zhost        VARCHAR(128) NULL,
  ihost        VARCHAR(128) NOT NULL,
  displayname  VARCHAR(255) NOT NULL,
  UUID         VARCHAR(36)  NOT NULL,
  hosttype     VARCHAR(8)   NOT NULL,
  Macs         TEXT         NULL,
  tenantid     VARCHAR(50)  NOT NULL,
  owner        VARCHAR(36)  NOT NULL,
  CreatedBy    VARCHAR(36)  NOT NULL,
  CreatedDate  TIMESTAMP    NOT NULL,
  UpdatedBy    VARCHAR(36)  NOT NULL,
  UpdatedDate  TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_HOSTS PRIMARY KEY (hostid)
);

/*==============================================================*/
/* Table: ipm_pft_httpstep                                      */
/*==============================================================*/
CREATE TABLE ipm_pft_httpstep (
  httpstepid       INT8          NOT NULL,
  httptestid       INT8          NOT NULL,
  name             VARCHAR(64)   NOT NULL DEFAULT '',
  no               INT4          NOT NULL DEFAULT 0,
  url              VARCHAR(2048) NOT NULL DEFAULT '',
  timeout          INT4          NOT NULL DEFAULT 15,
  posts            TEXT          NOT NULL DEFAULT '',
  required         VARCHAR(255)  NOT NULL DEFAULT '',
  status_codes     VARCHAR(255)  NOT NULL DEFAULT '',
  variables        TEXT          NOT NULL DEFAULT '',
  follow_redirects INT4          NOT NULL DEFAULT 1,
  retrieve_mode    INT4          NOT NULL DEFAULT 0,
  headers          TEXT          NOT NULL DEFAULT '',
  tenantid         VARCHAR(50)   NULL,
  owner            VARCHAR(36)   NULL,
  CreatedBy        VARCHAR(36)   NOT NULL,
  CreatedDate      TIMESTAMP     NOT NULL,
  UpdatedBy        VARCHAR(36)   NOT NULL,
  UpdatedDate      TIMESTAMP     NOT NULL,
  CONSTRAINT httpstep_pkey PRIMARY KEY (httpstepid)
);

/*==============================================================*/
/* Index: httpstep_2                                            */
/*==============================================================*/
CREATE INDEX httpstep_2
  ON ipm_pft_httpstep (
    "httptestid" ASC
  );

/*==============================================================*/
/* Table: ipm_pft_httpstepitem                                  */
/*==============================================================*/
CREATE TABLE ipm_pft_httpstepitem (
  httpstepitemid INT8 NOT NULL,
  httpstepid     INT8 NULL,
  itemid         INT8 NOT NULL,
  type           INT4 NOT NULL DEFAULT 0,
  CONSTRAINT httpstepitem_pkey PRIMARY KEY (httpstepitemid)
);

/*==============================================================*/
/* Index: httpstepitem_1                                        */
/*==============================================================*/
CREATE UNIQUE INDEX httpstepitem_1
  ON ipm_pft_httpstepitem (
    "httpstepid" ASC,
    "itemid" ASC
  );

/*==============================================================*/
/* Index: httpstepitem_2                                        */
/*==============================================================*/
CREATE INDEX httpstepitem_2
  ON ipm_pft_httpstepitem (
    "itemid" ASC
  );

/*==============================================================*/
/* Table: ipm_pft_httptest                                      */
/*==============================================================*/
CREATE TABLE ipm_pft_httptest (
  id          INT8        NOT NULL,
  name        VARCHAR(64) NOT NULL,
  displayname VARCHAR(64) NOT NULL,
  tenantid    VARCHAR(50) NULL,
  owner       VARCHAR(36) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_HTTPTEST PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_httptest2item                                 */
/*==============================================================*/
CREATE TABLE ipm_pft_httptest2item (
  id          INT8        NOT NULL,
  httptestid  INT8        NOT NULL,
  itemid      INT8        NOT NULL,
  tenantid    VARCHAR(50) NULL,
  owner       VARCHAR(36) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_HTTPTEST2ITEM PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_item2applications                             */
/*==============================================================*/
CREATE TABLE ipm_pft_item2applications (
  id            INT8        NOT NULL,
  applicationid INT8        NOT NULL,
  itemid        INT8        NOT NULL,
  CreatedBy     VARCHAR(36) NOT NULL,
  CreatedDate   TIMESTAMP   NOT NULL,
  UpdatedBy     VARCHAR(36) NOT NULL,
  UpdatedDate   TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_ITEM2APPLICATIONS PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_item2condition                                */
/*==============================================================*/
CREATE TABLE ipm_pft_item2condition (
  item_conditionid INT8         NOT NULL,
  itemid           INT8         NOT NULL,
  operator         INT4         NOT NULL DEFAULT 8,
  macro            VARCHAR(64)  NOT NULL DEFAULT '',
  value            VARCHAR(255) NOT NULL DEFAULT '',
  CONSTRAINT item_condition_pkey PRIMARY KEY (item_conditionid)
);

/*==============================================================*/
/* Index: item_condition_1                                      */
/*==============================================================*/
CREATE INDEX item_condition_1
  ON ipm_pft_item2condition (
    "itemid" ASC
  );

/*==============================================================*/
/* Table: ipm_pft_items                                         */
/*==============================================================*/
CREATE TABLE ipm_pft_items (
  itemid      INT8         NOT NULL,
  type        VARCHAR(4)   NOT NULL,
  snmp_oid    VARCHAR(255) NOT NULL,
  hostid      INT8         NOT NULL,
  name        VARCHAR(255) NOT NULL,
  displayname VARCHAR(255) NOT NULL,
  key_        VARCHAR(255) NOT NULL,
  value_type  VARCHAR(36)  NOT NULL,
  templateid  INT8         NOT NULL,
  status      VARCHAR(36)  NOT NULL,
  tenantid    VARCHAR(50)  NULL,
  owner       VARCHAR(36)  NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_ITEMS PRIMARY KEY (itemid)
);

/*==============================================================*/
/* Table: ipm_pft_media                                         */
/*==============================================================*/
CREATE TABLE ipm_pft_media (
  mediaid     INT8         NOT NULL,
  userid      INT8         NOT NULL,
  mediatypeid INT8         NOT NULL,
  sendto      VARCHAR(100) NOT NULL DEFAULT '',
  active      INT4         NOT NULL DEFAULT 0,
  severity    INT4         NOT NULL DEFAULT 63,
  period      VARCHAR(100) NOT NULL DEFAULT '1-7,00:00-24:00',
  CONSTRAINT PK_IPM_PFT_MEDIA PRIMARY KEY (mediaid)
);

/*==============================================================*/
/* Table: ipm_pft_mediaType                                     */
/*==============================================================*/
CREATE TABLE ipm_pft_mediaType (
  mediatypeid         INT8         NOT NULL,
  type                INT4         NOT NULL DEFAULT 0,
  description         VARCHAR(100) NOT NULL DEFAULT '',
  smtp_server         VARCHAR(255) NOT NULL DEFAULT '',
  smtp_helo           VARCHAR(255) NOT NULL DEFAULT '',
  smtp_email          VARCHAR(255) NOT NULL DEFAULT '',
  exec_path           VARCHAR(255) NOT NULL DEFAULT '',
  gsm_modem           VARCHAR(255) NOT NULL DEFAULT '',
  username            VARCHAR(255) NOT NULL DEFAULT '',
  passwd              VARCHAR(255) NOT NULL DEFAULT '',
  status              INT4         NOT NULL DEFAULT 0,
  smtp_port           INT4         NOT NULL DEFAULT 25,
  smtp_security       INT4         NOT NULL DEFAULT 0,
  smtp_verify_peer    INT4         NOT NULL DEFAULT 0,
  smtp_verify_host    INT4         NOT NULL DEFAULT 0,
  smtp_authentication INT4         NOT NULL DEFAULT 0,
  exec_params         VARCHAR(255) NOT NULL DEFAULT '',
  CONSTRAINT PK_IPM_PFT_MEDIATYPE PRIMARY KEY (mediatypeid)
);

/*==============================================================*/
/* Table: ipm_pft_opcommand                                     */
/*==============================================================*/
CREATE TABLE ipm_pft_opcommand (
  operationid INT8        NOT NULL,
  type        INT4        NOT NULL DEFAULT 0,
  scriptid    INT8        NULL,
  execute_on  INT4        NOT NULL DEFAULT 0,
  port        VARCHAR(64) NOT NULL DEFAULT '',
  authtype    INT4        NOT NULL DEFAULT 0,
  username    VARCHAR(64) NOT NULL DEFAULT '',
  password    VARCHAR(64) NOT NULL DEFAULT '',
  publickey   VARCHAR(64) NOT NULL DEFAULT '',
  privatekey  VARCHAR(64) NOT NULL DEFAULT '',
  command     TEXT        NOT NULL DEFAULT '',
  CONSTRAINT PK_IPM_PFT_OPCOMMAND PRIMARY KEY (operationid)
);

/*==============================================================*/
/* Table: ipm_pft_operations                                    */
/*==============================================================*/
CREATE TABLE ipm_pft_operations (
  operationid   INT8        NOT NULL,
  actionid      INT8        NOT NULL,
  operationtype INT4        NOT NULL DEFAULT 0,
  esc_period    INT4        NOT NULL DEFAULT 0,
  esc_step_from INT4        NOT NULL DEFAULT 1,
  esc_step_to   INT4        NOT NULL DEFAULT 1,
  evaltype      INT4        NOT NULL DEFAULT 0,
  tenantid      VARCHAR(50) NULL,
  CreatedBy     VARCHAR(36) NOT NULL,
  CreatedDate   TIMESTAMP   NOT NULL,
  UpdatedBy     VARCHAR(36) NOT NULL,
  UpdatedDate   TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_OPERATIONS PRIMARY KEY (operationid)
);

/*==============================================================*/
/* Table: ipm_pft_opgroup                                       */
/*==============================================================*/
CREATE TABLE ipm_pft_opgroup (
  opgroupid   INT8 NOT NULL,
  operationid INT8 NOT NULL,
  groupid     INT8 NOT NULL,
  CONSTRAINT PK_IPM_PFT_OPGROUP PRIMARY KEY (opgroupid)
);

/*==============================================================*/
/* Table: ipm_pft_opmessage                                     */
/*==============================================================*/
CREATE TABLE ipm_pft_opmessage (
  operationid INT8         NOT NULL,
  default_msg INT4         NOT NULL DEFAULT 0,
  subject     VARCHAR(255) NOT NULL DEFAULT '',
  message     TEXT         NOT NULL DEFAULT '',
  mediatypeid INT8         NULL,
  CONSTRAINT PK_IPM_PFT_OPMESSAGE PRIMARY KEY (operationid)
);

/*==============================================================*/
/* Table: ipm_pft_rights                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_rights (
  rightid     INT8        NOT NULL,
  groupid     INT8        NOT NULL,
  permission  INT4        NOT NULL DEFAULT 0,
  usrgrpid    INT8        NOT NULL,
  tenantid    VARCHAR(50) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_RIGHTS PRIMARY KEY (rightid)
);

/*==============================================================*/
/* Table: ipm_pft_scripts                                       */
/*==============================================================*/
CREATE TABLE ipm_pft_scripts (
  scriptid     INT8         NOT NULL,
  userid       INT8         NULL,
  name         VARCHAR(255) NOT NULL DEFAULT '',
  command      VARCHAR(255) NOT NULL DEFAULT '',
  host_access  INT4         NOT NULL DEFAULT 2,
  usrgrpid     INT8         NULL,
  groupid      INT8         NULL,
  description  TEXT         NOT NULL DEFAULT '',
  confirmation VARCHAR(255) NOT NULL DEFAULT '',
  type         INT4         NOT NULL DEFAULT 0,
  execute_on   INT4         NOT NULL DEFAULT 1,
  tenantid     VARCHAR(50)  NULL,
  CreatedBy    VARCHAR(36)  NOT NULL,
  CreatedDate  TIMESTAMP    NOT NULL,
  UpdatedBy    VARCHAR(36)  NOT NULL,
  UpdatedDate  TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_SCRIPTS PRIMARY KEY (scriptid)
);

/*==============================================================*/
/* Table: ipm_pft_template2application                          */
/*==============================================================*/
CREATE TABLE ipm_pft_template2application (
  id            INT8        NOT NULL,
  applicationid INT8        NULL,
  templateid    INT8        NULL,
  CreatedBy     VARCHAR(36) NOT NULL,
  CreatedDate   TIMESTAMP   NOT NULL,
  UpdatedBy     VARCHAR(36) NOT NULL,
  UpdatedDate   TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_TEMPLATE2APPLICATIO PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_templates                                     */
/*==============================================================*/
CREATE TABLE ipm_pft_templates (
  templateid   INT8         NOT NULL,
  templateName VARCHAR(255) NOT NULL,
  displayname  VARCHAR(255) NOT NULL,
  templateType VARCHAR(36)  NOT NULL,
  tenantid     VARCHAR(50)  NULL,
  owner        VARCHAR(36)  NULL,
  CreatedBy    VARCHAR(36)  NOT NULL,
  CreatedDate  TIMESTAMP    NOT NULL,
  UpdatedBy    VARCHAR(36)  NOT NULL,
  UpdatedDate  TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IPM_PFT_TEMPLATES PRIMARY KEY (templateid)
);

/*==============================================================*/
/* Table: ipm_pft_trigger2discovery                             */
/*==============================================================*/
CREATE TABLE ipm_pft_trigger2discovery (
  triggerid        INT8 NOT NULL,
  parent_triggerid INT8 NOT NULL,
  CONSTRAINT PK_IPM_PFT_TRIGGER2DISCOVERY PRIMARY KEY (triggerid)
);

/*==============================================================*/
/* Table: ipm_pft_triggers                                      */
/*==============================================================*/
CREATE TABLE ipm_pft_triggers (
  triggerid   INT8          NOT NULL,
  expression  VARCHAR(2048) NOT NULL DEFAULT '',
  description VARCHAR(255)  NOT NULL DEFAULT '',
  url         VARCHAR(255)  NOT NULL DEFAULT '',
  status      INT4          NOT NULL DEFAULT 0,
  value       INT4          NOT NULL DEFAULT 0,
  priority    INT4          NOT NULL DEFAULT 0,
  lastchange  INT4          NOT NULL DEFAULT 0,
  comments    TEXT          NOT NULL DEFAULT '',
  error       VARCHAR(128)  NOT NULL DEFAULT '',
  templateid  INT8          NULL,
  type        INT4          NOT NULL DEFAULT 0,
  state       INT4          NOT NULL DEFAULT 0,
  flags       INT4          NOT NULL DEFAULT 0,
  source_     VARCHAR(100)  NULL,
  sourcetype_ VARCHAR(100)  NULL,
  tenantid    VARCHAR(50)   NULL,
  CreatedBy   VARCHAR(36)   NOT NULL,
  CreatedDate TIMESTAMP     NOT NULL,
  UpdatedBy   VARCHAR(36)   NOT NULL,
  UpdatedDate TIMESTAMP     NOT NULL,
  CONSTRAINT PK_IPM_PFT_TRIGGERS PRIMARY KEY (triggerid)
);

/*==============================================================*/
/* Table: ipm_pft_users                                         */
/*==============================================================*/
CREATE TABLE ipm_pft_users (
  userid         INT8         NOT NULL,
  alias          VARCHAR(100) NOT NULL DEFAULT '',
  name           VARCHAR(100) NOT NULL DEFAULT '',
  surname        VARCHAR(100) NOT NULL DEFAULT '',
  passwd         CHAR(32)     NOT NULL DEFAULT '',
  url            VARCHAR(255) NOT NULL DEFAULT '',
  autologin      INT4         NOT NULL DEFAULT 0,
  autologout     INT4         NOT NULL DEFAULT 900,
  lang           VARCHAR(5)   NOT NULL DEFAULT 'en_GB',
  refresh        INT4         NOT NULL DEFAULT 30,
  type           INT4         NOT NULL DEFAULT 1,
  theme          VARCHAR(128) NOT NULL DEFAULT 'default',
  attempt_failed INT4         NOT NULL DEFAULT 0,
  attempt_ip     VARCHAR(39)  NOT NULL DEFAULT '',
  attempt_clock  INT4         NOT NULL DEFAULT 0,
  rows_per_page  INT4         NOT NULL DEFAULT 50,
  CONSTRAINT PK_IPM_PFT_USERS PRIMARY KEY (userid)
);

/*==============================================================*/
/* Table: ipm_pft_users2groups                                  */
/*==============================================================*/
CREATE TABLE ipm_pft_users2groups (
  id          INT8        NOT NULL,
  usrgrpid    INT8        NOT NULL,
  userid      INT8        NOT NULL,
  tenantid    VARCHAR(50) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IPM_PFT_USERS2GROUPS PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: ipm_pft_usrgrp                                        */
/*==============================================================*/
CREATE TABLE ipm_pft_usrgrp (
  usrgrpid     INT8        NOT NULL,
  name         VARCHAR(64) NOT NULL DEFAULT '',
  gui_access   INT4        NOT NULL DEFAULT 0,
  users_status INT4        NOT NULL DEFAULT 0,
  debug_mode   INT4        NOT NULL DEFAULT 0,
  CONSTRAINT PK_IPM_PFT_USRGRP PRIMARY KEY (usrgrpid)
);

ALTER TABLE ipm_pft_acknowledges
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_1 FOREIGN KEY (eventid)
REFERENCES ipm_pft_events (eventid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_alerts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_2 FOREIGN KEY (actionid)
REFERENCES ipm_pft_actions (actionid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_alerts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_3 FOREIGN KEY (userid)
REFERENCES ipm_pft_users (userid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_alerts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_4 FOREIGN KEY (mediatypeid)
REFERENCES ipm_pft_mediaType (mediatypeid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_alerts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_5 FOREIGN KEY (eventid)
REFERENCES ipm_pft_events (eventid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_conditions
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_6 FOREIGN KEY (actionid)
REFERENCES ipm_pft_actions (actionid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_dchecks
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_7 FOREIGN KEY (druleid)
REFERENCES ipm_pft_drules (druleid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_dchecks
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_8 FOREIGN KEY (dhostid)
REFERENCES ipm_pft_dhosts (dhostid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_dhosts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_9 FOREIGN KEY (druleid)
REFERENCES ipm_pft_drules (druleid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_drules
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_10 FOREIGN KEY (proxy_hostid)
REFERENCES ipm_pft_hosts (hostid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_dservices
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_11 FOREIGN KEY (hostid)
REFERENCES ipm_pft_dhosts (dhostid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_functions
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_12 FOREIGN KEY (itemid)
REFERENCES ipm_pft_items (itemid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_functions
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_13 FOREIGN KEY (triggerid)
REFERENCES ipm_pft_triggers (triggerid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_host2group
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_14 FOREIGN KEY (groupid)
REFERENCES ipm_pft_groups (groupid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_host2group
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_15 FOREIGN KEY (hostid)
REFERENCES ipm_pft_hosts (hostid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_host2template
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_16 FOREIGN KEY (templateid)
REFERENCES ipm_pft_templates (templateid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_host2template
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_17 FOREIGN KEY (hostid)
REFERENCES ipm_pft_hosts (hostid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_httpstep
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_18 FOREIGN KEY (httptestid)
REFERENCES ipm_pft_httptest (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_httpstepitem
  ADD CONSTRAINT c_httpstepitem_19 FOREIGN KEY (httpstepid)
REFERENCES ipm_pft_httpstep (httpstepid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_httptest2item
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_20 FOREIGN KEY (httptestid)
REFERENCES ipm_pft_httptest (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_httptest2item
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_21 FOREIGN KEY (itemid)
REFERENCES ipm_pft_items (itemid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_item2applications
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_22 FOREIGN KEY (applicationid)
REFERENCES ipm_pft_applications (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_item2applications
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_23 FOREIGN KEY (itemid)
REFERENCES ipm_pft_items (itemid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_item2condition
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_24 FOREIGN KEY (itemid)
REFERENCES ipm_pft_items (itemid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_items
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_25 FOREIGN KEY (hostid)
REFERENCES ipm_pft_hosts (hostid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_media
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_26 FOREIGN KEY (mediatypeid)
REFERENCES ipm_pft_mediaType (mediatypeid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_media
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_27 FOREIGN KEY (userid)
REFERENCES ipm_pft_users (userid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_opcommand
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_28 FOREIGN KEY (operationid)
REFERENCES ipm_pft_operations (operationid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_opcommand
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_29 FOREIGN KEY (scriptid)
REFERENCES ipm_pft_scripts (scriptid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_operations
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_30 FOREIGN KEY (actionid)
REFERENCES ipm_pft_actions (actionid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_opgroup
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_31 FOREIGN KEY (operationid)
REFERENCES ipm_pft_operations (operationid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_opgroup
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_32 FOREIGN KEY (groupid)
REFERENCES ipm_pft_groups (groupid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_opmessage
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_33 FOREIGN KEY (operationid)
REFERENCES ipm_pft_operations (operationid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_opmessage
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_34 FOREIGN KEY (mediatypeid)
REFERENCES ipm_pft_mediaType (mediatypeid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_rights
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_35 FOREIGN KEY (groupid)
REFERENCES ipm_pft_groups (groupid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_rights
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_36 FOREIGN KEY (usrgrpid)
REFERENCES ipm_pft_usrgrp (usrgrpid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_scripts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_37 FOREIGN KEY (userid)
REFERENCES ipm_pft_users (userid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_scripts
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_38 FOREIGN KEY (usrgrpid)
REFERENCES ipm_pft_usrgrp (usrgrpid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_template2application
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_39 FOREIGN KEY (templateid)
REFERENCES ipm_pft_templates (templateid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_template2application
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_40 FOREIGN KEY (applicationid)
REFERENCES ipm_pft_applications (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_trigger2discovery
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_41 FOREIGN KEY (triggerid)
REFERENCES ipm_pft_triggers (triggerid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_trigger2discovery
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_42 FOREIGN KEY (parent_triggerid)
REFERENCES ipm_pft_triggers (triggerid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_triggers
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_43 FOREIGN KEY (templateid)
REFERENCES ipm_pft_triggers (triggerid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_users2groups
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_44 FOREIGN KEY (usrgrpid)
REFERENCES ipm_pft_usrgrp (usrgrpid)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ipm_pft_users2groups
  ADD CONSTRAINT FK_IPM_PFT__REFERENCE_IPM_PFT_45 FOREIGN KEY (userid)
REFERENCES ipm_pft_users (userid)
ON DELETE RESTRICT ON UPDATE RESTRICT;
