
/**
时间：       2017年1月14日

说明：     应用配置

当前版本： ICloudsV3.4.pdm
*/
ALTER TABLE public.cmdb_app_views ADD lock bool not NULL;
ALTER TABLE public.cmdb_app_views ADD userId varchar(36) null;
ALTER TABLE public.cmdb_app_views ADD sessionId VARCHAR(64) null;
ALTER TABLE public.cmdb_app_views ADD version varchar(36) null;