/**
时间：	    2017年1月12日

说明： 	  创建产品规格表与目录定价表

当前版本：	ICloudsV3.3.pdm
*/

/*==============================================================*/
/* Table: iyun_product_specs2key                                */
/*==============================================================*/
create table iyun_product_specs2key (
  id                   varchar(36)          not null,
  classid              VARCHAR(36)          not null,
  keyname              varchar(500)         not null,
  "default"            varchar(100)         null,
  mixValue             INT8                 null,
  maxValue             INT8                 null,
  unit                 varchar(100)         not null,
  seq                  INT2                 null,
  CreatedBy            VARCHAR(36)          not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedBy            VARCHAR(36)          not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_PRODUCT_SPECS2KEY primary key (id)
);

/*==============================================================*/
/* Table: iyun_product_specs2keyvalue                           */
/*==============================================================*/
create table iyun_product_specs2keyvalue (
  Id                   varchar(36)          not null,
  key                  varchar(36)          not null,
  value                VARCHAR(500)         not null,
  isDefault            BOOL                 not null,
  listPrice            DECIMAL(10,2)        null,
  unit                 varchar(100)         not null,
  CreatedBy            VARCHAR(36)          not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedBy            VARCHAR(36)          not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_PRODUCT_SPECS2KEYVALUE primary key (Id)
);

/*==============================================================*/
/* Table: iyun_product_listPrice                                */
/*==============================================================*/
create table iyun_product_listPrice (
  id                   varchar(36)          not null,
  classid              varchar(36)          not null,
  azoneid              varchar(128)         null,
  name                 VARCHAR(100)         not null,
  spec                 text                 not null,
  specPrice            DECIMAL(10,2)        null,
  minValue             INT4                 null,
  maxValue             INT4                 null,
  "default"            INT4                 null,
  unit                 VARCHAR(36)          not null,
  listPrice            DECIMAL(10,2)        null,
  step                 INT4                 null,
  stepprice            DECIMAL(10,2)        null,
  discount             DECIMAL(10,2)        null,
  flavorid             varchar(36)          null,
  CreatedBy            VARCHAR(36)          not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedBy            VARCHAR(36)          not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_PRODUCT_LISTPRICE primary key (id)
);

/*==============================================================*/
/* Table: iyun_product_specs2keyvalue                           */
/*==============================================================*/
alter table iyun_product_specs2keyvalue
  add constraint FK_IYUN_PRO_REFERENCE_IYUN_PRO1 foreign key (key)
references iyun_product_specs2key (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_product_specs2key                                */
/*==============================================================*/
alter table iyun_product_specs2key
  add constraint FK_IYUN_PRO_REFERENCE_IYUN_BAS1 foreign key (classid)
references iyun_base_prdClass (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_product_listPrice                                */
/*==============================================================*/

alter table iyun_product_listPrice
  add constraint FK_IYUN_PRO_REFERENCE_IYUN_BAS2 foreign key (classid)
references iyun_base_prdClass (id)
on delete restrict on update restrict;

alter table iyun_product_listPrice
  add constraint FK_IYUN_PRO_REFERENCE_IYUN_KEY1 foreign key (azoneid)
references iyun_keystone_azone (UUID)
on delete restrict on update restrict;
