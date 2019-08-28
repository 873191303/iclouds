ALTER TABLE ipm_pfm_measurement_1d RENAME COLUMN mixvalue TO minvalue;
ALTER TABLE ipm_pfm_measurement_1h RENAME COLUMN mixvalue TO minvalue;
ALTER TABLE ipm_pfm_measurement_6h RENAME COLUMN mixvalue TO minvalue;

CREATE OR REPLACE function mova_data(starttime varchar) 
RETURNS varchar 
as $$ 
BEGIN
    delete from ipm_pfm_metric_7d where 1 = 1;
    insert into ipm_pfm_metric_7d select * from ipm_pfm_metric_6d;
    
    delete from ipm_pfm_metric_6d where 1 = 1;
    insert into ipm_pfm_metric_6d select * from ipm_pfm_metric_5d;  
    
    delete from ipm_pfm_metric_5d where 1 = 1;
    insert into ipm_pfm_metric_5d select * from ipm_pfm_metric_4d;
    
    delete from ipm_pfm_metric_4d where 1 = 1;
    insert into ipm_pfm_metric_4d select * from ipm_pfm_metric_3d;
    
    delete from ipm_pfm_metric_3d where 1 = 1;
    insert into ipm_pfm_metric_3d select * from ipm_pfm_metric_2d;
    
    delete from ipm_pfm_metric_2d where 1 = 1;
    insert into ipm_pfm_metric_2d select * from ipm_pfm_metric_1d;
    
    delete from ipm_pfm_metric_1d where 1 = 1;
    insert into ipm_pfm_metric_1d select * from ipm_pfm_metric_0d;
    
    delete from ipm_pfm_metric_0d where 1 = 1;
    insert into ipm_pfm_metric_0d select * from ipm_pfm_value2history where collecttime <= cast(starttime as timestamp);
    delete from ipm_pfm_value2history where collecttime <= cast(starttime as timestamp);
    
    delete from ipm_pfm_measurement_1h where collecttime < (cast(starttime as TIMESTAMP) - ('30 day'):: INTERVAL);
    delete from ipm_pfm_measurement_6h where collecttime < (cast(starttime as TIMESTAMP) - ('60 day'):: INTERVAL);
    
    return 'success';
END 
$$ LANGUAGE PLPGSQL;
