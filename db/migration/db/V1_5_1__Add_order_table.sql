DROP TABLE IF EXISTS iyun_work_master2approveLog;

DROP TABLE IF EXISTS iyun_work_detail;

DROP TABLE IF EXISTS iyun_work_master;

DROP TABLE IF EXISTS iyun_order_detail2item;

DROP TABLE IF EXISTS iyun_order_detail;

DROP TABLE IF EXISTS iyun_order_master;

/*==============================================================*/
/* Table: iyun_order_master                                     */
/*==============================================================*/
CREATE TABLE iyun_order_master (
  id          VARCHAR(36)    NOT NULL,
  orderId     VARCHAR(36)    NOT NULL,
  type        VARCHAR(36)    NOT NULL,
  status      INT8           NOT NULL,
  listAmount  DECIMAL(10, 2) NOT NULL,
  amount      DECIMAL(10, 2) NULL,
  payAmount   DECIMAL(10, 2) NOT NULL,
  begindate   TIMESTAMP      NOT NULL,
  enddate     TIMESTAMP      NOT NULL,
  paydate     TIMESTAMP      NOT NULL,
  comeType    VARCHAR(36)    NULL,
  fromId      VARCHAR(36)    NULL,
  CreatedBy   VARCHAR(36)    NOT NULL,
  CreatedDate TIMESTAMP      NOT NULL,
  UpdatedBy   VARCHAR(36)    NOT NULL,
  UpdatedDate TIMESTAMP      NOT NULL,
  CONSTRAINT PK_IYUN_ORDER_MASTER PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: iyun_order_detail                                     */
/*==============================================================*/
CREATE TABLE iyun_order_detail (
  id          VARCHAR(36)    NOT NULL,
  orderId     VARCHAR(36)    NOT NULL,
  classID     VARCHAR(36)    NULL,
  reqID       VARCHAR(36)    NOT NULL,
  reqType     VARCHAR(36)    NOT NULL,
  ajson       VARCHAR(1000)  NOT NULL,
  status      CHAR(1)        NULL,
  nums        INT8           NULL,
  specId      VARCHAR(36)    NOT NULL,
  specPrice   DECIMAL(10, 2) NULL,
  setup       VARCHAR(36)    NULL,
  provider    VARCHAR(500)   NULL,
  CreatedBy   VARCHAR(36)    NOT NULL,
  CreatedDate TIMESTAMP      NOT NULL,
  UpdatedBy   VARCHAR(36)    NOT NULL,
  UpdatedDate TIMESTAMP      NOT NULL,
  CONSTRAINT PK_IYUN_ORDER_DETAIL PRIMARY KEY (id)
);

ALTER TABLE iyun_order_detail
  ADD CONSTRAINT FK_IYUN_ORD_REFERENCE_IYUN_ORD FOREIGN KEY (orderId)
REFERENCES iyun_order_master (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE iyun_order_detail
  ADD CONSTRAINT FK_IYUN_ORD_REFERENCE_IYUN_PRO FOREIGN KEY (specId)
REFERENCES iyun_product_listPrice2imag (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;


/*==============================================================*/
/* Table: iyun_order_detail2item                                */
/*==============================================================*/
CREATE TABLE iyun_order_detail2item (
  id          VARCHAR(36) NOT NULL,
  detailId    VARCHAR(36) NULL,
  classID     VARCHAR(36) NULL,
  UUID        VARCHAR(36) NOT NULL,
  CreatedBy   VARCHAR(36) NOT NULL,
  CreatedDate TIMESTAMP   NOT NULL,
  UpdatedBy   VARCHAR(36) NOT NULL,
  UpdatedDate TIMESTAMP   NOT NULL,
  CONSTRAINT PK_IYUN_ORDER_DETAIL2ITEM PRIMARY KEY (id)
);

ALTER TABLE iyun_order_detail2item
  ADD CONSTRAINT FK_IYUN_ORD_REFERENCE_IYUN_ORD FOREIGN KEY (detailId)
REFERENCES iyun_order_detail (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;


/*==============================================================*/
/* Table: iyun_work_master                                      */
/*==============================================================*/
CREATE TABLE iyun_work_master (
  id          VARCHAR(36)  NOT NULL,
  incNo       VARCHAR(100) NOT NULL,
  topic       VARCHAR(100) NULL,
  content     VARCHAR(500) NULL,
  incType     VARCHAR(36)  NULL,
  causedTime  TIMESTAMP    NULL,
  responsible VARCHAR(36)  NULL,
  step        VARCHAR(36)  NULL,
  company     VARCHAR(36)  NULL,
  customer    VARCHAR(100) NULL,
  telphone    VARCHAR(100) NULL,
  email       VARCHAR(100) NULL,
  reporter    VARCHAR(100) NULL,
  orderId     VARCHAR(36)  NULL,
  ways        VARCHAR(36)  NULL,
  reqFTime    TIMESTAMP    NULL,
  actFtime    TIMESTAMP    NULL,
  incLevel    VARCHAR(36)  NULL,
  rtuFlag     VARCHAR(100) NULL,
  instanceId  VARCHAR(36)  NULL,
  workFlowId  VARCHAR(36)  NULL,
  attachment  VARCHAR(100) NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IYUN_WORK_MASTER PRIMARY KEY (id)
);


/*==============================================================*/
/* Table: iyun_work_detail                                      */
/*==============================================================*/
CREATE TABLE iyun_work_detail (
  id          VARCHAR(36)   NOT NULL,
  jobID       VARCHAR(36)   NULL,
  detailId    VARCHAR(36)   NULL,
  classID     VARCHAR(36)   NULL,
  reqType     VARCHAR(36)   NOT NULL,
  ajson       VARCHAR(1000) NOT NULL,
  status      CHAR(1)       NULL,
  nums        INT8          NULL,
  responsible VARCHAR(36)   NULL,
  setup       VARCHAR(36)   NULL,
  specId      VARCHAR(36)   NOT NULL,
  acceptTime  TIMESTAMP     NULL,
  provider    VARCHAR(500)  NULL,
  provideTime TIMESTAMP     NULL,
  CreatedBy   VARCHAR(36)   NOT NULL,
  CreatedDate TIMESTAMP     NOT NULL,
  UpdatedBy   VARCHAR(36)   NOT NULL,
  UpdatedDate TIMESTAMP     NOT NULL,
  CONSTRAINT PK_IYUN_WORK_DETAIL PRIMARY KEY (id)
);

ALTER TABLE iyun_work_detail
  ADD CONSTRAINT FK_IYUN_WOR_REFERENCE_IYUN_WOR FOREIGN KEY (jobID)
REFERENCES iyun_work_master (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE iyun_work_detail
  ADD CONSTRAINT FK_IYUN_WOR_REFERENCE_IYUN_ORD FOREIGN KEY (detailId)
REFERENCES iyun_order_detail (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;


/*==============================================================*/
/* Table: iyun_work_master2approveLog                           */
/*==============================================================*/
CREATE TABLE iyun_work_master2approveLog (
  id          VARCHAR(36)  NOT NULL,
  detailId    VARCHAR(36)  NOT NULL,
  option      VARCHAR(36)  NOT NULL,
  comment     VARCHAR(500) NULL,
  approver    VARCHAR(36)  NOT NULL,
  attachment  VARCHAR(100) NULL,
  emails      TEXT         NULL,
  GroupID     VARCHAR(36)  NOT NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IYUN_WORK_MASTER2APPROVELOG PRIMARY KEY (id)
);

ALTER TABLE iyun_work_master2approveLog
  ADD CONSTRAINT FK_IYUN_WOR_REFERENCE_IYUN_WOR FOREIGN KEY (detailId)
REFERENCES iyun_work_detail (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE public.iyun_instance_measuredetail ADD nextpaydate TIMESTAMP NULL;

ALTER TABLE public.iyun_order_master ALTER COLUMN payamount DROP NOT NULL;
ALTER TABLE public.iyun_order_master ALTER COLUMN paydate DROP NOT NULL;

ALTER TABLE public.iyun_order_detail ALTER COLUMN reqid DROP NOT NULL;
ALTER TABLE public.iyun_order_detail ALTER COLUMN reqtype DROP NOT NULL;

ALTER TABLE public.iyun_product_listprice ADD minvalue INTEGER NULL;
ALTER TABLE public.iyun_product_listprice ADD step INTEGER NULL;

ALTER TABLE public.iyun_order_detail2item ADD name VARCHAR(100) NULL;
ALTER TABLE public.iyun_order_detail2item ADD num INTEGER NULL;
ALTER TABLE public.iyun_order_detail2item ADD userid VARCHAR(100) NULL;
ALTER TABLE public.iyun_order_detail2item ADD tenantid VARCHAR(100) NULL;
ALTER TABLE public.iyun_order_detail2item ADD status VARCHAR(36) NULL;

ALTER TABLE public.iyun_order_detail ADD stopdate TIMESTAMP NULL;

ALTER TABLE public.iyun_order_master ADD provider VARCHAR(100) NULL;