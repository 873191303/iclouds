DROP TABLE IF EXISTS isso_auth_user;

/*==============================================================*/
/* Table: isso_auth_user                                        */
/*==============================================================*/
CREATE TABLE isso_auth_user (
  id            VARCHAR(36)  NOT NULL,
  projectid     VARCHAR(50)  NULL,
  projectname   VARCHAR(500) NULL,
  DeptID        VARCHAR(36)  NULL,
  DeptName      VARCHAR(500) NULL,
  LoginName     VARCHAR(10)  NOT NULL,
  UserName      VARCHAR(100) NOT NULL,
  IsAdmin       CHAR(1)      NULL,
  IDCard        VARCHAR(20)  NULL,
  Password      VARCHAR(100) NULL,
  Remark        VARCHAR(100) NULL,
  EMail         VARCHAR(100) NULL,
  Telephone     VARCHAR(100) NULL,
  Status        CHAR(1)      NULL,
  EffectiveDate TIMESTAMP    NULL,
  ExpireDate    TIMESTAMP    NULL,
  authType      CHAR(1)      NOT NULL,
  comefrom      VARCHAR(36)  NULL,
  CreatedBy     VARCHAR(36)  NOT NULL,
  CreatedDate   TIMESTAMP    NOT NULL,
  UpdatedBy     VARCHAR(36)  NOT NULL,
  UpdatedDate   TIMESTAMP    NOT NULL,
  CONSTRAINT PK_ISSO_AUTH_USER PRIMARY KEY (id)
);

DROP TABLE IF EXISTS isso_auth_subsystem;

/*==============================================================*/
/* Table: isso_auth_subsystem                                   */
/*==============================================================*/
CREATE TABLE isso_auth_subsystem (
  id          VARCHAR(36)  NOT NULL,
  appName     VARCHAR(500) NOT NULL,
  status      CHAR(1)      NOT NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_ISSO_AUTH_SUBSYSTEM PRIMARY KEY (id)
);

DROP TABLE IF EXISTS isso_auth_user2system;

/*==============================================================*/
/* Table: isso_auth_user2system                                      */
/*==============================================================*/
CREATE TABLE isso_auth_user2system (
  id          VARCHAR(36) NOT NULL,
  userid      VARCHAR(36) NULL,
  systemid    VARCHAR(36) NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_ISSO_USER_SYSTEM PRIMARY KEY (id)
);

ALTER TABLE isso_auth_user2system
  ADD CONSTRAINT FK_ISSO_USE_REFERENCE_ISSO_AUT1 FOREIGN KEY (userid)
REFERENCES isso_auth_user (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE isso_auth_user2system
  ADD CONSTRAINT FK_ISSO_USE_REFERENCE_ISSO_AUT2 FOREIGN KEY (systemid)
REFERENCES isso_auth_subsystem (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP TABLE IF EXISTS isso_sm_role;

/*==============================================================*/
/* Table: isso_sm_role                                          */
/*==============================================================*/
CREATE TABLE isso_sm_role (
  ID          VARCHAR(36)  NOT NULL,
  RoleName    VARCHAR(100) NOT NULL,
  RoleDesc    VARCHAR(100) NULL,
  PRoleID     VARCHAR(36)  NULL,
  flag        CHAR(1)      NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_ISSO_SM_ROLE PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS isso_sm_user2Role;

/*==============================================================*/
/* Table: isso_sm_user2Role                                     */
/*==============================================================*/
CREATE TABLE isso_sm_user2Role (
  ID          VARCHAR(36) NOT NULL,
  UserID      VARCHAR(36) NOT NULL,
  RoleID      VARCHAR(36) NOT NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_ISSO_SM_USER2ROLE PRIMARY KEY (ID)
);

ALTER TABLE isso_sm_user2Role
  ADD CONSTRAINT FK_ISSO_SM__REFERENCE_ISSO_AUT3 FOREIGN KEY (UserID)
REFERENCES isso_auth_user (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE isso_sm_user2Role
  ADD CONSTRAINT FK_ISSO_SM__REFERENCE_ISSO_SM_4 FOREIGN KEY (RoleID)
REFERENCES isso_sm_role (ID)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP TABLE IF EXISTS isso_sm_resources;

/*==============================================================*/
/* Table: isso_sm_resources                                     */
/*==============================================================*/
CREATE TABLE isso_sm_resources (
  ID          VARCHAR(36)  NOT NULL,
  ResName     VARCHAR(100) NOT NULL,
  resType     VARCHAR(100) NOT NULL,
  ResPath     VARCHAR(100) NOT NULL,
  ParentID    VARCHAR(36)  NOT NULL,
  Depth       INT2         NULL,
  Description VARCHAR(50)  NULL,
  ItemSEQ     INT2         NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_ISSO_SM_RESOURCES PRIMARY KEY (ID)
);
DROP TABLE IF EXISTS isso_sm_role2Res;

/*==============================================================*/
/* Table: isso_sm_role2Res                                      */
/*==============================================================*/
CREATE TABLE isso_sm_role2Res (
  ID          VARCHAR(36) NOT NULL,
  RoleID      VARCHAR(36) NOT NULL,
  ResID       VARCHAR(36) NOT NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_ISSO_SM_ROLE2RES PRIMARY KEY (ID)
);

ALTER TABLE isso_sm_role2Res
  ADD CONSTRAINT FK_ISSO_SM__REFERENCE_ISSO_SM_5 FOREIGN KEY (RoleID)
REFERENCES isso_sm_role (ID)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE isso_sm_role2Res
  ADD CONSTRAINT FK_ISSO_SM__REFERENCE_ISSO_SM_6 FOREIGN KEY (ResID)
REFERENCES isso_sm_resources (ID)
ON DELETE RESTRICT ON UPDATE RESTRICT;

DROP TABLE IF EXISTS isso_auth_logType;

/*==============================================================*/
/* Table: sso_auth_logType                                      */
/*==============================================================*/
CREATE TABLE isso_auth_logType (
  id          VARCHAR(36)  NOT NULL,
  description VARCHAR(100) NOT NULL,
  remark      VARCHAR(100) NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_SSO_AUTH_LOGTYPE PRIMARY KEY (id
  )
);

DROP TABLE IF EXISTS isso_auth_operateLogs;

/*==============================================================*/
/* Table: isso_auth_operateLogs                                 */
/*==============================================================*/
CREATE TABLE isso_auth_operateLogs (
  id          VARCHAR(36)  NOT NULL,
  UserID      VARCHAR(36)  NOT NULL,
  comefrom    VARCHAR(36)  NULL,
  LogTypeID   VARCHAR(36)  NULL,
  result      VARCHAR(32)  NULL,
  remark      VARCHAR(500) NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_ISSO_AUTH_OPERATELOGS PRIMARY KEY (id)
);

ALTER TABLE isso_auth_operateLogs
  ADD CONSTRAINT FK_ISSO_AUT_REFERENCE_ISSO_AUT7 FOREIGN KEY (UserID)
REFERENCES isso_auth_user (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE isso_auth_operateLogs
  ADD CONSTRAINT FK_ISSO_AUT_REFERENCE_SSO_AUTH8 FOREIGN KEY (LogTypeID)
REFERENCES isso_auth_logType (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;
