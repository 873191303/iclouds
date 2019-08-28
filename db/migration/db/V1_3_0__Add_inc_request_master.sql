/**

时间：	    2017年1月5日

说明： 	  	增加事件工单相关表

前置版本：	ICloudsV2.9.pdm

当前版本：	ICloudsV3.1.pdm
*/

/*==============================================================*/
/* Table: inc_base_level                                        */
/*==============================================================*/
create table inc_base_level (
   id                   VARCHAR(36)          not null,
   levelName            VARCHAR(100)         not null,
   ResponseTimes        FLOAT4               null,
   finishTimes          FLOAT4               not null,
   closeTimes           FLOAT4               not null,
   GroupID              VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_INC_BASE_LEVEL primary key (id)
);


/*==============================================================*/
/* Table: inc_req_master                                        */
/*==============================================================*/
create table inc_req_master (
   id                   VARCHAR(36)          not null,
   incNo                VARCHAR(100)         not null,
   topic                varchar(100)         null,
   content              varchar(500)         null,
   incType              VARCHAR(36)          null,
   causedTime           TIMESTAMP            null,
   responsible          VARCHAR(36)          null,
   step                 VARCHAR(36)          null,
   company              VARCHAR(36)          null,
   customer             VARCHAR(100)         null,
   telphone             VARCHAR(100)         null,
   email                VARCHAR(100)         null,
   reporter             VARCHAR(100)         null,
   fromTo               VARCHAR(36)          null,
   ways                 VARCHAR(36)          null,
   reqFTime             TIMESTAMP            null,
   actFtime             TIMESTAMP            null,
   incLevel             VARCHAR(36)          null,
   rtuFlag              varchar(100)         null,
   instanceId           varchar(36)          null,
   workFlowId           varchar(36)          null,
   attachment           varchar(100)         null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_INC_REQ_MASTER primary key (id)
);

alter table inc_req_master
   add constraint FK_INC_REQ__REFERENCE_IYUN_BAS foreign key (incType)
      references iyun_base_prdClass (id)
      on delete restrict on update restrict;

alter table inc_req_master
   add constraint FK_INC_REQ__REFERENCE_INC_BASE foreign key (incLevel)
      references inc_base_level (id)
      on delete restrict on update restrict;

/*==============================================================*/
/* Table: inc_req_master2approveLog                             */
/*==============================================================*/
create table inc_req_master2approveLog (
   id                   varchar(36)          not null,
   jobID                VARCHAR(36)          null,
   insID                varchar(36)          not null,
   step                 varchar(100)         not null,
   taskID               varchar(36)          not null,
   option               varchar(36)          not null,
   comment              varchar(500)         null,
   approver             varchar(36)          not null,
   attachment           varchar(100)         null,
   emails               TEXT                 null,
   GroupID              VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_INC_REQ_MASTER2APPROVELOG primary key (id)
);

alter table inc_req_master2approveLog
   add constraint FK_INC_REQ__REFERENCE_INC_REQ_ foreign key (jobID)
      references inc_req_master (id)
      on delete restrict on update restrict;