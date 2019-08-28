
/**
时间：     2017年1月20日

说明：     更改云管理云资源对象计量账单明细specId外键指向云管理产品下单定价目录镜像表的id

当前版本： ICloudsV3.5.pdm
*/

/*==============================================================*/
/* Table: iyun_instance_measuredetail                              */
/*==============================================================*/
-- ALTER TABLE public.iyun_instance_measuredetail DROP CONSTRAINT iyun_instance_measuredetail_specid_fkey;
ALTER TABLE public.iyun_instance_measuredetail
ADD CONSTRAINT iyun_instance_measuredetail_specid_fkey
FOREIGN KEY (specid) REFERENCES iyun_product_listprice2imag (id);