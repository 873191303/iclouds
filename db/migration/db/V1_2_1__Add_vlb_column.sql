
/**

序号：	    12

文件：	    V1_2_1__Add_vlb_column.sql

时间：	    2016年11月30日

说明： 	  增加字段

影响对象：	iyun_vdc_vlb

前置版本：	ICloudsV2.4.pdm

当前版本：	ICloudsV2.4.pdm
 */

ALTER TABLE public.iyun_vdc_vlb ADD vdcid VARCHAR(50) NULL;

ALTER TABLE public.iyun_vdc_vlb
ADD CONSTRAINT iyun_vdc_vlb_iyun_base_vdc_id_fk
FOREIGN KEY (vdcid) REFERENCES iyun_base_vdc (id);