CREATE OR REPLACE function mova_data(starttime varchar) 
RETURNS varchar 
as $$ 
BEGIN
    delete from ipm_pfm_metric_7d where collecttime < (cast(starttime as TIMESTAMP) - ('10 day'):: INTERVAL);
    insert into ipm_pfm_metric_7d select * from ipm_pfm_metric_6d where collecttime < (cast(starttime as TIMESTAMP) - ('9 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_6d where collecttime < (cast(starttime as TIMESTAMP) - ('9 day'):: INTERVAL);
    insert into ipm_pfm_metric_6d select * from ipm_pfm_metric_5d where collecttime < (cast(starttime as TIMESTAMP) - ('8 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_5d where collecttime < (cast(starttime as TIMESTAMP) - ('8 day'):: INTERVAL);
    insert into ipm_pfm_metric_5d select * from ipm_pfm_metric_4d where collecttime < (cast(starttime as TIMESTAMP) - ('7 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_4d where collecttime < (cast(starttime as TIMESTAMP) - ('7 day'):: INTERVAL);
    insert into ipm_pfm_metric_4d select * from ipm_pfm_metric_3d where collecttime < (cast(starttime as TIMESTAMP) - ('6 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_3d where collecttime < (cast(starttime as TIMESTAMP) - ('6 day'):: INTERVAL);
    insert into ipm_pfm_metric_3d select * from ipm_pfm_metric_2d where collecttime < (cast(starttime as TIMESTAMP) - ('5 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_2d where collecttime < (cast(starttime as TIMESTAMP) - ('5 day'):: INTERVAL);
    insert into ipm_pfm_metric_2d select * from ipm_pfm_metric_1d where collecttime < (cast(starttime as TIMESTAMP) - ('4 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_1d where collecttime < (cast(starttime as TIMESTAMP) - ('4 day'):: INTERVAL);
    insert into ipm_pfm_metric_1d select * from ipm_pfm_metric_0d where collecttime < (cast(starttime as TIMESTAMP) - ('3 day'):: INTERVAL);
    
    delete from ipm_pfm_metric_0d where collecttime < (cast(starttime as TIMESTAMP) - ('3 day'):: INTERVAL);
    insert into ipm_pfm_metric_0d select * from ipm_pfm_value2history where collecttime < (cast(starttime as TIMESTAMP) - ('2 day'):: INTERVAL);
    delete from ipm_pfm_value2history where collecttime < (cast(starttime as TIMESTAMP) - ('2 day'):: INTERVAL);
    
    delete from ipm_pfm_measurement_1h where collecttime < (cast(starttime as TIMESTAMP) - ('30 day'):: INTERVAL);
    delete from ipm_pfm_measurement_6h where collecttime < (cast(starttime as TIMESTAMP) - ('60 day'):: INTERVAL);
    
    return 'success';
END 
$$ LANGUAGE PLPGSQL;

drop table if exists iyun_copy_ports;
drop table if exists iyun_copy_ipallocations;
drop table if exists iyun_copy_floatingips;
drop table if exists iyun_copy_route;
drop table if exists iyun_copy_network;
drop table if exists iyun_copy_vips;
