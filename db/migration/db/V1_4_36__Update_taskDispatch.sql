DROP TABLE IF EXISTS auto_sync_log;

DROP TABLE IF EXISTS auto_sync_logcollect;

DROP TABLE IF EXISTS auto_sync_taskdispactchh;

DROP TABLE IF EXISTS auto_sync_taskdispatch;

/*==============================================================*/
/* Table: auto_sync_Log                                         */
/*==============================================================*/
create table auto_sync_Log (
   id                   varchar(50)          not null,
   logCollectId         VARCHAR(50)          null,
   syncId               VARCHAR(50)          not null,
   deviceType           VARCHAR(50)          null,
   deviceId             VARCHAR(50)          null,
   result               varchar(50)          null,
   failReason           VARCHAR(50)          null,
   syncDate             TIMESTAMP            null,
   constraint PK_AUTO_SYNC_LOG primary key (id)
);

/*==============================================================*/
/* Table: auto_sync_LogCollect                                  */
/*==============================================================*/
create table auto_sync_LogCollect (
   id                   VARCHAR(36)          not null,
   syncId               VARCHAR(50)          not null,
   syncStartTime        TIMESTAMP            null,
   syncEndTime          TIMESTAMP            null,
   status               varchar(50)          null,
   syncTimes            INT2                 null,
   syncSecond           FLOAT4               null,
   syncCollectContent   varchar(500)         null,
   constraint PK_AUTO_SYNC_LOGCOLLECT primary key (id)
);

/*==============================================================*/
/* Table: auto_sync_differences                                 */
/*==============================================================*/
create table auto_sync_differences (
   id                 	VARCHAR(36)          not null,
   uuid                 VARCHAR(36)          not null,
   dataType             VARCHAR(36)          not null,
   diffType             char(1)              not null,
   taskdispatchid       VARCHAR(36)          not null,
   description          text                  null,
   syncDate             TIMESTAMP            null,
   todoType             VARCHAR(36)          null,
   result               VARCHAR(255)         null,
   todoUser             VARCHAR(36)          null,
   todoTime             TIMESTAMP            null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_AUTO_SYNC_DIFFERENCES primary key (id)
);

/*==============================================================*/
/* Table: auto_sync_taskdispatchH                              */
/*==============================================================*/
create table auto_sync_taskdispatchH (
   id                   varchar(36)          not null,
   syncId               VARCHAR(50)          not null,
   startTime            TIMESTAMP            null,
   startDate            DATE                 null,
   endType              VARCHAR(50)          null,
   endDate              DATE                 null,
   endTimes             INT2                 null,
   safeType             VARCHAR(50)          null,
   safeTimes            INT2                 null,
   periodType           INT2                 null,
   dayInterval          INT2                 null,
   weekInterval         VARCHAR(50)          null,
   monthInterval        INT2                 null,
   monthType            VARCHAR(50)          null,
   monthDay             VARCHAR(50)          null,
   yearInterval         INT2                 null,
   yearType             VARCHAR(50)          null,
   yearDay              VARCHAR(50)          null,
   previousUpdateBy     VARCHAR(36)          null,
   previousUpdateDate   TIMESTAMP            null,
   remark               VARCHAR(50)          null,
   GroupID              VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_auto_sync_taskdispatchH primary key (id)
);

/*==============================================================*/
/* Table: auto_sync_taskdispatch                                */
/*==============================================================*/
create table auto_sync_taskdispatch (
   id                   VARCHAR(36)          not null,
   dbKey                VARCHAR(50)          null,
   syncType             VARCHAR(50)          null,
   startTime            TIMESTAMP            null,
   startDate            DATE                 null,
   endType              VARCHAR(50)          null,
   endDate              DATE                 null,
   endTimes             INT2                 null,
   endOverTimes         INT2                 null,
   safeType             VARCHAR(50)          null,
   safeTimes            INT2                 null,
   periodType           INT2                 null,
   dayInterval          INT2                 null,
   weekInterval         VARCHAR(50)          null,
   monthInterval        INT2                 null,
   monthType            VARCHAR(50)          null,
   monthDay             VARCHAR(50)          null,
   yearInterval         INT2                 null,
   yearType             VARCHAR(50)          null,
   yearDay              VARCHAR(50)          null,
   status               VARCHAR(50)          null,
   nextSyncTime         TIMESTAMP            null,
   remark               VARCHAR(50)          null,
   mail                 VARCHAR(500)         null,
   GroupID              VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_AUTO_SYNC_TASKDISPATCH primary key (id)
);


alter table auto_sync_Log
   add constraint FK_AUTO_SYN_REFERENCE_AUTO_SYN1 foreign key (id)
      references auto_sync_LogCollect (id)
      on delete restrict on update restrict;

alter table auto_sync_LogCollect
   add constraint FK_AUTO_SYN_REFERENCE_AUTO_SYN2 foreign key (syncId)
      references auto_sync_taskdispatch (id)
      on delete restrict on update restrict;

alter table auto_sync_differences
   add constraint FK_AUTO_SYN_REFERENCE_AUTO_SYN3 foreign key (taskdispatchid)
      references auto_sync_taskdispatch (id)
      on delete restrict on update restrict;

alter table auto_sync_taskdispatchH
   add constraint FK_AUTO_SYN_REFERENCE_AUTO_SYN4 foreign key (syncId)
      references auto_sync_taskdispatch (id)
      on delete restrict on update restrict;