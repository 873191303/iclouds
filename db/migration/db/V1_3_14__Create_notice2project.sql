
/**
时间：       2017年1月19日

说明：     通知公告

前置版本： ICloudsV3.4.pdm
当前版本： ICloudsV3.5.pdm
*/
/*==============================================================*/
/* Table: iyun_base_project2notice                              */
/*==============================================================*/
create table iyun_base_notice2project (
   id                   varchar(36)          not null,
   tenantid             varchar(36)          null,
   noticeid             varchar(36)          null,
   isread               BOOL DEFAULT 		 false,
   createdby 			   varchar(36) 		   not null,
   createddate 			timestamp(6)   		not null,
   updatedby 			   varchar(36) 		   not null,
   updateddate 			timestamp(6) 		   not null,
   constraint PK_IYUN_BASE_NOTICE2PROJECT primary key (id)
);

alter table iyun_base_notice2project
add constraint FK_IYUN_NOTICE2PROJECT_REFERENCE_IYUN_BASE_NOTICE foreign key (noticeid)
references iyun_base_notice (id)
on delete restrict on update restrict;

alter table iyun_base_notice2project
add constraint FK_IYUN_NOTICE2PROJECT_REFERENCE_IYUN_PROJECT foreign key (tenantid)
references iyun_keystone_project (id)
on delete restrict on update restrict;