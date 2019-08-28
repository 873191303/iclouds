
/**

时间：	   2017年3月10日

说明： 	 存储相关表格增加与hp关联id

*/

ALTER TABLE public.cmdb_storage_volums ALTER COLUMN id TYPE VARCHAR(100) USING id::VARCHAR(100);
ALTER TABLE public.cmdb_storage_volums ALTER COLUMN iqn TYPE VARCHAR(100) USING iqn::VARCHAR(100);