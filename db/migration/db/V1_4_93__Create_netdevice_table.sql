/** 去除计量清单里面的单价字段 */
ALTER TABLE public.iyun_instance_measuredetail DROP price;

/** 定价表和定价镜像记录扣费周期 去除开始价格字段 定价镜像表记录计价的基准值和步长 */
ALTER TABLE public.iyun_product_listprice ADD measuretype VARCHAR(36) NULL;
ALTER TABLE public.iyun_product_listprice2imag ADD measuretype VARCHAR(36) NULL;
ALTER TABLE public.iyun_product_listprice2imag DROP beginprice;
ALTER TABLE public.iyun_product_listprice DROP beginprice;
ALTER TABLE public.iyun_product_listprice2imag ADD minvalue INT4 NULL;
ALTER TABLE public.iyun_product_listprice2imag ADD step INT4 NULL;

/** 去除扣费流水账单里面的单价字段 */
ALTER TABLE public.iyun_instance_bilings DROP price;

/** 修改存储模块表名 */
ALTER TABLE public.cmdb_storage_groups
  RENAME TO cmdb_storagedevice_groups;
ALTER TABLE public.cmdb_storage_clusters
  RENAME TO cmdb_storagedevice_clusters;
ALTER TABLE public.cmdb_storage_groups2items
  RENAME TO cmdb_storagedevice_group2Items;
ALTER TABLE public.cmdb_storage_volums
  RENAME TO cmdb_storagedevice_volums;

/** 创建存储文件系统表和共享存储挂载主机关系表 */
DROP TABLE IF EXISTS cmdb_storagedevice_assageHosts;
DROP TABLE IF EXISTS cmdb_storagedevice_Filesystems;

/*==============================================================*/
/* Table: cmdb_storagedevice_Filesystems                        */
/*==============================================================*/
CREATE TABLE cmdb_storagedevice_Filesystems (
  id          VARCHAR(36)  NOT NULL,
  displayName VARCHAR(100) NOT NULL,
  path        VARCHAR(100) NOT NULL,
  fsType      VARCHAR(50)  NULL,
  lunId       VARCHAR(36)  NOT NULL,
  maxSize     INT8         NULL,
  freeSize    INT8         NULL,
  usage       FLOAT8       NOT NULL,
  unit        VARCHAR(50)  NULL,
  hostId      VARCHAR(36)  NULL,
  clousterId  VARCHAR(36)  NULL,
  GroupID     VARCHAR(36)  NOT NULL,
  CONSTRAINT PK_CMDB_STORAGEDEVICE_FILESYST PRIMARY KEY (id)
);

ALTER TABLE cmdb_storagedevice_Filesystems
  ADD CONSTRAINT FK_CMDB_STO_REFERENCE_CMDB_STO FOREIGN KEY (lunId)
