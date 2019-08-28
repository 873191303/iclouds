ALTER TABLE public.ipm_pft_applications ALTER COLUMN tenantid DROP NOT NULL;
ALTER TABLE public.ipm_pft_applications ALTER COLUMN owner DROP NOT NULL;

ALTER TABLE public.cmdb_asm_master ADD tenantid VARCHAR(100) DEFAULT '81cc455b2e9a4af3b6e74509390069b9' NOT NULL;
ALTER TABLE public.cmdb_asm_master
ADD CONSTRAINT cmdb_asm_master_iyun_keystone_project_id_fk
FOREIGN KEY (tenantid) REFERENCES iyun_keystone_project (id);

ALTER TABLE public.ipm_pft_items ADD isimport BOOLEAN DEFAULT FALSE NOT NULL;