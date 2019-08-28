/**

时间：	    2017年2月23日

说明： 	  移除集群配置明细表,增加CVK和CVM表

当前版本：	ICloudsV3.9.pdm
*/

drop table if EXISTS cmdb_cap_server2ovelflow;

drop table if EXISTS cmdb_cap_cvm2ovelflow;

/*==============================================================*/
/* Table: cmdb_cap_cvm2ovelflow                                 */
/*==============================================================*/
create table cmdb_cap_cvm2ovelflow (
  id                   varchar(36)          not null,
  cvmName              varchar(100)         null,
  totalCPU             INT8                 null,
  assignCPU            INT8                 null,
  cpuUsage             FLOAT8               null,
  totalMEM             INT8                 null,
  assignMEM            INT8                 null,
  memUsage             FLOAT8               null,
  year                 VARCHAR(4)           not null,
  month                VARCHAR(2)           not null,
  day                  VARCHAR(2)           not null,
  tenant               varchar(36)          null,
  syncDate             TIMESTAMP            not null,
  constraint PK_CMDB_CAP_CVM2OVELFLOW primary key (id)
);

/*==============================================================*/
/* Table: cmdb_cap_server2ovelflow                              */
/*==============================================================*/
create table cmdb_cap_server2ovelflow (
  id                   varchar(36)          not null,
  poolID               varchar(36)          not null,
  hostName             varchar(100)         not null,
  hostID               varchar(36)          not null,
  assetID              varchar(36)          null,
  seriNo               varchar(36)          null,
  belongTo             varchar(36)          null,
  year                 VARCHAR(4)           not null,
  month                VARCHAR(2)           not null,
  day                  VARCHAR(2)           not null,
  cpus                 INT2                 null,
  cpuOverSize          FLOAT4               not null,
  ram                  INT2                 null,
  ramOverSize          FLOAT4               null,
  vms                  INT4                 null,
  clousterId           varchar(36)          null,
  tenant               varchar(36)          null,
  syncDate             TIMESTAMP            not null,
  IP                   varchar(36)          null,
  MAC                  varchar(36)          null,
  constraint PK_CMDB_CAP_SERVER2OVELFLOW primary key (id)
);

alter table cmdb_cap_server2ovelflow
  add constraint FK_CMDB_CAP_REFERENCE_CMDB_CAP001 foreign key (belongTo)
references cmdb_cap_cvm2ovelflow (id)
on delete restrict on update restrict;

alter table cmdb_cap_server2ovelflow
  add constraint FK_CMDB_CAP_REFERENCE_CMDB_SER002 foreign key (poolID)
references cmdb_server_pools2host (id)
on delete restrict on update restrict;

alter table cmdb_cap_server2ovelflow
  add constraint FK_CMDB_CAP_REFERENCE_CMDB_SER003 foreign key (clousterId)
references cmdb_server_clusters (id)
on delete restrict on update restrict;
