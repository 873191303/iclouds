alter table iyun_base_rules rename to iyun_base_images;

ALTER TABLE iyun_base_images ADD tenantid VARCHAR(50);

ALTER TABLE iyun_base_images ADD userid VARCHAR(50);

ALTER TABLE iyun_base_images ADD createdby VARCHAR(50);

ALTER TABLE iyun_base_images ADD createddate TIMESTAMP;

ALTER TABLE iyun_base_images ADD updatedby VARCHAR(50);

ALTER TABLE iyun_base_images ADD updateddate TIMESTAMP;