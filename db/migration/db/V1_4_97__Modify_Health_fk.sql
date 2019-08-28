alter table iyun_sla_instance2originals drop constraint fk_iyun_sla_reference_iyun_hea5;

alter table iyun_sla_instance2originals
   add constraint FK_IYUN_SLA_REFERENCE_IYUN_HEA5 foreign key (itemid)
      references iyun_healthys_type2items (id)
      on delete restrict on update restrict;