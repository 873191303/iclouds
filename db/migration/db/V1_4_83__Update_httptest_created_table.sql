/**

时间：	    2017-07-31

说明： 	  修改http监控表字段

前置版本：	ICloudsV5.2.pdm

当前版本：	ICloudsV5.2.pdm
*/
ALTER TABLE public.ipm_pft_httptest RENAME COLUMN "CreatedBy" TO createdby;
ALTER TABLE public.ipm_pft_httptest RENAME COLUMN "CreatedDate" TO createddate;
ALTER TABLE public.ipm_pft_httptest RENAME COLUMN "UpdatedBy" TO updatedby;
ALTER TABLE public.ipm_pft_httptest RENAME COLUMN "UpdatedDate" TO updateddate;