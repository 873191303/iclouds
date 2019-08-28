
/**
时间：     2017年1月22日

说明：     账单明细表增加规格单价字段

当前版本： ICloudsV3.5.pdm
*/

ALTER TABLE public.iyun_product_listprice2imag ADD specprice NUMERIC(10,2) NULL;
