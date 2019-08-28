/**

时间：	    2017年7月5日

说明： 	  修改监控项templateid外键关联；新增字段外键关联本身

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/
ALTER TABLE public.ipm_pft_items ADD templetid INT8 NULL;
ALTER TABLE public.ipm_pft_items DROP CONSTRAINT ipm_pft_items_ipm_pft_templates_templateid_fk;
ALTER TABLE public.ipm_pft_items
  ADD CONSTRAINT ipm_pft_items_ipm_pft_items_itemid_fk01
FOREIGN KEY (templateid) REFERENCES ipm_pft_items (itemid);
ALTER TABLE public.ipm_pft_items
  ADD CONSTRAINT ipm_pft_items_ipm_pft_templates_templateid_fk02
FOREIGN KEY (templetid) REFERENCES ipm_pft_templates (templateid);
