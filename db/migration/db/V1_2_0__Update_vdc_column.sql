
/**

序号：	    11

文件：	    V1_2_0__Update_vdc_column.sql

时间：	    2016年11月30日

说明： 	  增删字段

影响对象：	iyun_vdc_view,iyun_vdc_items

前置版本：	ICloudsV2.4.pdm

当前版本：	ICloudsV2.4.pdm
 */
ALTER TABLE public.iyun_vdc_view ADD objid VARCHAR(50) NULL;

ALTER TABLE public.iyun_vdc_view
ADD CONSTRAINT view_items_id_fk01
FOREIGN KEY (objid) REFERENCES iyun_vdc_items (id);

ALTER TABLE public.iyun_vdc_view
ADD CONSTRAINT view_items_id_fk02
FOREIGN KEY (previous) REFERENCES iyun_vdc_items (id);

ALTER TABLE public.iyun_vdc_view DROP CONSTRAINT fk_iyun_vdc_reference_iyun_vdc8;

ALTER TABLE public.iyun_vdc_view
ADD CONSTRAINT reference_vdc803
FOREIGN KEY (vdcid) REFERENCES iyun_base_vdc (id);

ALTER TABLE public.iyun_vdc_view DROP CONSTRAINT fk_iyun_vdc_reference_iyun_bas7;

ALTER TABLE public.iyun_vdc_items RENAME COLUMN option TO option_name;

ALTER TABLE public.iyun_vdc_items ALTER COLUMN option_name TYPE VARCHAR(255) USING option_name::VARCHAR(255);

ALTER TABLE public.iyun_vdc_items DROP uuid;