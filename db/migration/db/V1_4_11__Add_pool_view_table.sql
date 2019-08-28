
/**

时间：	   2017年2月18日

说明： 	 增加主机池视图表

*/

drop table if exists cmdb_hostpool_relations;

/*==============================================================*/
/* Table: cmdb_hostpool_relations                               */
/*==============================================================*/
create table cmdb_hostpool_relations (
  id                   varchar(36)          not null,
  viewID               varchar(36)          null,
  hpoolId              varchar(36)          null,
  previous             varchar(36)          not null,
  sequence             INT4                 null,
  constraint PK_CMDB_HOSTPOOL_RELATIONS primary key (id)
);


drop table if exists cmdb_hostpool_view2;


/*==============================================================*/
/* Table: cmdb_hostpool_view2                                   */
/*==============================================================*/
create table cmdb_hostpool_view2 (
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
  constraint PK_CMDB_HOSTPOOL_VIEW2 primary key (id)
);


drop table if exists cmdb_hostpool_items;

/*==============================================================*/
/* Table: cmdb_hostpool_items                                   */
/*==============================================================*/
create table cmdb_hostpool_items (
  id                   varchar(36)          not null,
  UUID                 varchar(36)          not null,
  option               text                 not null,
  itemtype             varchar(36)          not null,
  constraint PK_CMDB_HOSTPOOL_ITEMS primary key (id)
);

alter table cmdb_hostpool_relations
  add constraint FK_CMDB_HOS_REFERENCE_CMDB_HOS1 foreign key (hpoolId)
references cmdb_hostpool_items (id)
on delete restrict on update restrict;

alter table cmdb_hostpool_relations
  add constraint FK_CMDB_HOS_REFERENCE_CMDB_HOS2 foreign key (viewID)
references cmdb_hostpool_view2 (id)
on delete restrict on update restrict;
