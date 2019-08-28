
/**

时间：	   2017年3月10日

说明： 	 增加存储视图表

*/
drop table if exists cmdb_storage_relations;

/*==============================================================*/
/* Table: cmdb_storage_relations                                */
/*==============================================================*/
create table cmdb_storage_relations (
  id                   varchar(36)          not null,
  viewID               varchar(36)          null,
  netId                varchar(36)          not null,
  previous             varchar(36)          null,
  sequence             INT4                 null,
  constraint PK_CMDB_STORAGE_RELATIONS primary key (id)
);

drop table if exists cmdb_storage_views;

/*==============================================================*/
/* Table: cmdb_storage_views                                    */
/*==============================================================*/
create table cmdb_storage_views (
  id                   varchar(36)          not null,
  name                 varchar(20)          not null,
  description          varchar(250)         null,
  sequence             INT2                 null,
  userId               varchar(36)          null,
  sessionId            VARCHAR(64)          null,
  version              varchar(36)          null,
  lock                 bool                 not null,
  projectid            varchar(36)          null,
  CreatedBy            VARCHAR(36)          not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedBy            VARCHAR(36)          not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_CMDB_STORAGE_VIEWS primary key (id)
);

drop table if exists cmdb_storage_items;

/*==============================================================*/
/* Table: cmdb_storage_items                                    */
/*==============================================================*/
create table cmdb_storage_items (
  id                   varchar(36)          not null,
  UUID                 varchar(36)          not null,
  option               text                 null,
  name                 varchar(36)          not null,
  itemtype             varchar(36)          not null,
  constraint PK_CMDB_STORAGE_ITEMS primary key (id)
);

alter table cmdb_storage_relations
  add constraint FK_CMDB_STO_REFERENCE_CMDB_STO0 foreign key (netId)
references cmdb_storage_items (id)
on delete restrict on update restrict;

alter table cmdb_storage_relations
  add constraint FK_CMDB_STO_REFERENCE_CMDB_STO1 foreign key (viewID)
references cmdb_storage_views (id)
on delete restrict on update restrict;
