/**

时间：	    2017年2月09日

说明： 	  创建网卡所属设备视图

*/
DROP VIEW IF EXISTS port_device;
CREATE VIEW port_device AS
(
 SELECT rt.id,rt."name" FROM iyun_vdc_route rt
 UNION ALL
 SELECT vm.id,vm.hostname FROM iyun_nova_vm vm
 UNION ALL
 SELECT pl.id,pl."name" FROM iyun_vlb_pools pl
)
