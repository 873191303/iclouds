/**

时间：	    2017年2月13日

说明： 	  创建floatingip对应host视图

*/
DROP VIEW IF EXISTS floatingip2host;
CREATE VIEW floatingip2host AS
(
 SELECT floatip2route.id floatingipid, floatip2route.routename, b.id networkid,b.name networkname, b.route_id, c.cidr, d.ip_address ,e.device_owner, e.device_id , f.hostname, f.id hostid
FROM (
SELECT a.id, a.name, f.gw_port_id, f."name" as routename, f."id" routeid, f.tenant_id FROM iyun_neutron_floatingips a
LEFT JOIN iyun_vdc_ipallocations b on a.floating_port_id = b.port_id
LEFT JOIN iyun_vdc_ipallocations c on c.subnet_id = b.subnet_id 
LEFT JOIN iyun_vdc_ports d on d.id = c.port_id
LEFT JOIN iyun_vdc_route f on f.gw_port_id = d.id
WHERE  d.device_owner = 'network:router_gateway') as floatip2route

LEFT JOIN iyun_vdc_network b on b.route_id = floatip2route.routeid
LEFT JOIN iyun_vdc_subnet c on c.network_id = b."id"
LEFT JOIN iyun_vdc_ipallocations d on d.subnet_id = c."id"
LEFT JOIN iyun_vdc_ports e on e."id" = d.port_id
LEFT JOIN iyun_nova_vm f on f."uuid" = e.device_id
WHERE  e.device_owner NOT in ('', 'network:floatingip', 'network:router_gateway', 'network:router_interface', 'neutron:LOADBALANCER')
)
