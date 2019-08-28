delete from "public"."iyun_mail_templates" where id in (
'requestMaster:usertask2',
'requestMaster:usertask3',
'requestMaster:usertask4',
'requestMaster:usertask5',
'requestMaster:usertask7',
'incMaster:usertask2',
'incMaster:usertask3'
);


INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('requestMaster:usertask2', '行业云系统提醒：请处理流转的申请单', '需求工单-审批环节', '您好，云业务资源申请，请您审批，审批链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('requestMaster:usertask3', '行业云系统提醒：请处理流转的申请单', '需求工单-审批环节', '您好，云业务资源申请，请您审批，审批链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('requestMaster:usertask4', '行业云系统提醒：请处理流转的申请单', '需求工单-调度环节', '您好，云业务资源流程，请您调度处理，调度链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('requestMaster:usertask5', '行业云系统提醒：请处理流转的申请单', '需求工单-处理环节', '您好，云业务资源流程，请您处理，处理链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('requestMaster:usertask7', '行业云系统提醒：请处理流转的申请单', '需求工单-验证环节', '您好，云业务资源申请已经处理，请验证，链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('incMaster:usertask2', '行业云系统提醒：请处理流转的申请单', '事件工单-一线处理环节', '您好，云业务事件工单，请您处理，链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
INSERT INTO "public"."iyun_mail_templates" ("id", "desc", "templatename", "content", "status", "createdby", "createddate", "updatedby", "updateddate") VALUES ('incMaster:usertask3', '行业云系统提醒：请处理流转的申请单', '事件工单-二线处理环节', '您好，云业务事件工单，请您处理，链接如下如下：<br/>#{URL}<br/>注：此为系统自动发送邮件，请勿回复；', '0', '1', '2017-02-10 15:42:52', '1', '2017-02-10 15:42:54');
