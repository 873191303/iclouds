
/**

序号：	    19

文件：	    V1_2_8__Add_cloudosId_column.sql

时间：	    2016年12月10日

说明： 	  增加cloudos回传的id,增加任务类型表

影响对象： vdc相关资源

前置版本：	ICloudsV2.8.pdm

当前版本：	ICloudsV2.8.pdm

 */

ALTER TABLE iyun_vdc_firewall ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_fw_policie ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_policie_rule ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_route ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_network ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_subnet ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_ports ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_securitygroups ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vdc_vlb ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vlb_healthmonitors ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vlb_members ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vlb_pools ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_vlb_vips ADD cloudos_id VARCHAR(50) NULL;
ALTER TABLE iyun_securitygroups_rule ADD cloudos_id VARCHAR(50) NULL;

/*==============================================================*/
/* Table: iyun_base_taskType                                    */
/*==============================================================*/
create table iyun_base_taskType (
  id                   varchar(36)          not null,
  name                 varchar(36)          not null,
  retrys               INT2                 null,
  status               char(1)              not null,
  CreatedDate          TIMESTAMP            not null,
  constraint PK_IYUN_BASE_TASKTYPE primary key (id)
);


/*==============================================================*/
/* Table: iyun_vdc_subnet2DNSServer                             */
/*==============================================================*/
create table iyun_vdc_subnet2DNSServer (
  address              varchar(128)         not null,
  subnet_id            varchar(36)          not null,
  constraint PK_IYUN_VDC_SUBNET2DNSSERVER primary key (address, subnet_id)
);

/*==============================================================*/
/* Table: iyun_vdc_subnet2route                                 */
/*==============================================================*/
create table iyun_vdc_subnet2route (
  nexthop              VARCHAR(64)          not null,
  destination          VARCHAR(64)          not null,
  subnet_id            varchar(36)          not null,
  constraint PK_IYUN_VDC_SUBNET2ROUTE primary key (nexthop, destination, subnet_id)
);

alter table iyun_vdc_subnet2route
  add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC1 foreign key (subnet_id)
references iyun_vdc_subnet (id)
on delete restrict on update restrict;

alter table iyun_vdc_subnet2DNSServer
  add constraint FK_IYUN_VDC_REFERENCE_IYUN_VDC2 foreign key (subnet_id)
references iyun_vdc_subnet (id)
on delete restrict on update restrict;

