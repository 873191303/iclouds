ALTER TABLE public.iyun_netflow_originals ADD type VARCHAR(100) NOT NULL;
ALTER TABLE public.iyun_netflow_originals2day ADD type VARCHAR(100) NOT NULL;

ALTER TABLE public.iyun_netflow_originals ADD value int8 NOT NULL;
ALTER TABLE public.iyun_netflow_originals2day ADD value int8 NOT NULL;

ALTER TABLE public.iyun_netflow_originals ADD currentTotal int8 NOT NULL;

ALTER TABLE iyun_netflow_originals DROP inputs;
ALTER TABLE iyun_netflow_originals DROP outputs;

ALTER TABLE iyun_netflow_originals2day DROP inputs;
ALTER TABLE iyun_netflow_originals2day DROP outputs;