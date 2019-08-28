/**
时间：       2017年1月13日

说明：     创建通知公告及公告详情

当前版本： ICloudsV3.3.pdm
*/

/*==============================================================*/
/* Table: iyun_base_notice                               */
/*==============================================================*/

create table iyun_base_notice (
   id                   varchar(36)          not null,
   title				      varchar(255)         not null,
   brief                varchar(255)         not null,
   status 				   varchar(16) 		   null, 
   grade  				   varchar(16)          null,
   type  				   varchar(36)          null,
   tenant_id            varchar(64)          null,
   deleted           	varchar(32)          not null default '0',
   hint              	bool 			         not null default true,
   createdby 			   varchar(36) 		   not null,
   createddate 			timestamp(6)   		not null,
   updatedby 			   varchar(36) 		   not null,
   updateddate 			timestamp(6) 		   not null,
   constraint PK_IYUN_BASE_NOTICE primary key (id)
);
alter table iyun_base_notice
   add constraint FK_IYUN_NOTICE_REFERENCE_IYUN_KEY foreign key (tenant_id)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;


/*==============================================================*/
/* Table: iyun_notice_detail                               */
/*==============================================================*/

create table iyun_notice_detail (
   id          varchar(36)       not null,
   notice_id   varchar(36)       not null,
   detail      varchar(1024)     null,
   constraint PK_IYUN_NOTICE_DETAIL primary key (id)
);

alter table iyun_notice_detail
add constraint FK_IYUN_NOTICEDETAIL_REFERENCE_IYUN_KEY foreign key (notice_id)
references iyun_base_notice (id)
on delete restrict on update restrict;