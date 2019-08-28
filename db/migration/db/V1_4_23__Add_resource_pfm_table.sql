
/**

时间：	   2017年3月1日

说明： 	 新增云资源性能指标数据表

*/
drop table if EXISTS ipm_pfm_values;

drop table if EXISTS ipm_cas_item;

/*==============================================================*/
/* Table: ipm_cas_item                                          */
/*==============================================================*/
create table ipm_cas_item (
  id                   varchar(36)          not null,
  item                 varchar(36)          not null,
  constraint PK_IPM_CAS_ITEM primary key (id)
);

/*==============================================================*/
/* Table: ipm_pfm_values                                        */
/*==============================================================*/
create table ipm_pfm_values (
  id                   varchar(36)          not null,
  collecttime          TIMESTAMP            not null,
  uuid                 varchar(36)          not null,
  restype              varchar(36)          not null,
  item                 varchar(36)          not null,
  keyvalue             FLOAT4               not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_IPM_PFM_VALUES primary key (id)
);

alter table ipm_pfm_values
  add constraint FK_IPM_PFM__REFERENCE_IPM_CAS_ foreign key (item)
references ipm_cas_item (id)
on delete restrict on update restrict;

drop table if EXISTS ipm_pfm_configuration;

/*==============================================================*/
/* Table: ipm_pfm_configuration                                 */
/*==============================================================*/
create table ipm_pfm_configuration (
  id                   varchar(36)          not null,
  param                varchar(36)          not null,
  paramvalue           FLOAT4               not null
);

drop table if EXISTS ipm_pfm_value2history;

/*==============================================================*/
/* Table: ipm_pfm_value2history                                 */
/*==============================================================*/
create table ipm_pfm_value2history (
  id                   varchar(36)          not null,
  collecttime          TIMESTAMP            not null,
  uuid                 varchar(36)          not null,
  restype              varchar(36)          not null,
  item                 varchar(36)          not null,
  keyvalue             FLOAT4               not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedDate          TIMESTAMP            not null
);
