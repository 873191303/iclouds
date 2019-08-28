
/**

序号：	    17

文件：	    V1_2_6__Add_task_table.sql

时间：	    2016年12月6日

说明： 	  增加队列任务表

影响对象： iyun_base_tasksh,iyun_base_task2exec,iyun_base_tasks

前置版本：	ICloudsV2.6.pdm

当前版本：	ICloudsV2.6.pdm

 */
/*==============================================================*/
/* Table: iyun_base_task2exec                                   */
/*==============================================================*/
create table iyun_base_task2exec (
  id                   varchar(36)          not null,
  hid                  varchar(36)          not null,
  taskid               varchar(36)          not null,
  status               char(1)              not null,
  result               text                 not null,
  retryNum             INT2                 null,
  CreatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_BASE_TASK2EXEC primary key (id)
);
/*==============================================================*/
/* Table: iyun_base_tasksh                                      */
/*==============================================================*/
create table iyun_base_tasksh (
  id                   varchar(36)          not null,
  tid                  varchar(36)          not null,
  busID                varchar(36)          not null,
  input                text                 null,
  busType              varchar(100)         not null,
  pushTime             TIMESTAMP            null,
  stackTime            TIMESTAMP            null,
  finishTime           TIMESTAMP            null,
  projectid            VARCHAR(64)          null,
  status               char(1)              not null,
  CreatedBy            VARCHAR(36)          not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedBy            VARCHAR(36)          not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_BASE_TASKSH primary key (id)
);

/*==============================================================*/
/* Table: iyun_base_tasks                                       */
/*==============================================================*/
create table iyun_base_tasks (
  id                   varchar(36)          not null,
  busID                varchar(36)          not null,
  input                text                 null,
  busType              varchar(100)         not null,
  pushTime             TIMESTAMP            null,
  stackTime            TIMESTAMP            null,
  finishTime           TIMESTAMP            null,
  status               char(1)              not null,
  projectid            VARCHAR(64)          null,
  CreatedBy            VARCHAR(36)          not null,
  CreatedDate          TIMESTAMP            not null,
  UpdatedBy            VARCHAR(36)          not null,
  UpdatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_BASE_TASKS primary key (id)
);

alter table iyun_base_task2exec
  add constraint FK_IYUN_BAS_REFERENCE_IYUN_BAS1 foreign key (hid)
references iyun_base_tasksh (id)
on delete restrict on update restrict;

alter table iyun_base_tasksh
  add constraint FK_IYUN_BAS_REFERENCE_IYUN_BAS2 foreign key (tid)
references iyun_base_tasks (id)
on delete restrict on update restrict;

