
/**

时间：	    2017年2月28日

说明： 	 修改查询云主机获得IP的视图

*/

DROP VIEW  IF EXISTS device_ip_list_group;
DROP VIEW  IF EXISTS device_ip_info;
CREATE VIEW device_ip_info AS
SELECT v.device_id,
    i.ip_address,
    s.cidr,
    s.name AS subnetname,
    n.externalnetworks,
    f.fixed_port_id,
    f.floating_ip_address
   FROM ((((iyun_vdc_ports v
     LEFT JOIN iyun_vdc_ipallocations i ON (((i.port_id)::text = (v.id)::text)))
     LEFT JOIN iyun_vdc_subnet s ON (((i.subnet_id)::text = (s.id)::text)))
     LEFT JOIN iyun_vdc_network n ON (((n.id)::text = (s.network_id)::text)))
     LEFT JOIN iyun_neutron_floatingips f ON (((f.fixed_port_id)::text = (v.id)::text)));
CREATE VIEW device_ip_list_group AS
 SELECT view1.device_id,
    string_agg(concat(view1.ip_address,
        CASE
            WHEN (view1.externalnetworks = true) THEN 'T'::text
            ELSE 'F'::text
        END), ''::text) AS ips,
    string_agg((view1.subnetname)::text, ','::text) AS subnetnames,
    string_agg((view1.cidr)::text, ','::text) AS cidrs,
    string_agg((view1.fixed_port_id)::text, ','::text) AS port_id,
    string_agg((view1.floating_ip_address)::text, ','::text) AS ip_address
   FROM device_ip_info view1
  GROUP BY view1.device_id;