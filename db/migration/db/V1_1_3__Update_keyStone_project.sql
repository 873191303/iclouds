ALTER TABLE public.iyun_sm_project RENAME TO iyun_keystone_project;
ALTER TABLE public.iyun_keystone_project ADD cusid VARCHAR(50) NULL;
ALTER TABLE public.iyun_keystone_project ADD CONSTRAINT cusid_fk FOREIGN KEY (cusid) REFERENCES bus_cis_custom (id);