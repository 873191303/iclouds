/**

时间：	    2017年1月5日

说明： 	  	增加公网IP辅助表

前置版本：	ICloudsV2.9.pdm

当前版本：	ICloudsV3.1.pdm
*/
/*==============================================================*/
/* Table: iyun_neutron_floatingips                              */
/*==============================================================*/
create table iyun_neutron_floatingips (
   id                   varchar(36)          not null,
   tenant_id            varchar(64)          null,
   floating_ip_address  varchar(64)          null,
   floating_network_id  varchar(36)          null,
   floating_port_id     varchar(36)          null,
   fixed_port_id        varchar(36)          null,
   fixed_ip_address     varchar(64)          null,
   router_id            varchar(36)          null,
   last_known_router_id varchar(36)          null,
   status               varchar(16)          null,
   name                 varchar(128)         null,
   norm                 varchar(256)         null,
   cloudos_id            varchar(256)         null,
   constraint PK_IYUN_NEUTRON_FLOATINGIPS primary key (id)
);

alter table iyun_neutron_floatingips
   add constraint FK_IYUN_NEU_REFERENCE_IYUN_KEY foreign key (tenant_id)
      references iyun_keystone_project (id)
      on delete restrict on update restrict;

alter table iyun_neutron_floatingips
   add constraint FK_IYUN_NEU_REFERENCE_IYUN_VDC1 foreign key (floating_port_id)
      references iyun_vdc_ports (id)
      on delete restrict on update restrict;

alter table iyun_neutron_floatingips
   add constraint FK_IYUN_NEU_REFERENCE_IYUN_VDC2 foreign key (fixed_port_id)
      references iyun_vdc_ports (id)
      on delete restrict on update restrict;

alter table iyun_neutron_floatingips
   add constraint FK_IYUN_NEU_REFERENCE_IYUN_VDC3 foreign key (router_id)
      references iyun_vdc_route (id)
      on delete restrict on update restrict;
