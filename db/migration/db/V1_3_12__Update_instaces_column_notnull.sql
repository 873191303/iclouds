
/**
时间：     2017年1月19日

说明：     修改云管理云资源计量对象表结束日期字段不为notnull;修改操作日志参数remark字段为text类型

当前版本：  ICloudsV3.5.pdm
*/


/*==============================================================*/
/* Table: iyun_measure_instaces                                 */
/*==============================================================*/
ALTER TABLE public.iyun_measure_instaces ALTER COLUMN enddate DROP NOT NULL;

/*==============================================================*/
/* Table: iyun_sm_operatelogs                                   */
/*==============================================================*/
ALTER TABLE public.iyun_sm_operatelogs ALTER COLUMN remark TYPE TEXT USING remark::TEXT;