REFERENCES cmdb_storagedevice_volums (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

/*==============================================================*/
/* Table: cmdb_storagedevice_assageHosts                        */
/*==============================================================*/
CREATE TABLE cmdb_storagedevice_assageHosts (
  id           VARCHAR(36)  NOT NULL,
  filesystemId VARCHAR(36)  NULL,
  hostid       VARCHAR(36)  NULL,
  IQN          VARCHAR(100) NULL,
  VIP          VARCHAR(36)  NULL,
  targetName   VARCHAR(100) NULL,
  wwn          VARCHAR(100) NULL,
  CONSTRAINT PK_CMDB_STORAGEDEVICE_ASSAGEHO PRIMARY KEY (id)
);

ALTER TABLE cmdb_storagedevice_assageHosts
  ADD CONSTRAINT FK_CMDB_STO_REFERENCE_CMDB_STO FOREIGN KEY (filesystemId)
REFERENCES cmdb_storagedevice_Filesystems (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

/** 新建网络配置模块表 */
DROP TABLE IF EXISTS cmdb_netdevice_group2Items;
DROP TABLE IF EXISTS cmdb_netdevice_vdevices;
DROP TABLE IF EXISTS cmdb_netdevice_vPorts;
DROP TABLE IF EXISTS cmdb_netdevice_groups;

/*==============================================================*/
/* Table: cmdb_netdevice_groups                                 */
/*==============================================================*/
CREATE TABLE cmdb_netdevice_groups (
  id          VARCHAR(36)  NOT NULL,
  stackName   VARCHAR(100) NULL,
  IP          VARCHAR(36)  NULL,
  Remark      VARCHAR(500) NULL,
  assType     VARCHAR(36)  NULL,
  GroupID     VARCHAR(36)  NOT NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_CMDB_NETDEVICE_GROUPS PRIMARY KEY (id)
);

ALTER TABLE cmdb_netdevice_groups
  ADD CONSTRAINT FK_CMDB_NET_REFERENCE_CMDB_ASM FOREIGN KEY (assType)
REFERENCES iyun_base_initcode (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

/*==============================================================*/
/* Table: cmdb_netdevice_vPorts                                 */
/*==============================================================*/
CREATE TABLE cmdb_netdevice_vPorts (
  id          VARCHAR(36)  NOT NULL,
  stackid     VARCHAR(50)  NULL,
  seq         INT2         NULL,
  MAC         VARCHAR(50)  NULL,
  portType    VARCHAR(50)  NULL,
  macs        TEXT         NULL,
  Remark      VARCHAR(600) NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_CMDB_NETDEVICE_VPORTS PRIMARY KEY (id)
);

ALTER TABLE cmdb_netdevice_vPorts
  ADD CONSTRAINT FK_CMDB_NET_REFERENCE_CMDB_NET FOREIGN KEY (stackid)
REFERENCES cmdb_netdevice_groups (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

/*==============================================================*/
/* Table: cmdb_netdevice_vdevices                               */
/*==============================================================*/
CREATE TABLE cmdb_netdevice_vdevices (
  id          VARCHAR(36)  NOT NULL,
  stackid     VARCHAR(50)  NULL,
  vassetName  VARCHAR(36)  NOT NULL,
  ncore       INT4         NULL,
  MemTotal    INT4         NULL,
  IP          VARCHAR(36)  NULL,
  Mac         VARCHAR(36)  NULL,
  utility     VARCHAR(500) NULL,
  masterId    VARCHAR(50)  NULL,
  Remark      VARCHAR(500) NULL,
  tenant      VARCHAR(36)  NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_CMDB_NETDEVICE_VDEVICES PRIMARY KEY (id)
);

ALTER TABLE cmdb_netdevice_vdevices
  ADD CONSTRAINT FK_CMDB_NET_REFERENCE_CMDB_NET FOREIGN KEY (stackid)
REFERENCES cmdb_netdevice_groups (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

/*==============================================================*/
/* Table: cmdb_netdevice_group2Items                            */
/*==============================================================*/
CREATE TABLE cmdb_netdevice_group2Items (
  id          VARCHAR(36)  NOT NULL,
  stackid     VARCHAR(50)  NULL,
  seriNo      VARCHAR(36)  NULL,
  ncore       INT4         NULL,
  MemTotal    INT4         NULL,
  masterId    VARCHAR(50)  NULL,
  Remark      VARCHAR(500) NULL,
  CreatedBy   VARCHAR(36)  NOT NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedBy   VARCHAR(36)  NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_CMDB_NETDEVICE_GROUP2ITEMS PRIMARY KEY (id)
);

ALTER TABLE cmdb_netdevice_group2Items
  ADD CONSTRAINT FK_CMDB_NET_REFERENCE_CMDB_NET FOREIGN KEY (stackid)
REFERENCES cmdb_netdevice_groups (id)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE cmdb_netdevice_group2Items
  ADD CONSTRAINT FK_CMDB_NET_REFERENCE_CMDB_ASM FOREIGN KEY (masterId)
REFERENCES cmdb_asm_master (ID)
ON DELETE RESTRICT ON UPDATE RESTRICT;

/** 新增网络流量计量表 */
DROP TABLE IF EXISTS iyun_netflow_originals2day;
DROP TABLE IF EXISTS iyun_netflow_originals;

/*==============================================================*/
/* Table: iyun_netflow_originals                                */
/*==============================================================*/
CREATE TABLE iyun_netflow_originals (
  id          VARCHAR(36)  NOT NULL,
  collecttime TIMESTAMP    NOT NULL,
  vassetId    VARCHAR(36)  NOT NULL,
  inputs      INT8         NOT NULL,
  outputs     INT8         NOT NULL,
  uuid        VARCHAR(36)  NOT NULL,
  tenantid    VARCHAR(255) NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IYUN_NETFLOW_ORIGINALS PRIMARY KEY (id)
);

/*==============================================================*/
/* Table: iyun_netflow_originals2day                            */
/*==============================================================*/
CREATE TABLE iyun_netflow_originals2day (
  id          VARCHAR(36)  NOT NULL,
  collecttime TIMESTAMP    NOT NULL,
  vassetId    VARCHAR(36)  NOT NULL,
  inputs      INT8         NOT NULL,
  outputs     INT8         NOT NULL,
  uuid        VARCHAR(36)  NOT NULL,
  tenantid    VARCHAR(255) NULL,
  CreatedDate TIMESTAMP    NOT NULL,
  UpdatedDate TIMESTAMP    NOT NULL,
  CONSTRAINT PK_IYUN_NETFLOW_ORIGINALS2DAY PRIMARY KEY (id)
);


