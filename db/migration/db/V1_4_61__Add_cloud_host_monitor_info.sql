
/*==============================================================*/
/* Table: iyun_spe_networks                                     */
/*==============================================================*/
create table iyun_spe_networks (
   id                   varchar(36)          not null,
   hostid               varchar(36)          not null,
   mac                  varchar(36)          not null,
   IP                   varchar(36)          not null,
   uuid                 varchar(36)          not null,
   monitorid            varchar(36)          null,
   portid               varchar(36)          null,
   tenant_id            VARCHAR(255)         not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_SPE_NETWORKS primary key (id)
);

/*==============================================================*/
/* Table: iyun_spe_netTemplater                                 */
/*==============================================================*/
create table iyun_spe_netTemplater (
   id                   varchar(36)          not null,
   tenant_id            VARCHAR(255)         not null,
   vsId                 INT8                 null,
   vsName               VARCHAR(255)         null,
   deviceModel          VARCHAR(255)         null,
   isKernelAccelerated  INT2                 null,
   mode                 VARCHAR(255)         null,
   profileId            INT8                 not null,
   profileName          VARCHAR(255)         null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_SPE_NETTEMPLATER primary key (id)
);