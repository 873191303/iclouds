DROP TABLE IF EXISTS iyun_healthy_value2days;
DROP TABLE IF EXISTS iyun_healthy_values;
DROP TABLE IF EXISTS iyun_sla_instance2originals;
DROP TABLE IF EXISTS iyun_healthys_type2items;
DROP TABLE IF EXISTS iyun_healthy_instances;
DROP TABLE IF EXISTS iyun_healthy_types;

/*==============================================================*/
/* Table: iyun_healthy_types                                    */
/*==============================================================*/
create table iyun_healthy_types (
   id                   varchar(36)          not null,
   healthName           varchar(500)         not null,
   configurate          text                 not null,
   className            varchar(500)         null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_HEALTHY_TYPES primary key (id)
);

/*==============================================================*/
/* Table: iyun_healthys_type2items                              */
/*==============================================================*/
create table iyun_healthys_type2items (
   id                   varchar(36)          not null,
   itemName             varchar(1000)        not null,
   type                 varchar(36)          not null,
   weight               FLOAT4               not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_HEALTHYS_TYPE2ITEMS primary key (id)
);

/*==============================================================*/
/* Table: iyun_healthy_instances                                */
/*==============================================================*/
create table iyun_healthy_instances (
   id                   varchar(36)          not null,
   instanceid           varchar(36)          not null,
   instanceName         varchar(1000)        not null,
   configurate          text                 not null,
   status               char(1)              not null,
   type                 varchar(36)          not null,
   tenantid             VARCHAR(255)         not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_HEALTHY_INSTANCES primary key (id)
);

/*==============================================================*/
/* Table: iyun_sla_instance2originals                           */
/*==============================================================*/
create table iyun_sla_instance2originals (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   instanceId           varchar(36)          not null,
   itemid               varchar(36)          null,
   itemValue            FLOAT8               not null,
   weight               FLOAT4               not null,
   tenantid             VARCHAR(255)         null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_SLA_INSTANCE2ORIGINALS primary key (id)
);

/*==============================================================*/
/* Table: iyun_healthy_values                                   */
/*==============================================================*/
create table iyun_healthy_values (
   id                   varchar(36)          not null,
   instanceId           varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   healthValue          FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_HEALTHY_VALUES primary key (id)
);

/*==============================================================*/
/* Table: iyun_healthy_value2days                               */
/*==============================================================*/
create table iyun_healthy_value2days (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   instanceId           varchar(36)          not null,
   healthValue          FLOAT8               not null,
   maxSlaValue          FLOAT8               not null,
   minSlaValue          FLOAT8               not null,
   tenantid             VARCHAR(255)         null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_HEALTHY_VALUE2DAYS primary key (id)
);

alter table iyun_healthys_type2items
   add constraint FK_IYUN_HEA_REFERENCE_IYUN_HEA1 foreign key (type)
      references iyun_healthy_types (id)
      on delete restrict on update restrict;

alter table iyun_sla_instance2originals
   add constraint FK_IYUN_SLA_REFERENCE_IYUN_HEA4 foreign key (instanceId)
      references iyun_healthy_instances (id)
      on delete restrict on update restrict;

alter table iyun_sla_instance2originals
   add constraint FK_IYUN_SLA_REFERENCE_IYUN_HEA5 foreign key (id)
      references iyun_healthys_type2items (id)
      on delete restrict on update restrict;

alter table iyun_healthy_instances
   add constraint FK_IYUN_HEA_REFERENCE_IYUN_HEA2 foreign key (type)
      references iyun_healthy_types (id)
      on delete restrict on update restrict;

alter table iyun_healthy_values
   add constraint FK_IYUN_HEA_REFERENCE_IYUN_HEA3 foreign key (instanceId)
      references iyun_healthy_instances (id)
      on delete restrict on update restrict;

ALTER TABLE public.cmdb_app_items ADD name VARCHAR(100) NULL;