/**

时间：	    2017年2月08日

说明： 	  	邮件发送内容模板

*/
create table iyun_mail_templates (
   id                   varchar(36)          not null,
   "desc"               varchar(100)         null,
   templatename         varchar(50)          null,
   content              TEXT                 null,
   status               INT2                 null,
   createdby            VARCHAR(36)          not null,
   createddate          TIMESTAMP            not null,
   updatedby            VARCHAR(36)          not null,
   updateddate          TIMESTAMP            not null,
   constraint PK_IYUN_MAIL_TEMPLATES primary key (id)
);
