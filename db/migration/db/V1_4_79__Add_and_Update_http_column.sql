ALTER TABLE public.ipm_pft_httptest ADD hostid BIGINT NOT NULL;
CREATE SEQUENCE public.ipm_pft_httpstepitem_httpstepitemid_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.ipm_pft_httpstepitem ALTER COLUMN httpstepitemid SET DEFAULT nextval('public.ipm_pft_httpstepitem_httpstepitemid_seq');
ALTER SEQUENCE public.ipm_pft_httpstepitem_httpstepitemid_seq OWNED BY public.ipm_pft_httpstepitem.httpstepitemid;
CREATE SEQUENCE public.ipm_pft_httptest2item_id_seq NO MINVALUE NO MAXVALUE NO CYCLE;
ALTER TABLE public.ipm_pft_httptest2item ALTER COLUMN id SET DEFAULT nextval('public.ipm_pft_httptest2item_id_seq');
ALTER SEQUENCE public.ipm_pft_httptest2item_id_seq OWNED BY public.ipm_pft_httptest2item.id;