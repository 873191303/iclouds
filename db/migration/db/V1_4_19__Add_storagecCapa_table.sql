/**

时间：	    2017年2月28日

说明： 	  新增存储容量管理表

当前版本：	ICloudsV3.9.pdm
*/
drop table if EXISTS cmdb_cap_storage2ovelflow;

/*==============================================================*/
/* Table: cmdb_cap_storage2ovelflow                             */
/*==============================================================*/
create table cmdb_cap_storage2ovelflow (
  id                   varchar(36)          not null,
  sName                varchar(100)         null,
  sType                varchar(36)          null,
  totalsize            FLOAT8               null,
  usedsize             FLOAT8               null,
  allocation           FLOAT8               null,
  remainder            FLOAT8               null,
  usage                FLOAT8               null,
  overflow             FLOAT8               null,
  year                 VARCHAR(4)           not null,
  month                VARCHAR(2)           not null,
  day                  VARCHAR(2)           not null,
  syncDate             TIMESTAMP            not null,
  constraint PK_CMDB_CAP_STORAGE2OVELFLOW primary key (id)
);
