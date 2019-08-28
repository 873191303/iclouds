alter table public.iyun_product_specs2key add "key" varchar(36);
alter table public.iyun_product_specs2key add datatype varchar(1);
alter table public.iyun_product_specs2key add validate varchar(200);
alter table public.iyun_product_specs2key add isshow varchar(1);
alter table public.iyun_product_specs2key add ismust varchar(1);
alter table public.iyun_product_specs2key alter column unit drop not null;
alter table public.iyun_product_specs2key alter column createdby drop not null;
alter table public.iyun_product_specs2key alter column createddate drop not null;
alter table public.iyun_product_specs2key alter column updatedby drop not null;
alter table public.iyun_product_specs2key alter column updateddate  drop not null;

insert into public.iyun_product_specs2key(id,classid,"key",keyname,unit,seq,isshow,ismust,validate,datatype) 
  select id,classID,"key",keyname,units,orderby,isshow,ismust,validate,datatype from public.iyun_base_prd2templates;