
/**

	序号：	8

	文件：	V1_1_7__Add_approveLog_column.sql

	时间：	2016年11月26日

	说明：	添加字段

影响对象：	bus_req_master2approveLog

前置版本：	ICloudsV2.3.pdm

当前版本：	ICloudsV2.3.pdm
 */
ALTER TABLE public.bus_req_master2approvelog ADD attachment VARCHAR(100) NULL;
ALTER TABLE public.bus_req_master2approvelog ADD emails VARCHAR(200) NULL;