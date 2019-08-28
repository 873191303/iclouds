
/**

序号：	    24

文件：	    V1_2_13__Add_recycle_table.sql

时间：	    2016年12月17日

说明： 	  修改字段

影响对象：	iyun_base_prdClass

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
create table iyun_recycle_items (
   id                   VARCHAR(36)          not null,
   resID                VARCHAR(36)          not null,
   classID              VARCHAR(36)          null,
   recycleType          char(1)              null,
   inboundTime          TIMESTAMP            null,
   recycleTime          TIMESTAMP            null,
   recycleAction        VARCHAR(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_RECYCLE_ITEMS primary key (id)
);

alter table iyun_recycle_items
add constraint FK_IYUN_REC_REFERENCE_IYUN_BAS foreign key (classID)
references iyun_base_prdClass (id)
on delete restrict on update restrict;
