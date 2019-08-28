/**

文件：	    V1_2_19__Add_novavm_column.sql

时间：	    2016年12月20日

说明： 	  	修改爱数备份数据表内容

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
drop table iyun_tparty_bakup;

drop table iyun_tparty_netDisk;
/*==============================================================*/
/* Table: iyun_partner_bakup2nodes                              */
/*==============================================================*/
create table iyun_partner_bakup2nodes (
   id                   varchar(36)          not null,
   nodeid               int                  not null,
   nodeName             VARCHAR(255)         not null,
   backupid             varchar(36)          null,
   tenant_id            VARCHAR(64)          null,
   nodeMac              varchar(64)          not null,
   nodeAddr             VARCHAR(255)         not null,
   nodeUuid             varchar(36)          not null,
   version              varchar(36)          not null,
   osType               varchar(36)          null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_PARTNER_BAKUP2NODES primary key (id)
);

alter table iyun_partner_bakup2nodes
   add constraint FK_IYUN_PAR_REFERENCE_IYUN_KEY1 foreign key (tenant_id)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;


/*==============================================================*/
/* Table: iyun_partner_bakup_node2tasks                         */
/*==============================================================*/
create table iyun_partner_bakup_node2tasks (
   taskid               INT8                 not null,
   id                   varchar(36)          null,
   taskName             varchar(255)         null,
   TaskType             INT8                 null,
   DataSourceType       varchar(100)         null,
   taskstatus           varchar(50)          null,
   taskLastResult       varchar(255)         null,
   taskLastRunTime      varchar(255)         null,
   taskNextRunTime      varchar(255)         null,
   clinetID             INT8                 null,
   clientName           varchar(255)         null,
   clientAddr           varchar(255)         null,
   SrcServerId          INT8                 null,
   SrcServerName        varchar(255)         null,
   SrcServerType        varchar(100)         null,
   SrcServerAddr        varchar(255)         null,
   DestServerId         INT8                 null,
   DestServerName       varchar(255)         null,
   DestServerType       varchar(100)         null,
   DestServerAddr       varchar(255)         null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_PARTNER_BAKUP_NODE2TAS primary key (taskid)
);

alter table iyun_partner_bakup_node2tasks
   add constraint FK_IYUN_PAR_REFERENCE_IYUN_PAR2 foreign key (id)
      references iyun_partner_bakup2nodes (id)
      on delete restrict on update restrict;

