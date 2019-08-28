
/**

时间：	    2017年2月14日

说明： 	 去掉novavm云主机不为空属性

*/

ALTER TABLE public.iyun_nova_vm ALTER COLUMN uuid DROP NOT NULL;