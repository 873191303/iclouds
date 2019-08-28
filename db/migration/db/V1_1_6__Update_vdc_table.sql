/**

	序号：	7

	文件：	V1_1_6__Update_vdc_table.sql

	时间：	2016年11月22日

	说明：	修正vdc部分的表

影响对象：	全部

前置版本：	ICloudsV2.3.pdm

当前版本：	ICloudsV2.3.pdm

 */
DROP TABLE iyun_vdc_route2port;

ALTER TABLE iyun_vdc_route ADD vdc_id VARCHAR (50) NULL;

ALTER TABLE iyun_vdc_route ADD CONSTRAINT route_vdc_vdcid_fk1 FOREIGN KEY (vdc_id) REFERENCES iyun_base_vdc (id);

ALTER TABLE iyun_vdc_network ADD vdc_id VARCHAR (50) NULL;

ALTER TABLE iyun_vdc_network ADD CONSTRAINT network_vdc_vdcid_fk1 FOREIGN KEY (vdc_id) REFERENCES iyun_base_vdc (id);

ALTER TABLE iyun_vdc_fireware DROP policyid;

ALTER TABLE iyun_vdc_ports ADD route_id VARCHAR (50) NULL;

ALTER TABLE iyun_vdc_ports ADD port_type VARCHAR (50) NULL;

ALTER TABLE iyun_vdc_ports ADD CONSTRAINT iyun_ports_route_routeid_fk1 FOREIGN KEY (route_id) REFERENCES iyun_vdc_route (id);

ALTER TABLE iyun_vdc_view RENAME COLUMN NEXT TO previous;

ALTER TABLE iyun_vdc_fireware RENAME TO iyun_vdc_firewall;

ALTER TABLE iyun_vlb_vips DROP pool_id;