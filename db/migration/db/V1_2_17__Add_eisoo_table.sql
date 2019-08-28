
/**

文件：	    V1_2_17__Add_eisoo_table.sql

时间：	    2016年12月20日

说明： 	  	增加第三方爱数对接表

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */

/*==============================================================*/
/* Table: iyun_tparty_bakup                                     */
/*==============================================================*/
create table iyun_tparty_bakup (
   id                   varchar(36)          not null,
   vhostid              varchar(36)          not null,
   hostName             VARCHAR(255)         not null,
   IP                   varchar(36)          not null,
   bkContent            VARCHAR(255)         not null,
   bTime                TIMESTAMP            not null,
   copies               INT2                 not null,
   lastbakTime          TIMESTAMP            null,
   status               char(1)              null,
   projectid            VARCHAR(64)          null,
   UpdatedDate          TIMESTAMP            null,
   constraint PK_IYUN_TPARTY_BAKUP primary key (id)
);


/*==============================================================*/
/* Table: iyun_tparty_netDisk                                   */
/*==============================================================*/
create table iyun_tparty_netDisk (
   id                   varchar(36)          not null,
   tUsers               INT4                 not null,
   uUsers               INT4                 not null,
   tCapacity            INT8                 not null,
   uCapacity            INT8                 not null,
   projectid            VARCHAR(64)          null,
   UpdatedDate          TIMESTAMP            null,
   constraint PK_IYUN_TPARTY_NETDISK primary key (id)
);

