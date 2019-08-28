ALTER TABLE public.iyun_product_specs2key DROP defvalue;
ALTER TABLE public.iyun_product_specs2key DROP minvalue;
ALTER TABLE public.iyun_product_specs2key DROP maxvalue;
ALTER TABLE public.iyun_product_specs2key DROP seq;

ALTER TABLE public.iyun_product_specs2keyvalue ADD valuetype VARCHAR(36) NULL;
ALTER TABLE public.iyun_product_specs2keyvalue ADD minvalue INT4 NULL;
ALTER TABLE public.iyun_product_specs2keyvalue ADD maxvalue INT4 NULL;
ALTER TABLE public.iyun_product_specs2keyvalue ADD step INT4 NULL;
ALTER TABLE public.iyun_product_specs2keyvalue DROP isdefault;
ALTER TABLE public.iyun_product_specs2keyvalue DROP listprice;
ALTER TABLE public.iyun_product_specs2keyvalue ALTER COLUMN unit DROP NOT NULL;
ALTER TABLE public.iyun_product_specs2keyvalue ALTER COLUMN value DROP NOT NULL;

ALTER TABLE public.iyun_product_listprice ADD beginprice NUMERIC(10,2) NULL;
ALTER TABLE public.iyun_product_listprice DROP step;
ALTER TABLE public.iyun_product_listprice DROP minvalue;
ALTER TABLE public.iyun_product_listprice DROP maxvalue;
ALTER TABLE public.iyun_product_listprice DROP defvalue;
ALTER TABLE public.iyun_product_listprice ALTER COLUMN unit DROP NOT NULL;

ALTER TABLE public.iyun_product_listprice2imag ADD beginprice NUMERIC(10,2) NULL;
ALTER TABLE public.iyun_product_listprice2imag DROP step;
ALTER TABLE public.iyun_product_listprice2imag DROP minvalue;
ALTER TABLE public.iyun_product_listprice2imag DROP maxvalue;
ALTER TABLE public.iyun_product_listprice2imag DROP defaultvalue;
ALTER TABLE public.iyun_product_listprice2imag ALTER COLUMN unit DROP NOT NULL;

ALTER TABLE public.iyun_base_prdclass ADD flavorflag BOOLEAN NULL;

