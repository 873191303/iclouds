
/**

序号：	    20

文件：	    V1_2_9__Update_lb_column.sql

时间：	    2016年12月12日

说明： 	  修改负载均衡模块相关的表字段

影响对象： 负载均衡相关表

前置版本：	ICloudsV2.8.pdm

当前版本：	ICloudsV2.8.pdm

 */
ALTER TABLE public.iyun_vlb_members DROP type;
ALTER TABLE public.iyun_vlb_members DROP cookie_name;
ALTER TABLE public.iyun_vlb_members ADD vm_id VARCHAR(50) NULL;
ALTER TABLE public.iyun_vlb_members
  ADD CONSTRAINT iyun_vm_id_fk
FOREIGN KEY (vm_id) REFERENCES iyun_nova_vm (id);
ALTER TABLE public.iyun_vlb_vips ADD vain_subnet_id VARCHAR(50) NULL;
ALTER TABLE public.iyun_vlb_pools RENAME COLUMN subnet_id TO fact_subnet_id;
ALTER TABLE public.iyun_vlb_vips ADD vip_address VARCHAR(50) NULL;
ALTER TABLE public.iyun_vlb_vips ADD "session_persistence " VARCHAR(50) NULL;
