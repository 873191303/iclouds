/**

时间：	    2017年7月4日

说明： 	  监控模块表格修改

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm

内容:     ipm_pft_opmessage2grp、ipm_pft_opmessage2usr 增加对应的sequence

*/
drop sequence if EXISTS ope_message_usr_seq;
drop sequence if EXISTS ope_message_grp_seq;
drop sequence if EXISTS action_condition_seq;

create sequence ope_message_usr_seq increment by 1 minvalue 1 no maxvalue start with 1;
create sequence ope_message_grp_seq increment by 1 minvalue 1 no maxvalue start with 1;
create sequence action_condition_seq increment by 1 minvalue 1 no maxvalue start with 1;
