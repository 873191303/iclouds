
/**
时间：     2017年1月17日

说明：     新增云管理产品下单定价目录镜像表

当前版本：  ICloudsV3.5.pdm
*/


/*==============================================================*/
/* Table: iyun_product_listPrice2imag                           */
/*==============================================================*/
DROP TABLE IF EXISTS  iyun_product_listPrice2imag;
create table iyun_product_listPrice2imag (
  id                   varchar(36)          not null,
  specid               varchar(36)          not null,
  classid              varchar(36)          not null,
  azoneid              varchar(128)         null,
  name                 VARCHAR(100)         not null,
  spec                 text                 not null,
  minValue             INT4                 null,
  maxValue             INT4                 null,
  defaultvalue         INT4                 null,
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
  constraint PK_IYUN_PRODUCT_LISTPRICE2IMAG primary key (id)
);