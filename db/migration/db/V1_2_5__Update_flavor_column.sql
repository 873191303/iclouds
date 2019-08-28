
/**

序号：	    16

文件：	    V1_2_5__Update_flavor_column.sql

时间：	    2016年12月6日

说明： 	  修改字段类型

影响对象：	iyun_nova_flavor,iyun_base_flavor

前置版本：	ICloudsV2.4.pdm

当前版本：	ICloudsV2.4.pdm

 */
Alter table iyun_base_flavor ALTER column id type varchar(36);
Alter table iyun_nova_flavor ALTER column id type varchar(36);
