
/**

序号：	    24

文件：	    V1_2_14__Add_ip_device_view.sql

时间：	    2016年12月17日

说明： 	  增加IP视图

影响对象：	iyun_base_prdClass

前置版本：	ICloudsV2.7.pdm

当前版本：	ICloudsV2.8.pdm
 */
 create view device_ip_info as SELECT v.device_id,
    i.ip_address,
    s.cidr,
    s.name AS subnetname,
    n.externalnetworks
   FROM (((iyun_vdc_ports v
     LEFT JOIN iyun_vdc_ipallocations i ON (((i.port_id)::text = (v.id)::text)))
     LEFT JOIN iyun_vdc_subnet s ON (((i.subnet_id)::text = (s.id)::text)))
     LEFT JOIN iyun_vdc_network n ON (((n.id)::text = (s.network_id)::text)));
     
 create view device_ip_list_group as SELECT view1.device_id,
    string_agg(concat(view1.ip_address,
        CASE
            WHEN (view1.externalnetworks = true) THEN 'T'::text
            ELSE 'F'::text
        END), ''::text) AS ips,
    string_agg((view1.subnetname)::text, ','::text) AS subnetnames,
    string_agg((view1.cidr)::text, ','::text) AS cidrs
   FROM device_ip_info view1
  GROUP BY view1.device_id;