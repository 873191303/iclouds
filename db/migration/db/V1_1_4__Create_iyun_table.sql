/*==============================================================*/
/* Table: iyun_base_vdc                                         */
/*==============================================================*/
create table iyun_base_vdc (
   id                   varchar(32)          not null,
   name                 varchar(100)         not null,
   description          varchar(250)         null,
   sequence             INT2                 null,
   vdctype              VARCHAR(64)          null,
   projectid            VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_BASE_VDC primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_fireware                                     */
/*==============================================================*/
create table iyun_vdc_fireware (
   id                   VARCHAR(32)          not null,
   name                 VARCHAR(50)          null,
   description          VARCHAR(1024)        null,
   shared               bool                 null,
   tenant_id            VARCHAR(50)          null,
   policyid             VARCHAR(50)          null,
   admin_state_up       bool                 null,
   status               VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_FIREWARE primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_fw2policie                                   */
/*==============================================================*/
create table iyun_vdc_fw2policie (
   id                   varchar(36)          not null,
   firewareId           varchar(36)          null,
   policieId            varchar(36)          null,
   sequence             INT2                 null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_FW2POLICIE primary key (id)
);

/*==============================================================*/
/* Table: iyun_fw_policie                                       */
/*==============================================================*/
create table iyun_fw_policie (
   id                   VARCHAR(50)          not null,
   name                 VARCHAR(50)          null,
   description          VARCHAR(50)          null,
   tenant_id            VARCHAR(50)          null,
   status               VARCHAR(50)          null,
   shared               bool                 null,
   audited              bool                 null,
   deleted              varchar(32)          null,
   deleteBy             VARCHAR(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_FW_POLICIE primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_vdc2fw                                       */
/*==============================================================*/
create table iyun_vdc_vdc2fw (
   ID                   VARCHAR(32)          not null,
   vdcId                VARCHAR(32)          null,
   fireware             VARCHAR(32)          null,
   throughput           INT8                 null,
   sequence             INT2                 null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_VDC2FW primary key (ID)
);

/*==============================================================*/
/* Table: iyun_policie_rule                                     */
/*==============================================================*/
create table iyun_policie_rule (
   id                   VARCHAR(36)          not null,
   name                 VARCHAR(255)         null,
   description          VARCHAR(1024)        null,
   shared               BOOL                 null,
   protocol             VARCHAR(40)          null,
   action               VARCHAR(50)          null,
   ip_version           INT4                 not null,
   source_ip            VARCHAR(50)          null,
   source_port_range_min INT4                 null,
   source_port_range_max INT4                 null,
   destination_ip       VARCHAR(50)          null,
   destination_port_range_min INT4                 null,
   destination_port_range_max INT4                 null,
   enabled              BOOL                 null,
   policyid             VARCHAR(36)          null,
   tenantid             VARCHAR(50)          null,
   "position"           VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_POLICIE_RULE primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_view                                         */
/*==============================================================*/
create table iyun_vdc_view (
   id                   varchar(32)          not null,
   vdcId                varchar(36)          null,
   next                 varchar(36)          not null,
   sequence             INT4                 null,
   constraint PK_IYUN_VDC_VIEW primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_items                                        */
/*==============================================================*/
create table iyun_vdc_items (
   id                   varchar(36)          not null,
   UUID                 varchar(36)          not null,
   option               text                 not null,
   itemtype             varchar(36)          not null,
   constraint PK_IYUN_VDC_ITEMS primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_securitygroups                               */
/*==============================================================*/
create table iyun_vdc_securitygroups (
   id                   varchar(36)          not null,
   name                 VARCHAR(255)         null,
   description          VARCHAR(1024)        null,
   tenantid             VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_SECURITYGROUPS primary key (id)
);

/*==============================================================*/
/* Table: iyun_tenant_defaultsgroup                             */
/*==============================================================*/
create table iyun_tenant_defaultsgroup (
   tenantid             VARCHAR(50)          not null,
   security_group_od    VARCHAR(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_TENANT_DEFAULTSGROUP primary key (tenantid)
);

/*==============================================================*/
/* Table: iyun_securitygroups_rule                              */
/*==============================================================*/
create table iyun_securitygroups_rule (
   id                   VARCHAR(36)          not null,
   security_group_od    VARCHAR(36)          null,
   remote_group_id      VARCHAR(36)          null,
   direction            VARCHAR(50)          null,
   ethertype            VARCHAR(40)          not null,
   protocol             VARCHAR(40)          null,
   port_range_min       INT4                 null,
   port_range_max       INT4                 null,
   remote_ip_prefix     VARCHAR(250)         null,
   tenantid             VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_SECURITYGROUPS_RULE primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_sgroup2port                                  */
/*==============================================================*/
create table iyun_vdc_sgroup2port (
   port_id              VARCHAR(36)          not null,
   security_group_od    VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_SGROUP2PORT primary key (port_id, security_group_od)
);

/*==============================================================*/
/* Table: iyun_vdc_vlb                                          */
/*==============================================================*/
create table iyun_vdc_vlb (
   id                   varchar(36)          not null,
   name                 VARCHAR(50)          null,
   description          VARCHAR(500)         null,
   status               INT4                 null,
   through_put          INT4                 null,
   extra                varchar(2048)        null,
   projectid            VARCHAR(64)          null,
   owner                VARCHAR(36)          null,
   deleted              varchar(32)          null,
   deleteBy             VARCHAR(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_VLB primary key (id)
);

/*==============================================================*/
/* Table: iyun_vlb_vips                                         */
/*==============================================================*/
create table iyun_vlb_vips (
   id                   varchar(36)          not null,
   name                 varchar(255)         null,
   description          varchar(255)         null,
   status               VARCHAR(16)          not null,
   status_descrition    varchar(255)         null,
   protocol_port        INT4                 null,
   protocol             varchar(255)         not null,
   port_id              varchar(36)          not null,
   admin_state_up       bool                 not null,
   commection_limit     INT4                 not null,
   pool_id              varchar(36)          not null,
   tenant_id            VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VLB_VIPS primary key (id)
);

/*==============================================================*/
/* Table: iyun_vlb_healthmonitors                               */
/*==============================================================*/
create table iyun_vlb_healthmonitors (
   id                   varchar(36)          not null,
   type                 varchar(255)         null,
   delay                INT4                 not null,
   timeout              INT4                 not null,
   max_retries          INT4                 not null,
   http_method          varchar(16)          not null,
   url_path             varchar(255)         null,
   expected_codes       varchar(64)          null,
   admin_state_up       bool                 not null,
   tenant_id            VARCHAR(50)          null,
   lbid                 varchar(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VLB_HEALTHMONITORS primary key (id)
);

/*==============================================================*/
/* Table: iyun_vlb_pool2healthmonitors                          */
/*==============================================================*/
create table iyun_vlb_pool2healthmonitors (
   hmonitorId           varchar(36)          not null,
   poolid               varchar(36)          not null,
   status               VARCHAR(16)          not null,
   status_descrition    varchar(255)         not null,
   constraint PK_IYUN_VLB_POOL2HEALTHMONITOR primary key (hmonitorId, poolid)
);

/*==============================================================*/
/* Table: iyun_vlb_members                                      */
/*==============================================================*/
create table iyun_vlb_members (
   id                   VARCHAR(36)          not null,
   status               VARCHAR(16)          not null,
   status_descrition    varchar(255)         null,
   pool_id              varchar(36)          not null,
   address              varchar(64)          not null,
   protocol_port        INT4                 not null,
   weight               INT4                 not null,
   admin_state_up       bool                 not null,
   tenant_id            VARCHAR(50)          null,
   type                 varchar(255)         not null,
   cookie_name          varchar(1024)        null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VLB_MEMBERS primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_ports                                        */
/*==============================================================*/
create table iyun_vdc_ports (
   id                   VARCHAR(36)          not null,
   name                 VARCHAR(255)         null,
   network_id           VARCHAR(36)          not null,
   mac_address          VARCHAR(32)          not null,
   admin_state_up       bool                 not null,
   status               VARCHAR(16)          not null,
   device_id            VARCHAR(255)         not null,
   device_owner         VARCHAR(255)         not null,
   tenant_id            VARCHAR(255)         null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_PORTS primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_ipallocations                                */
/*==============================================================*/
create table iyun_vdc_ipallocations (
   port_id              varchar(36)          not null,
   ip_address           varchar(64)          not null,
   subnet_id            varchar(36)          not null,
   network_id           varchar(36)          not null,
   constraint PK_IYUN_VDC_IPALLOCATIONS primary key (port_id, subnet_id, network_id)
);

/*==============================================================*/
/* Table: iyun_vdc_subnet                                       */
/*==============================================================*/
create table iyun_vdc_subnet (
   id                   varchar(36)          not null,
   network_id           VARCHAR(36)          null,
   name                 VARCHAR(255)         null,
   ip_version           INT4                 not null,
   cidr                 VARCHAR(64)          not null,
   gateway_ip           VARCHAR(64)          null,
   enable_dhcp          bool                 null,
   shared               bool                 null,
   ipv6_ra_mode         varchar(64)          null,
   ipv6_address_mode    varchar(64)          null,
   subnetpool_id        varchar(36)          null,
   tenant_id            VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_SUBNET primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_route                                        */
/*==============================================================*/
create table iyun_vdc_route (
   id                   varchar(32)          not null,
   name                 VARCHAR(50)          null,
   status               VARCHAR(50)          null,
   admin_state_up       bool                 null,
   gw_port_id           varchar(32)          null,
   enable_snat          bool                 not null,
   tenant_id            VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_ROUTE primary key (id)
);

/*==============================================================*/
/* Table: iyun_vdc_route2port                                   */
/*==============================================================*/
create table iyun_vdc_route2port (
   router_id            varchar(32)          not null,
   port_id              VARCHAR(36)          not null,
   port_type            varchar(255)         null,
   constraint PK_IYUN_VDC_ROUTE2PORT primary key (router_id, port_id)
);

/*==============================================================*/
/* Table: iyun_vdc_network                                      */
/*==============================================================*/
create table iyun_vdc_network (
   id                   VARCHAR(36)          not null,
   name                 VARCHAR(50)          null,
   shared               bool                 null,
   tenant_id            VARCHAR(50)          null,
   admin_state_up       bool                 null,
   status               VARCHAR(16)          null,
   mtu                  INT4                 null,
   vlan_transparent     BOOL                 null,
   externalnetworks     bool                 not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VDC_NETWORK primary key (id)
);

/*==============================================================*/
/* Table: iyun_subnet_ipallocationpools                         */
/*==============================================================*/
create table iyun_subnet_ipallocationpools (
   id                   varchar(36)          not null,
   subnet_id            varchar(36)          null,
   first_ip             varchar(64)          not null,
   last_ip              varchar(64)          not null,
   constraint PK_IYUN_SUBNET_IPALLOCATIONPOO primary key (id)
);

/*==============================================================*/
/* Table: iyun_subnet_ipavailabilityrange                       */
/*==============================================================*/
create table iyun_subnet_ipavailabilityrange (
   allocation_pool_id   varchar(36)          not null,
   first_ip             varchar(64)          not null,
   last_ip              varchar(64)          not null,
   constraint PK_IYUN_SUBNET_IPAVAILABILITYR primary key (allocation_pool_id, first_ip, last_ip)
);

/*==============================================================*/
/* Table: iyun_nova_vm                                          */
/*==============================================================*/
create table iyun_nova_vm (
   ID                   VARCHAR(36)          not null,
   UUID                 VARCHAR(36)          not null,
   host                 VARCHAR(255)         not null,
   hostName             VARCHAR(255)         null,
   vcpus                INT4                 null,
   memory_mb            INT4                 null,
   ramdisk_mb           INT4                 null,
   vmstate              VARCHAR(255)         null,
   powerstate           INT4                 null,
   flavorid             varchar(50)          null,
   ostype               VARCHAR(64)          null,
   image_ref            VARCHAR(255)         null,
   azoneID              varchar(128)         null,
   projectid            VARCHAR(64)          null,
   owner                VARCHAR(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_NOVA_VM primary key (ID)
);

/*==============================================================*/
/* Table: iyun_vlb_pools                                        */
/*==============================================================*/
create table iyun_vlb_pools (
   id                   varchar(36)          not null,
   name                 varchar(255)         null,
   description          varchar(255)         null,
   protocol             varchar(255)         not null,
   lb_method            varchar(255)         not null,
   subnet_id            varchar(255)         not null,
   vip_id               varchar(255)         null,
   admin_state_up       bool                 not null,
   status               VARCHAR(16)          not null,
   status_descrition    varchar(255)         null,
   lbid                 varchar(36)          null,
   tenant_id            VARCHAR(50)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VLB_POOLS primary key (id)
);

/*==============================================================*/
/* Table: bus_req_item2exec                                     */
/*==============================================================*/
create table bus_req_item2exec (
   id                   VARCHAR(36)          not null,
   itemId               VARCHAR(36)          null,
   classID              VARCHAR(36)          null,
   UUID                 VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_BUS_REQ_ITEM2EXEC primary key (id)
);

/*==============================================================*/
/* Table: iyun_keystone_azone                                   */
/*==============================================================*/
create table iyun_keystone_azone (
   UUID                 varchar(128)         not null,
   lablename            varchar(128)         not null,
   description          varchar(128)         null,
   zone                 varchar(32)          not null,
   virt_type            varchar(32)          null,
   resource_type        varchar(32)          not null,
   deleted              varchar(32)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_KEYSTONE_AZONE primary key (UUID)
);

/*==============================================================*/
/* Table: iyun_measure_units                                    */
/*==============================================================*/
create table iyun_measure_units (
   Id                   varchar(36)          not null,
   code                 VARCHAR(36)          not null,
   name                 varchar(100)         not null,
   minValue             INT4                 null,
   maxValue             INT4                 null,
   "default"            INT4                 null,
   unit                 varchar(100)         not null,
   descript             varchar(100)         null,
   classid              VARCHAR(36)          not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_MEASURE_UNITS primary key (Id)
);

/*==============================================================*/
/* Table: iyun_measure_specs                                    */
/*==============================================================*/
create table iyun_measure_specs (
   id                   varchar(36)          not null,
   classid              varchar(36)          not null,
   azoneid              varchar(128)         null,
   name                 VARCHAR(100)         not null,
   spec                 text                 not null,
   minValue             INT4                 null,
   maxValue             INT4                 null,
   "default"            INT4                 null,
   unit                 VARCHAR(36)          not null,
   listPrice            DECIMAL(10,2)        null,
   step                 INT4                 null,
   stepprice            DECIMAL(10,2)        null,
   discount             DECIMAL(10,2)        null,
   flavorid             varchar(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_MEASURE_SPECS primary key (id)
);

/*==============================================================*/
/* Table: iyun_measure_instaces                                 */
/*==============================================================*/
create table iyun_measure_instaces (
   ID                   VARCHAR(36)          not null,
   instance             VARCHAR(36)          not null,
   classid              varchar(36)          not null,
   flavor               text                 null,
   begdate              TIMESTAMP            not null,
   enddate              TIMESTAMP            not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_MEASURE_INSTACES primary key (ID)
);

/*==============================================================*/
/* Table: iyun_measure_eventtype                                */
/*==============================================================*/
create table iyun_measure_eventtype (
   id                   varchar(36)          not null,
   name                 varchar(100)         not null,
   classID              VARCHAR(36)          null,
   constraint PK_IYUN_MEASURE_EVENTTYPE primary key (id)
);

/*==============================================================*/
/* Table: iyun_instance_measuredetail                           */
/*==============================================================*/
create table iyun_instance_measuredetail (
   id                   varchar(36)          not null,
   instanceId           VARCHAR(36)          not null,
   specId               VARCHAR(36)          not null,
   num                  int4                 not null,
   price                DECIMAL(10,2)        not null,
   isEffective          BOOL                 not null,
   begdate              TIMESTAMP            not null,
   enddate              TIMESTAMP            not null,
   eventtype            varchar(36)          not null,
   flag                 bool                 not null,
   description          VARCHAR(255)         not null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_INSTANCE_MEASUREDETAIL primary key (id)
);

/*==============================================================*/
/* Table: iyun_measure_instance2specimag                        */
/*==============================================================*/
create table iyun_measure_instance2specimag (
   id                   varchar(36)          not null,
   specid               varchar(36)          not null,
   classid              varchar(36)          not null,
   azoneid              varchar(128)         null,
   name                 VARCHAR(100)         not null,
   spec                 text                 not null,
   minValue             INT4                 null,
   maxValue             INT4                 null,
   "default"            INT4                 null,
   unit                 VARCHAR(36)          not null,
   listPrice            DECIMAL(10,2)        null,
   step                 INT4                 null,
   stepprice            DECIMAL(10,2)        null,
   discount             DECIMAL(10,2)        null,
   flavorid             varchar(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_MEASURE_INSTANCE2SPECI primary key (id)
);

/*==============================================================*/
/* Table: iyun_nova_volumes                                     */
/*==============================================================*/
create table iyun_nova_volumes (
   id                   varchar(36)          not null,
   UUID                 varchar(36)          null,
   diskid               VARCHAR(50)          null,
   name                 VARCHAR(50)          null,
   attach_status        VARCHAR(250)         null,
   volumeType           VARCHAR(50)          null,
   size                 INT8                 null,
   host                 VARCHAR(100)         null,
   mountpoint           VARCHAR(255)         null,
   status               VARCHAR(50)          null,
   snapshot_id          VARCHAR(50)          null,
   source_volid         VARCHAR(50)          null,
   imageRef             VARCHAR(50)          null,
   description          VARCHAR(500)         null,
   metadata             VARCHAR(500)         null,
   azoneID              VARCHAR(50)          null,
   projectid            VARCHAR(64)          null,
   flavorId             varchar(32)          null,
   owner2               VARCHAR(36)          null,
   deleted              varchar(32)          null,
   deleteBy             VARCHAR(36)          null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_NOVA_VOLUMES primary key (id)
);

/*==============================================================*/
/* Table: iyun_storage_flavor                                   */
/*==============================================================*/
create table iyun_storage_flavor (
   UUID                 varchar(32)          not null,
   name                 varchar(100)         not null,
   description          varchar(100)         not null,
   azone_uuid           varchar(64)          not null,
   user_name            varchar(64)          not null,
   location             varchar(64)          not null,
   deleted              varchar(32)          null,
   deleteBy             VARCHAR(36)          null,
   deleteDate           TIMESTAMP            null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_STORAGE_FLAVOR primary key (UUID)
);

/*==============================================================*/
/* Table: iyun_vm_extra                                         */
/*==============================================================*/
create table iyun_vm_extra (
   ID                   VARCHAR(36)          not null,
   sshkey               varchar(50)          null,
   os_user              varchar(50)          null,
   os_passwd            varchar(50)          null,
   constraint PK_IYUN_VM_EXTRA primary key (ID)
);

/*==============================================================*/
/* Table: iyun_nova_flavor                                      */
/*==============================================================*/
create table iyun_nova_flavor (
   id                   varchar(32)          not null,
   name                 varchar(100)         not null,
   vcpus                INT8                 not null,
   ram                  INT8                 not null,
   disk                 INT8                 not null,
   diskType             varchar(100)         not null,
   swap                 INT8                 not null,
   disabled             bool                 null,
   is_public            bool                 not null,
   deleted              varchar(32)          null,
   deleteBy             VARCHAR(36)          null,
   deleteDate           TIMESTAMP            null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_NOVA_FLAVOR primary key (id)
);

/*==============================================================*/
/* Table: iyun_vm_metadata                                      */
/*==============================================================*/
create table iyun_vm_metadata (
   id                   varchar(36)          not null,
   instance_uuid        VARCHAR(36)          null,
   key                  varchar(255)         null,
   value                varchar(255)         null,
   deleted              varchar(32)          null,
   deleteBy             VARCHAR(36)          null,
   deleteDate           TIMESTAMP            null,
   CreatedBy            VARCHAR(36)          not null,
   CreatedDate          TIMESTAMP            not null,
   UpdatedBy            VARCHAR(36)          not null,
   UpdatedDate          TIMESTAMP            not null,
   constraint PK_IYUN_VM_METADATA primary key (id)
);
/*==============================================================*/
/* Table: iyun_keystone_project2azone                           */
/*==============================================================*/
create table iyun_keystone_project2azone (
   UUID                 varchar(64)          not null,
   id                   VARCHAR(64)          null,
   iyu_UUID             varchar(128)         null,
   deleted              varchar(32)          null,
   constraint PK_IYUN_KEYSTONE_PROJECT2AZONE primary key (UUID)
);

/*==============================================================*/
/* Table: iyun_base_vdc                                         */
/*==============================================================*/

alter table iyun_base_vdc
   add constraint FK_IYUN_BAS_REFERENCE_IYUN_KEY1 foreign key (projectid)
references iyun_keystone_project (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vdc_fw2policie                                   */
/*==============================================================*/

alter table iyun_vdc_fw2policie
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC2 foreign key (firewareId)
references iyun_vdc_fireware (id)
on delete restrict on update restrict;

alter table iyun_vdc_fw2policie
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_FW_3 foreign key (policieId)
references iyun_fw_policie (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_vdc_vdc2fw                                       */
/*==============================================================*/

alter table iyun_vdc_vdc2fw
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_BAS4 foreign key (vdcId)
references iyun_base_vdc (id)
on delete restrict on update restrict;

alter table iyun_vdc_vdc2fw
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC5 foreign key (fireware)
references iyun_vdc_fireware (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_policie_rule                                     */
/*==============================================================*/

alter table iyun_policie_rule
   add constraint FK_IYUN_POL_REFERENCE_IYUN_FW_6 foreign key (policyid)
references iyun_fw_policie (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vdc_view                                         */
/*==============================================================*/

alter table iyun_vdc_view
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_BAS7 foreign key (id)
references iyun_base_vdc (id)
on delete restrict on update restrict;

alter table iyun_vdc_view
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC8 foreign key (vdcId)
references iyun_vdc_items (id)
on delete restrict on update restrict;


/*==============================================================*/
/* Table: iyun_tenant_defaultsgroup                             */
/*==============================================================*/

alter table iyun_tenant_defaultsgroup
   add constraint FK_IYUN_TEN_REFERENCE_IYUN_VDC9 foreign key (security_group_od)
references iyun_vdc_securitygroups (id)
on delete restrict on update restrict;


/*==============================================================*/
/* Table: iyun_vdc_sgroup2port                                  */
/*==============================================================*/

alter table iyun_vdc_sgroup2port
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC10 foreign key (port_id)
references iyun_vdc_ports (id)
on delete restrict on update restrict;

alter table iyun_vdc_sgroup2port
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC11 foreign key (security_group_od)
references iyun_vdc_securitygroups (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_securitygroups_rule                              */
/*==============================================================*/

alter table iyun_securitygroups_rule
   add constraint FK_IYUN_SEC_REFERENCE_IYUN_VDC12 foreign key (security_group_od)
references iyun_vdc_securitygroups (id)
on delete restrict on update restrict;

alter table iyun_securitygroups_rule
   add constraint FK_IYUN_SEC_REFERENCE_IYUN_VDC13 foreign key (remote_group_id)
references iyun_vdc_securitygroups (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_vlb_healthmonitors                               */
/*==============================================================*/

alter table iyun_vlb_healthmonitors
   add constraint FK_IYUN_VLB_REFERENCE_IYUN_VDC14 foreign key (lbid)
references iyun_vdc_vlb (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_vlb_pool2healthmonitors                          */
/*==============================================================*/

alter table iyun_vlb_pool2healthmonitors
   add constraint FK_IYUN_VLB_REFERENCE_IYUN_VLB15 foreign key (hmonitorId)
references iyun_vlb_healthmonitors (id)
on delete restrict on update restrict;

alter table iyun_vlb_pool2healthmonitors
   add constraint FK_IYUN_VLB_REFERENCE_IYUN_VLB16 foreign key (poolid)
references iyun_vlb_pools (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_vlb_members                                      */
/*==============================================================*/
alter table iyun_vlb_members
   add constraint FK_IYUN_VLB_REFERENCE_IYUN_VLB17 foreign key (pool_id)
references iyun_vlb_pools (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_vdc_ports                                        */
/*==============================================================*/

alter table iyun_vdc_ports
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC18 foreign key (network_id)
references iyun_vdc_network (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vdc_ipallocations                                */
/*==============================================================*/

alter table iyun_vdc_ipallocations
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC19 foreign key (port_id)
references iyun_vdc_ports (id)
on delete restrict on update restrict;

alter table iyun_vdc_ipallocations
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC20 foreign key (network_id)
references iyun_vdc_network (id)
on delete restrict on update restrict;

alter table iyun_vdc_ipallocations
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC21 foreign key (subnet_id)
references iyun_vdc_subnet (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vdc_subnet                                       */
/*==============================================================*/

alter table iyun_vdc_subnet
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC22 foreign key (network_id)
references iyun_vdc_network (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vdc_route2port                                   */
/*==============================================================*/

alter table iyun_vdc_route2port
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC23 foreign key (router_id)
references iyun_vdc_route (id)
on delete restrict on update restrict;

alter table iyun_vdc_route2port
   add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC24 foreign key (port_id)
references iyun_vdc_ports (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_subnet_ipallocationpools                         */
/*==============================================================*/

alter table iyun_subnet_ipallocationpools
   add constraint FK_IYUN_SUB_REFERENCE_IYUN_VDC25 foreign key (subnet_id)
references iyun_vdc_subnet (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_subnet_ipavailabilityrange                       */
/*==============================================================*/
alter table iyun_subnet_ipavailabilityrange
   add constraint FK_IYUN_SUB_REFERENCE_IYUN_SUB26 foreign key (allocation_pool_id)
references iyun_subnet_ipallocationpools (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_nova_vm                                          */
/*==============================================================*/
alter table iyun_nova_vm
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_KEY27 foreign key (projectid)
references iyun_keystone_project (id)
on delete restrict on update restrict;

alter table iyun_nova_vm
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_NOV28 foreign key (flavorid)
references iyun_nova_flavor (id)
on delete restrict on update restrict;

alter table iyun_nova_vm
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_KEY29 foreign key (azoneID)
references iyun_keystone_azone (UUID)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_vlb_pools                                        */
/*==============================================================*/
alter table iyun_vlb_pools
   add constraint FK_IYUN_VLB_REFERENCE_IYUN_VDC30 foreign key (lbid)
references iyun_vdc_vlb (id)
on delete restrict on update restrict;

alter table iyun_vlb_pools
   add constraint FK_IYUN_VLB_REFERENCE_IYUN_VLB31 foreign key (vip_id)
references iyun_vlb_vips (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: bus_req_items                                         */
/*==============================================================*/
alter table bus_req_items
   add constraint FK_BUS_REQ__REFERENCE_BUS_REQ_32 foreign key (reqID)
references bus_req_master (id)
on delete restrict on update restrict;

alter table bus_req_items
   add constraint FK_BUS_REQ__REFERENCE_IYUN_BAS33 foreign key (classID)
references iyun_base_prdClass (id)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: bus_req_item2exec                                     */
/*==============================================================*/

alter table bus_req_item2exec
   add constraint FK_BUS_REQ__REFERENCE_BUS_REQ_34 foreign key (itemId)
references bus_req_items (id)
on delete restrict on update restrict;

alter table bus_req_item2exec
   add constraint FK_BUS_REQ__REFERENCE_IYUN_BAS35 foreign key (classID)
references iyun_base_prdClass (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_measure_units                                    */
/*==============================================================*/

alter table iyun_measure_units
   add constraint FK_IYUN_MEA_REFERENCE_IYUN_BAS36 foreign key (classid)
references iyun_base_prdClass (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_measure_specs                                    */
/*==============================================================*/

alter table iyun_measure_specs
   add constraint FK_IYUN_MEA_REFERENCE_IYUN_BAS37 foreign key (classid)
references iyun_base_prdClass (id)
on delete restrict on update restrict;

alter table iyun_measure_specs
   add constraint FK_IYUN_MEA_REFERENCE_IYUN_KEY38 foreign key (azoneid)
references iyun_keystone_azone (UUID)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_measure_eventtype                                */
/*==============================================================*/
alter table iyun_measure_eventtype
   add constraint FK_IYUN_MEA_REFERENCE_IYUN_BAS39 foreign key (classID)
references iyun_base_prdClass (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_instance_measuredetail                           */
/*==============================================================*/
alter table iyun_instance_measuredetail
   add constraint FK_IYUN_INS_REFERENCE_IYUN_MEA40 foreign key (instanceId)
references iyun_measure_instaces (ID)
on delete restrict on update restrict;

alter table iyun_instance_measuredetail
   add constraint FK_IYUN_INS_REFERENCE_IYUN_MEA41 foreign key (specId)
references iyun_measure_instance2specimag (id)
on delete restrict on update restrict;

alter table iyun_instance_measuredetail
   add constraint FK_IYUN_INS_REFERENCE_IYUN_MEA42 foreign key (eventtype)
references iyun_measure_eventtype (id)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_nova_volumes                                     */
/*==============================================================*/

alter table iyun_nova_volumes
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_KEY43 foreign key (projectid)
references iyun_keystone_project (id)
on delete restrict on update restrict;

alter table iyun_nova_volumes
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_KEY44 foreign key (azoneID)
references iyun_keystone_azone (UUID)
on delete restrict on update restrict;

alter table iyun_nova_volumes
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_STO45 foreign key (flavorId)
references iyun_storage_flavor (UUID)
on delete restrict on update restrict;

alter table iyun_nova_volumes
   add constraint FK_IYUN_NOV_REFERENCE_IYUN_NOV46 foreign key (host)
references iyun_nova_vm (ID)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_storage_flavor                                   */
/*==============================================================*/

alter table iyun_storage_flavor
   add constraint FK_IYUN_STO_REFERENCE_IYUN_KEY47 foreign key (azone_uuid)
references iyun_keystone_azone (UUID)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vm_extra                                         */
/*==============================================================*/

alter table iyun_vm_extra
   add constraint FK_IYUN_VM__REFERENCE_IYUN_NOV48 foreign key (ID)
references iyun_nova_vm (ID)
on delete restrict on update restrict;

/*==============================================================*/
/* Table: iyun_vm_metadata                                      */
/*==============================================================*/

alter table iyun_vm_metadata
   add constraint FK_IYUN_VM__REFERENCE_IYUN_NOV49 foreign key (instance_uuid)
references iyun_nova_vm (ID)
on delete restrict on update restrict;
/*==============================================================*/
/* Table: iyun_keystone_project2azone                           */
/*==============================================================*/

alter table iyun_keystone_project2azone
   add constraint FK_IYUN_KEY_REFERENCE_IYUN_KEY50 foreign key (id)
references iyun_keystone_project (id)
on delete restrict on update restrict;

alter table iyun_keystone_project2azone
   add constraint FK_IYUN_KEY_REFERENCE_IYUN_KEY51 foreign key (iyu_UUID)
references iyun_keystone_azone (UUID)
on delete restrict on update restrict;



