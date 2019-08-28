/**

时间：	    2017年6月22日

说明： 	  新增教育云配置api数据表

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/
DROP TABLE IF EXISTS iyun_tparty_edu;

/*==============================================================*/
/* Table: iyun_tparty_edu                                       */
/*==============================================================*/
create table iyun_tparty_edu (
   id                   varchar(36)          not null,
   educode              varchar(36)          not null,
   eduName              varchar(36)          not null,
   projectid            VARCHAR(64)          not null,
   status               char(1)              not null,
   remark               VARCHAR(100)         null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_TPARTY_EDU primary key (id)
);
