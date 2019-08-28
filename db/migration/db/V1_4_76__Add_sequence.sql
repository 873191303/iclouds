/**

时间：	    2017年7月10日

说明： 	  将user2group的tenantid字段移至usergroup

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/

ALTER TABLE public.ipm_pft_users2groups DROP tenantid;

ALTER TABLE public.ipm_pft_usrgrp ADD tenantid VARCHAR(50) NULL;

CREATE SEQUENCE PUBLIC.ipm_pft_users2groups_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;

ALTER TABLE PUBLIC.ipm_pft_users2groups
  ALTER COLUMN ID
  SET DEFAULT nextval(
    'public.ipm_pft_users2groups_id_seq'
);

ALTER SEQUENCE PUBLIC.ipm_pft_users2groups_id_seq OWNED BY PUBLIC.ipm_pft_users2groups.ID;

CREATE SEQUENCE PUBLIC.ipm_pft_host2group_hostgroupid_seq NO MINVALUE NO MAXVALUE NO CYCLE;

ALTER TABLE PUBLIC.ipm_pft_host2group
  ALTER COLUMN hostgroupid
  SET DEFAULT nextval(
    'public.ipm_pft_host2group_hostgroupid_seq'
);

ALTER SEQUENCE PUBLIC.ipm_pft_host2group_hostgroupid_seq OWNED BY PUBLIC.ipm_pft_host2group.hostgroupid;

CREATE SEQUENCE PUBLIC.ipm_pft_host2template_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;

ALTER TABLE PUBLIC.ipm_pft_host2template
  ALTER COLUMN ID
  SET DEFAULT nextval(
    'public.ipm_pft_host2template_id_seq'
);

ALTER SEQUENCE PUBLIC.ipm_pft_host2template_id_seq OWNED BY PUBLIC.ipm_pft_host2template.ID;