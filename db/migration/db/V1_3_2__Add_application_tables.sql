/**

时间：	    2017年1月9日

说明： 	  	增加应用配置

前置版本：	ICloudsV3.1.pdm

当前版本：	ICloudsV3.2.pdm
*/


/*==============================================================*/
/* Table: cmdb_service_master                                   */
/*==============================================================*/
create table cmdb_service_master (
   id                   varchar(36)          not null,
   service_type         int                  null,
   serviceVersion       varchar(50)          null,
   name                 varchar(100)         null,
   instanceName         varchar(100)         null,
   hosts                varchar(36)          null,
   hostType             varchar(36)          null,
   ip                   varchar(40)          null,
   port                 varchar(10)          null,
   adminConsole         varchar(40)          null,
   accessAddress        varchar(50)          null,
   supportLevel         varchar(50)          null,
   purpose              varchar(50)          null,
   note                 varchar(100)         null,
   serverowner          varchar(36)          null,
   isPrivate            BOOL                 null,
  	projectid           	varchar(64)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_CMDB_SERVICE_MASTER primary key (id)
);

alter table cmdb_service_master
   add constraint FK_CMDB_SER_REFERENCE_IYUN_KEY foreign key (projectid)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;
      


/*==============================================================*/
/* Table: cmdb_application_master                               */
/*==============================================================*/
create table cmdb_application_master (
   id                   VARCHAR(36)          not null,
   AppName              VARCHAR(100)         not null,
   Mode                 VARCHAR(36)          null,
   DNS                  VARCHAR(500)         null,
   URL                  VARCHAR(100)         null,
   Port                 VARCHAR(36)          null,
   Scope                VARCHAR(100)         null,
   AppRrame             VARCHAR(36)          null,
   TRTime               TIMESTAMP            null,
   Domains              VARCHAR(36)          null,
   AppDept              VARCHAR(36)          null,
   Status               VARCHAR(36)          null,
   Remark               VARCHAR(200)         null,
   owner                VARCHAR(36)          null,
   isPrivate            BOOL                 null,
   projectid           	varchar(64)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_CMDB_APPLICATION_MASTER primary key (id)
);

alter table cmdb_application_master
   add constraint FK_CMDB_APP_REFERENCE_IYUN_KEY foreign key (projectid)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;
      


/*==============================================================*/
/* Table: cmdb_service_clusters                                 */
/*==============================================================*/
create table cmdb_service_clusters (
   ID                   varchar(36)          not null,
   cname                varchar(100)         not null,
   relation             varchar(100)         not null,
   VIP                  varchar(100)         null,
   remark               varchar(500)         null,
   projectid           	varchar(64)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_CMDB_SERVICE_CLUSTERS primary key (ID)
);

alter table cmdb_service_clusters
   add constraint FK_CMDB_SER_REFERENCE_IYUN_KEY foreign key (projectid)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;



/*==============================================================*/
/* Table: cmdb_app_views                                        */
/*==============================================================*/
create table cmdb_app_views (
   id                   varchar(36)          not null,
   name                 varchar(20)          not null,
   description          varchar(250)         null,
   sequence             INT2                 null,
   projectid           	varchar(64)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_CMDB_APP_VIEWS primary key (id)
);

alter table cmdb_app_views
   add constraint FK_CMDB_APP_REFERENCE_IYUN_KEY foreign key (projectid)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;

/*==============================================================*/
/* Table: cmdb_app_items                                        */
/*==============================================================*/
create table cmdb_app_items (
   id                   varchar(36)          not null,
   UUID                 varchar(36)          not null,
   option               text                 not null,
   itemtype             varchar(36)          not null,
   constraint PK_CMDB_APP_ITEMS primary key (id)
);
      
/*==============================================================*/
/* Table: cmdb_app_view                                         */
/*==============================================================*/
create table cmdb_app_view (
   id                   varchar(36)          not null,
   viewID               varchar(36)          null,
   appId                varchar(36)          null,
   previous             varchar(36)          not null,
   sequence             INT4                 null,
   constraint PK_CMDB_APP_VIEW primary key (id)
);


alter table cmdb_app_view
   add constraint FK_CMDB_APP_REFERENCE_CMDB_APP1 foreign key (appId)
      references cmdb_app_items (id)
      on delete restrict on update restrict;

alter table cmdb_app_view
   add constraint FK_CMDB_APP_REFERENCE_CMDB_APP foreign key (viewID)
      references cmdb_app_views (id)
      on delete restrict on update restrict;


/*==============================================================*/
/* Table: cmdb_database_master                                  */
/*==============================================================*/
create table cmdb_database_master (
   id                   varchar(36)          not null,
   dbtype               int                  null,
   dbDesc               varchar(100)         null,
   DBVersion            varchar(50)          null,
   instanceName         varchar(100)         null,
   dbname               varchar(50)          null,
   port                 varchar(10)          null,
   hosts                varchar(36)          null,
   hostType             varchar(36)          null,
   ip                   varchar(40)          null,
   adminConsole         varchar(40)          null,
   supportLevel         varchar(50)          null,
   purpose              varchar(50)          null,
   remark               varchar(100)         null,
   DBowner              varchar(36)          null,
   isPrivate            BOOL                 null,
   projectid           	varchar(64)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_CMDB_DATABASE_MASTER primary key (id)
);

alter table cmdb_database_master
   add constraint FK_CMDB_DAT_REFERENCE_IYUN_KEY foreign key (projectid)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;
