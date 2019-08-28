
/**

时间：	    2017年3月1日

说明： 	创建云运维系统参数设置2接口类表

*/

drop table if EXISTS iyun_sysconfig_interfaces;

/*==============================================================*/
/* Table: iyun_sysconfig_interfaces                             */
/*==============================================================*/
create table iyun_sysconfig_interfaces (
   id                   varchar(36)          not null,
   type                 varchar(36)          not null,
   ip                   varchar(36)          null,
   port                 varchar(36)          null,
   admin                varchar(36)          null,
   passwd               varchar(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_SYSCONFIG_INTERFACES primary key (id)
);
