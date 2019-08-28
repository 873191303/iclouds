


alter table public.cmdb_asm_extAValue add assetID varchar(36) not null;


alter table public.cmdb_asm_extAValue 
     add foreign key (assetID) 
          references cmdb_asm_master(id) 
          on delete restrict on update restrict;