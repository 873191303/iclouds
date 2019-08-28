
/*==============================================================*/
/* Table: iyun_quota_tenant                                     */
/*==============================================================*/
create table iyun_quota_tenant
(
   ID                   varchar(36)                    not null,
   tenant_id            VARCHAR(64)                    null,
   class_id             varchar(36)                    null,
   class_code           varchar(36)                    null,
   hard_limit           int4                           null,
   Created_at           timestamp                      null,
   Updated_at           timestamp                      null,
   deleted_at           timestamp                      null,
   deleted              int4                           null,
   constraint PK_IYUN_QUOTA_TENANT primary key (ID)
);

/*==============================================================*/
/* Table: iyun_quota_tenant_network                             */
/*==============================================================*/
create table iyun_quota_tenant_network
(
   ID                   varchar(36)                    not null,
   tenant_id            VARCHAR(64)                    null,
   cidr                 varchar(36)                    null,
   Created_at           timestamp                      null,
   Updated_at           timestamp                      null,
   deleted_at           timestamp                      null,
   deleted              int4                           null,
   constraint PK_IYUN_QUOTA_TENANT_NETWORK primary key (ID)
);

/*==============================================================*/
/* Table: iyun_quota_classes                                    */
/*==============================================================*/
create table iyun_quota_classes
(
   ID                   varchar(36)                    not null,
   class_code           varchar(64)                    null,
   class_name           varchar(64)                    null,
   class_type           varchar(2)                     null,
   deleted              int4                           null,
   create_at            timestamp                      null,
   unit                 varchar(8)                     null,
   constraint PK_IYUN_QUOTA_CLASSES primary key (ID)
);

/*==============================================================*/
/* Table: iyun_quota_tenant_used                                */
/*==============================================================*/
create table iyun_quota_tenant_used
(
   ID                   varchar(36)                    not null,
   tenant_id            VARCHAR(64)                    null,
   class_id             varchar(36)                    null,
   class_code           varchar(36)                    null,
   quota_used           int4                           null,
   Created_at           timestamp                      null,
   Updated_at           timestamp                      null,
   Delete_at            timestamp                      null,
   deleted              int4                           null,
   constraint PK_IYUN_QUOTA_TENANT_USED primary key (ID)
);

/*==============================================================*/
/* Table: iyun_quota_tenant_subnet                              */
/*==============================================================*/
create table iyun_quota_tenant_subnet
(
   ID                   varchar(36)                    not null,
   network_id           varchar(36)                    null,
   start_ip             varchar(36)                    null,
   end_ip               varchar(36)                    null,
   Created_at           timestamp                      null,
   Updated_at           timestamp                      null,
   deleted_at           timestamp                      null,
   deleted              int4                           null,
   constraint PK_IYUN_QUOTA_TENANT_SUBNET primary key (ID)
);

/*==============================================================*/
/* Table: iyun_quota_tenant                                     */
/*==============================================================*/

alter table iyun_quota_tenant
   add constraint FK_IYUN_QUO_REFERENCE_IYUN_KEY53 foreign key (tenant_id)
references iyun_keystone_project (id)
on update restrict
on delete restrict;

alter table iyun_quota_tenant
   add constraint FK_IYUN_QUO_REFERENCE_IYUN_QUO54 foreign key (class_id)
references iyun_quota_classes (ID)
on update restrict
on delete restrict;

/*==============================================================*/
/* Table: iyun_quota_tenant_network                             */
/*==============================================================*/

alter table iyun_quota_tenant_network
   add constraint FK_IYUN_QUO_REFERENCE_IYUN_KEY55 foreign key (tenant_id)
references iyun_keystone_project (id)
on update restrict
on delete restrict;

/*==============================================================*/
/* Table: iyun_quota_tenant_used                                */
/*==============================================================*/

alter table iyun_quota_tenant_used
   add constraint FK_IYUN_QUO_REFERENCE_IYUN_KEY56 foreign key (tenant_id)
references iyun_keystone_project (id)
on update restrict
on delete restrict;

alter table iyun_quota_tenant_used
   add constraint FK_IYUN_QUO_REFERENCE_IYUN_QUO57 foreign key (class_id)
references iyun_quota_classes (ID)
on update restrict
on delete restrict;
/*==============================================================*/
/* Table: iyun_quota_tenant_subnet                              */
/*==============================================================*/

alter table iyun_quota_tenant_subnet
   add constraint FK_IYUN_QUO_REFERENCE_IYUN_QUO58 foreign key (network_id)
references iyun_quota_tenant_network (ID)
on update restrict
on delete restrict;

-- ----------------------------
-- Records of iyun_quota_classes
-- ----------------------------
INSERT INTO "public"."iyun_quota_classes" VALUES ('1', 'cores', 'CPU数量', '01', null, '2016-11-22 14:41:34', '核');
INSERT INTO "public"."iyun_quota_classes" VALUES ('10', 'firewall', '防火墙数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('11', 'ips', '网卡数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('12', 'loadbalancer', '负载均衡数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('13', 'floatingip', '公网IP数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('14', 'listener', '监听器数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('15', 'security_group', '安全组数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('16', 'ipsecpolicy', 'IPsec策略数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('17', 'security_group_rule', '安全组规则数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('2', 'instances', '主机数量', '01', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('3', 'ram', '内存容量', '01', null, '2016-11-22 14:41:34', 'GB');
INSERT INTO "public"."iyun_quota_classes" VALUES ('4', 'gigabytes', '存储总容量', '02', null, '2016-11-22 14:41:34', 'GB');
INSERT INTO "public"."iyun_quota_classes" VALUES ('5', 'snapshots', '快照数量', '02', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('6', 'volumes', '硬盘数量', '02', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('7', 'router', '路由器数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('8', 'vpnservice', 'VPN数量', '03', null, '2016-11-22 14:41:34', '个');
INSERT INTO "public"."iyun_quota_classes" VALUES ('9', 'network', '网络数量', '03', null, '2016-11-22 14:41:34', '个');