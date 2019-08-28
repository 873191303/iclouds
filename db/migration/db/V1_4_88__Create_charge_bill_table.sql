/**

时间：	    2017-08-10

说明： 	  新增计费账单表

前置版本：	ICloudsV5.3.pdm

当前版本：	ICloudsV5.3.pdm
*/
DROP TABLE IF EXISTS iyun_instance_bilings;

/*==============================================================*/
/* Table: iyun_instance_bilings                                 */
/*==============================================================*/
create table iyun_instance_bilings (
   id                   varchar(36)          not null,
   instanceId           VARCHAR(36)          not null,
   measureId            VARCHAR(36)          not null,
   mYear                INT4                 not null,
   mMonth               INT4                 not null,
   mDay                 INT4                 not null,
   num                  int4                 not null,
   price                DECIMAL(10,2)        not null,
   amount               DECIMAL(10,2)        not null,
   bilingDate           TIMESTAMP            not null,
   bilingType           char(1)              not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_INSTANCE_BILINGS primary key (id)
);

alter table iyun_instance_bilings
   add constraint FK_IYUN_INS_REFERENCE_IYUN_INS foreign key (measureId)
      references iyun_instance_measuredetail (id)
      on delete restrict on update restrict;
