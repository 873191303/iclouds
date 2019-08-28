
drop table if exists ipm_pfm_measurement_1d;

drop table if exists ipm_pfm_measurement_1h;
           
drop table if exists ipm_pfm_measurement_6h;
           
drop table if exists ipm_pfm_metric_0d;
           
drop table if exists ipm_pfm_metric_1d;
           
drop table if exists ipm_pfm_metric_2d;
           
drop table if exists ipm_pfm_metric_3d;
           
drop table if exists ipm_pfm_metric_4d;
           
drop table if exists ipm_pfm_metric_5d;
           
drop table if exists ipm_pfm_metric_6d;
           
drop table if exists ipm_pfm_metric_7d;

/*==============================================================*/
/* Table: ipm_pfm_measurement_1d                                */
/*==============================================================*/
create table ipm_pfm_measurement_1d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   mixvalue             FLOAT4               not null,
   maxvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_MEASUREMENT_1D primary key (id)
);

/*==============================================================*/
/* Table: ipm_pfm_measurement_1h                                */
/*==============================================================*/
create table ipm_pfm_measurement_1h (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   mixvalue             FLOAT4               not null,
   maxvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_MEASUREMENT_1H primary key (id)
);

/*==============================================================*/
/* Table: ipm_pfm_measurement_6h                                */
/*==============================================================*/
create table ipm_pfm_measurement_6h (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   mixvalue             FLOAT4               not null,
   maxvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_MEASUREMENT_6H primary key (id)
);

/*==============================================================*/
/* Table: ipm_pfm_metric_0d                                     */
/*==============================================================*/
create table ipm_pfm_metric_0d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_0D primary key (id)
);

/*==============================================================*/
/* Table: ipm_pfm_metric_1d                                     */
/*==============================================================*/
create table ipm_pfm_metric_1d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_1D primary key (id)
);


/*==============================================================*/
/* Table: ipm_pfm_metric_2d                                     */
/*==============================================================*/
create table ipm_pfm_metric_2d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_2D primary key (id)
);


/*==============================================================*/
/* Table: ipm_pfm_metric_3d                                     */
/*==============================================================*/
create table ipm_pfm_metric_3d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_3D primary key (id)
);


/*==============================================================*/
/* Table: ipm_pfm_metric_4d                                     */
/*==============================================================*/
create table ipm_pfm_metric_4d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_4D primary key (id)
);


/*==============================================================*/
/* Table: ipm_pfm_metric_5d                                     */
/*==============================================================*/
create table ipm_pfm_metric_5d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_5D primary key (id)
);

/*==============================================================*/
/* Table: ipm_pfm_metric_6d                                     */
/*==============================================================*/
create table ipm_pfm_metric_6d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_6D primary key (id)
);


/*==============================================================*/
/* Table: ipm_pfm_metric_7d                                     */
/*==============================================================*/
create table ipm_pfm_metric_7d (
   id                   varchar(36)          not null,
   collecttime          TIMESTAMP            not null,
   uuid                 varchar(36)          not null,
   restype              varchar(36)          not null,
   item                 varchar(36)          not null,
   keyvalue             FLOAT4               not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IPM_PFM_METRIC_7D primary key (id)
);
