
/**
时间：     2017年1月18日

说明：     修改云管理云资源对象计量账单明细

当前版本：  ICloudsV3.5.pdm
*/


/*==============================================================*/
/* Table: iyun_instance_measuredetail                           */
/*==============================================================*/
ALTER TABLE public.iyun_instance_measuredetail ALTER COLUMN num DROP NOT NULL;
ALTER TABLE public.iyun_instance_measuredetail ALTER COLUMN enddate DROP NOT NULL;
ALTER TABLE public.iyun_instance_measuredetail ALTER COLUMN description DROP NOT NULL;