/**
时间：	    2017年1月13日

说明： 	  修改产品规格表default字段名

当前版本：	ICloudsV3.3.pdm
*/

/*==============================================================*/
/* Table: iyun_product_specs2key                                */
/*==============================================================*/
ALTER TABLE public.iyun_product_listprice RENAME COLUMN "default" TO defvalue;

ALTER TABLE public.iyun_product_specs2key RENAME COLUMN mixvalue TO minvalue;


/*==============================================================*/
/* Table: iyun_product_listPrice                                */
/*==============================================================*/
ALTER TABLE public.iyun_product_specs2key RENAME COLUMN "default" TO defvalue;