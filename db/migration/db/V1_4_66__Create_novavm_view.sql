/**

时间：	    2017年6月15日

说明： 	  新增云主机相关数据视图

前置版本：	ICloudsV4.9.pdm

当前版本：	ICloudsV4.9.pdm
*/
DROP VIEW IF EXISTS nova_vm_view;
CREATE VIEW nova_vm_view AS (
  SELECT
    n."id" AS ID,
    n.uuid AS uuid,
    n.vmstate AS vmState,
    n.powerstate AS powerState,
    n.hostname AS hostName,
    n.createddate AS createDate,
    n.updateddate AS updateDate,
    n.owner AS userId,
    n.manageip AS manageIp,
    n.projectid,
    d.ips AS ipaddress,
    d.subnetnames AS name,
    d.cidrs AS cidr,
    (
      SELECT
        NAME
      FROM
        iyun_keystone_project T
      WHERE
        T . ID = n.projectid
    ) AS projectname,
    u.loginname AS owner,
    d.ip_address,
    spe.monitorid
  FROM
    iyun_nova_vm n
    LEFT JOIN device_ip_list_group d ON n.uuid = d.device_id
    LEFT JOIN iyun_sm_user u ON u. ID = n. OWNER
    LEFT JOIN iyun_spe_networks spe ON spe.uuid = n.uuid
  WHERE
    n."id" NOT IN (
      SELECT
        r.resid
      FROM
        iyun_recycle_items r
      WHERE
        r.recycleaction <> '0'
    )
)