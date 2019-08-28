
/**

时间：	   2017年2月21日

说明： 	 修改字段

*/
ALTER TABLE public.cmdb_dc_draws ADD isstandard CHAR NULL;

ALTER TABLE public.cmdb_hostpool_items ADD name VARCHAR(50) NOT NULL;

ALTER TABLE public.cmdb_hostpool_relations ALTER COLUMN previous DROP NOT NULL;

ALTER TABLE public.cmdb_hostpool_items ALTER COLUMN option DROP NOT NULL;