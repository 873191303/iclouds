/**

时间：	    2017年7月4日

说明： 	  监控模块表格修改

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/
ALTER TABLE public.ipm_pft_hosts
  ALTER COLUMN owner DROP NOT NULL;
ALTER TABLE public.ipm_pft_hosts
  ALTER COLUMN tenantid DROP NOT NULL;
ALTER TABLE public.ipm_pft_items
  ADD CONSTRAINT ipm_pft_items_ipm_pft_templates_templateid_fk
FOREIGN KEY (templateid) REFERENCES ipm_pft_templates (templateid);
ALTER TABLE public.ipm_pft_items
  ALTER COLUMN hostid DROP NOT NULL;
ALTER TABLE public.ipm_pft_items
  ALTER COLUMN templateid DROP NOT NULL;
ALTER TABLE public.ipm_pft_items
  ALTER COLUMN snmp_oid DROP NOT NULL;
ALTER TABLE public.ipm_pft_users
  ALTER COLUMN surname DROP NOT NULL;
ALTER TABLE public.ipm_pft_users
  ALTER COLUMN url DROP NOT NULL;
ALTER TABLE public.ipm_pft_triggers
  ALTER COLUMN url DROP NOT NULL;
ALTER TABLE public.ipm_pft_triggers
  ALTER COLUMN comments DROP NOT NULL;
ALTER TABLE public.ipm_pft_triggers
  ALTER COLUMN error DROP NOT NULL;
ALTER TABLE public.ipm_pft_triggers
  ALTER COLUMN type DROP NOT NULL;
ALTER TABLE public.ipm_pft_triggers
  ALTER COLUMN state DROP NOT NULL;
ALTER TABLE public.ipm_pft_triggers
  ALTER COLUMN flags DROP NOT NULL;
ALTER TABLE public.ipm_pft_functions
  ALTER COLUMN parameter DROP NOT NULL;