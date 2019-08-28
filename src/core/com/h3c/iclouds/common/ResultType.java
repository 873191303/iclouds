package com.h3c.iclouds.common;

public enum ResultType {

    success("操作成功"),

    user_not_grant("未授权使用行业云"),

    sso_login_failure("票据验证失败，登录失败"),

    system_error("系统异常，请稍后再尝试操作"),

    task_diff_limit_effect("异常条目已经失效，请获取最新任务结果进行处理"),

    task_diff_not_define("同步异常任务还未定义"),

    task_diff_not_start("同步异常任务还没开始"),

    not_found_token("没找到token"),

    user_cloudos_group_error("调用cloudos授权用户失败"),

    failure("操作失败"),

    role_select_least_one("归属H3C租户或者电信租户的用户最少选择一个角色"),

    cannot_select_group("非H3C租户下的用户不需要选择群组"),

    parameter_error("参数错误"),

    missing_require_parameter("丢少必要的参数，操作失败"),

    volume_owner_not_exist("云硬盘归属用户不存在"),

    custom_in_use("客户已经被使用，删除失败"),

    contact_in_use("联系人已经被使用，删除失败"),

    date_mapping_status_error("用户登录时间已过期，请联系管理员修改"),

    deleted("已删除"),

    exist_error("已存在"),

    data_error("数据异常，请联系管理员"),

    loginName_or_password_error("用户名或密码错误"),

    password_error("密码错误"),

    user_not_exist("用户不存在"),

    unAuthorized("无权限"),

    repeat("重复"),

    user_in_department("还有用户存在于部门中"),

    not_exist("不存在"),

    task_diff_already_handled("差异的数据已经处理完毕或正在处理"),

    upload_failure("上传文件失败"),

    file_type_error("文件类型错误"),

    file_not_exist("该文件不存在"),

    work_flow_exist("流程已经存在"),

    process_delete_error("删除流程失败"),

    deploy_error("部署流程失败"),

    deploy_file_exist("流程文件丢失，请重新上传"),

    bus_started("业务申请已经开始，操作失败"),

    bus_assigne_error("申请单已经被签收"),

    workflow_flow_deployed("流程已经部署过了"),

    bus_start_workflow_failure("流程申请开始失败，请重新尝试"),

    custom_deleted("操作的客户已停用"),

    old_request_not_exist("原申请单不存在"),

    old_request_already_change("原申请单已经发生变更，提交失败"),

    old_request_not_close("原申请单必须处于已完结状态能发生变更"),

    empty_item("业务办理最少需要添加一条项目"),

    old_item_not_exist("原申请单条目不存在"),

    bus_start_applier_department("申请人未归属于任何部门，开始流程请求失败"),

    workflow_role_use("该角色关联着流程角色，删除失败"),

    bus_end("审批流程已经结束，请刷新页面"),

    item_parameter_error("申请资源条目参数错误"),

    draw_has_asm("机房机柜中还存在设备"),

    draw_asm_clash("机柜中的设备机位冲突"),

    draw_not_exist("机柜不存在"),

    class_item_not_exist("设备型号不存在"),

    assetId_repeat("资产编号已存在"),

    serial_repeat("设备序列号已存在"),
    
    serial_or_assetId_not_null("序列号或资产编号不能为空"),
    
    assetType_error("没有该设备类型"),

    draw_unumb_over("超过机柜最大U数"),

    class_item_in_asm_error("资产选择的设备型号不对"),

    tenant_role_cannot_use_other_error("选择的角色类型与用户不匹配"),

    ip_was_used("IP已经被其他设备占用"),

    asset_flag_error("资产状态不对"),

    role_add_user_project_error("角色赋权用户对应的租户不匹配"),

    function_not_open("功能暂未开放"),

    parent_code_not_exist("归属编码数据不存在"),

    software_status_error("软件资产状态不对"),

    software_assetId_repeat("软件资产编号已存在"),

    user_status_error("用户状态异常"),

    asset_exist_stack("设备处于其他堆叠中"),

    user_role_error("当前用户没有授权访问该平台"),

    custom_error("客户不存在或者选择的客户不属于当前用户"),

    contact_error("客户联系人不存在或者联系人与客户不匹配"),

    asset_already_in_stack("设备已经存在于堆叠中"),

    asset_not_in_stack("设备不在堆叠中"),

    select_port_deleted("选择的网口已被删除"),

    select_port_already_linked("选择的网口已经连接其他网口"),

    port_type_error("端口类型不一致"),

    virtual_eth_type_cannt_link("虚拟网口不能作为网口被连接"),

    stock_port_cannt_be_trunk("存储网口不能作为上联设备"),

    server_trunk_port_under_must_be_stock("服务器作为上联口，只允许连接存储网口"),

    draw_less_than_origin("设定的U数必须大于机柜上设备的最大U数"),

    tenant_not_exist("租户不存在"),

    pools_used("主机池正在使用"),

    clusters_used("集群正在使用"),

    host_used("主机正在使用"),

    project_resource_owner("租户扔拥有资源"),

    policie_not_exist("规则集不存在"),

    vlb_not_exist("负载均衡不存在"),

    vlb_deleted_status("负载均衡为删除状态"),

    vlbPool_not_exist("实例池不存在"),

    route_not_exist("路由器不存在"),

    network_not_exist("网络不存在"),

    network_public_not_exist("公网不存在"),

    healthMonitor_not_exist("监视器不存在"),

    still_relate_vlbPool("仍然关联着负载均衡实例"),

    still_relate_policie("仍然关联着规则"),

    still_relate_firewall("仍然关联着防火墙"),

    still_relate_route("仍然关联着路由器"),

    still_relate_healthMonitor("仍然关联着监视器"),

    still_relate_vlbMember("仍然关联着成员"),

    not_relate_healthMonitor("没有关联监视器"),

    still_relate_port("仍然关联着端口"),

    still_relate_network("仍然关联着网络"),

    still_relate_gateway("仍然关联着外部网关"),

    some_ip_used("仍然有ip正在使用"),

    ip_format_error("ip格式错误"),

    ip_notIn_range("ip不在范围内"),

    cidr_format_error("cidr格式错误"),

    ipPool_format_error("地址池格式错误"),

    cidr_not_null("cidr不能为空"),

    gatewayIp_notIn_range("网关ip不在ip池范围内"),

    startIP_greater_than_range("首ip大于末ip"),

    ipPool_contain_gatewayIp("ip段包含网关ip"),

    ipPool_contain_repeat("ip段内有重叠部分"),

    ipPool_notIn_range("ip段不在cidr范围内"),

    attathment_empty("必须上传附件"),

    next_approver_not_exist("选择的下一个处理人不存在"),

    next_approver_error("选择的下一个处理人无权限处理"),

    attathment_error("上传的附件有错"),

    mail_error("邮箱格式错误"),

    reqFTime_error("要求完成时间格式错误"),
    
    renewal_error("续租时间不能超过用户合同时间"),

    firewall_not_exist("防火墙不存在"),

    vdc_already_created("已创建vdc"),

    connection_relation_error("连接关系错误(网络跟路由器相连，路由器跟防火墙相连)"),

    still_not_create_vdc("未创建视图"),

    parent_status_exception("关联的父级状态异常"),

    status_exception("状态异常"),

    flavor_not_exist("规格id不存在"),

    azone_not_exist("可用域不存在"),

    project_not_exist("租户不存在"),

    cloudos_create_failure("cloudos对接创建失败"),

    diff_cloud_host_create_failure("创建云主机失败"),

    diff_cloud_host_create_timeover("创建云主机超时"),

    diff_cloud_host_get_ip_failure("获取云主机ip失败"),

    cloudos_update_failure("cloudos对接修改失败"),

    cloudos_delete_failure("cloudos对接删除失败"),

    user_name_exist("用户名已存在"),

    login_name_exist("登录名已存在"),

    tenant_exist_custom("租户已经存在客户了"),

    state_starting("云主机当前状态:开机中"),

    state_rebooting("云主机当前状态:重启中"),

    state_stoping("云主机当前状态:关机中"),

    state_normal("云主机当前状态:正常"),

    state_stop("云主机当前状态:已停止"),

    state_creating("云主机当前状态:创建中"),

    state_exception("云主机当前状态:异常"),

    state_cloning("云主机当前状态:克隆新云主机"),

    state_clone_operating("云主机当前状态:克隆处理中"),

    state_image_translating("云主机当前状态:转换为镜像中"),

    state_image_translating_create_image("云主机当前状态:转换为镜像中(进度:20%, 创建镜像)"),

    state_image_translating_create_vm("云主机当前状态:转换为镜像中(进度:40%, 创建云主机)"),

    state_image_translating_create_template("云主机当前状态:转换为镜像中(进度:60%, 生成模板)"),

    state_image_translating_replace_vm("云主机当前状态:转换为镜像中(进度:80%, 替换云主机)"),

    state_image_translate_failure("云主机当前状态:转换为镜像失败"),

    state_hang_up("云主机当前状态:挂起"),

    state_create_failure("云主机当前状态:创建失败"),

    state_deleting("云主机当前状态:删除中"),

    state_delete_failure("云主机当前状态:删除失败"),

    request_not_match_workflow("申请单与流程不匹配"),

    cloudos_api_error("CLOUDOS API访问异常"),
    
    cloudos_login_lose("登录失效，请重新登录"),

    quota_use_more_hardlimit("使用已超过当前限定配额，请重新设置"),

    tenant_quota_network_not_exist("租户未分配网络配额"),

    cloud_networkid_not_exist("cloudos网络不存在"),

    cidr_notin_range_or_not_contain_quota("cidr不在范围内或租户未分配此网络"),

    cidr_contain_repeat("cidr重叠"),

    tenant_not_contain_quota("租户没有分配配额或配额为0"),

    quota_reach_max("达到配额最大值"),

    option_not_null("操作类型不能为空"),

    uuid_not_null("uuid不能为空"),

    id_not_null("id不能为空"),

    type_not_null("资源类型不能为空"),

    data_not_null("资源参数不能为空"),

    sequence_not_null("操作顺序不能为空"),

    data_param_error("资源参数错误"),

    previous_not_exist("父级不存在"),

    previous_type_error("父级类型错误"),

    previous_not_null("父级不能为空"),

    subnet_must_link_route("网络必须连接路由器"),

    cidr_over_parent_project("cidr超过父级租户的cidir"),

    handling("处理中"),

    vdc_option_lock("VDC视图处于编辑状态，请先保存或取消操作"),

    unlink_firewall("没有连接防火墙"),

    unlink_route("没有连接路由器"),

    cloudos_id_not_exist("cloudos主键id不存在"),

    novam_not_exist("主机不存在"),

    subnet_not_exist("子网不存在"),

    still_relate_host("仍然关联着主机"),

    abcClient_init_failure("爱数网络服务初始化失败"),

    tenant_not_root("不是根租户"),

    lock_by_other_user("其它用户正在操作"),

    lock_by_other_address("其它地点正在操作"),

    mapping_session_error("session不匹配"),

    mapping_user_error("用户不匹配"),

    mapping_version_error("版本不匹配"),

    quota_use_range("配额使用范围"),

    cookie_name_not_null("会话名称不能为空"),

    url_and_method_not_null("url和http方法不能为空"),

    url_param_error("url和http方法不能为空"),

    task_handling("有任务正在执行"),

    cloudos_port_not_find("cloudos平台虚拟网卡不存在"),

    ipallocation_not_exist("ipallocation不存在"),

    network_still_has_floatingip("网络仍有公网ip"),

    tenant_parent_not_exist("父级租户不存在"),

    isnot_external_network("不是外部网络"),

    network_not_link_to_route("外网未链接到路由"),

    volume_already_attached_success("硬盘已经成功挂载至主机"),

    volume_already_attached_other_host("硬盘已经挂载到其它主机"),

    volume_not_attach_host("硬盘未挂载至主机"),

    volume_not_in_recycle("硬盘不在回收站中"),

    floatingip_link_to_port("公网ip绑定至虚拟网卡"),

    unit_error("单位错误"),

    product_not_contain_keyName("该产品没有这个属性"),

    key_not_contain_value("该属性没有这个预设值"),

    used_in_flavor_group("组合规格中正在使用"),

    still_not_create_app("应用未创建"),

    not_in_range("超出赋值范围"),

    app_not_exist("应用不存在"),

    app_option_lock("应用正在执行"),

    app_relation_not_exist("app关系记录不存在"),

    app_id_repeat("app的id不可重复"),

    app_pid_not_point("元素没有指定父节点"),

    cidr_use_in_nova("cidr正在被云主机使用"),

    cloud_imageid_not_exist("镜像id不存在"),

    old_password_error("旧密码错误"),

    password_length_less_than_six("密码长度不够"),

    password_too_long("密码长度过长"),

    choose_department_not_exists("选择的部门不存在"),

    department_mapping_user_error("选择的部门与用户所属租户不符"),

    department_deptCode_repeat("部门编码已存在"),

    department_deptName_repeat("部门名称已存在"),

    delete_current_user("不能删除当前用户"),

    login_code_error("登录验证码错误"),

    delay_must_equal_or_greater_than_timeout("检查间隔必须大于或等于超时限制"),

    relate_measure("仍然关联计费账单"),

    app_type_not_exist("App类型不存在"),

    cidr_use_in_vdc("cidr正在被使用"),

    service_not_exist("中间件不存在"),

    data_name_error("数据名称不合法"),

    valid_key_error("验证key错误"),

    task_complete_error("不能处理当前环节的申请单，请检查申请单环节"),

    file_upload_error("上传文件key不存在或者文件后缀不匹配"),

    file_than_max("上传文件个数大于上限"),

    file_size_than_max("上传文件大小大于3M"),

    cloud_host_create_mode_error("云主机模式不对"),

    dont_have_this_operation("无此操作"),

    canot_send_notice_to_yourself("不能给自己发通知公告"),

    tenant_not_contain_port_quota("租户没有分配网卡配额或配额为0"),

    port_quota_reach_max("网卡达到配额最大值"),

    image_resource_not_synchronized("镜像资源未同步"),

    zone_resource_not_synchronized("可用域资源未同步"),

    port_owner_error("虚拟网卡类型错误"),

    appview_name_not_null("视图名称不能为空"),

    appview_not_exist("数据库中应用视图不存在"),

    app_database_not_exist("app数据库不存在"),

    app_cluster_not_exist("app数据库不存在"),

    pwd_error_too_many_times("账户或密码错误次数太多，请一分钟后再试"),

    please_retry_later("请稍后尝试登录"),

    role_mapping_resource_error("角色关联资源的类型不对"),

    network_has_no_ip("网络中没有ip段"),

    server_has_no_ip("云主机不存在IP"),

    in_handle_task("当前主机有任务在处理中"),

    novavm_volume_not_exist("云主机的云硬盘已被删除"),

    novavm_start_only_stop("云主机开机当前的状态只能是关机"),

    novavm_stoporreboot_only_normal("云主机关机或者重启当前的状态只能是正常"),

    novavm_flavor_not_exist("创建的云主机的规格不存在"),

    novavm_port_not_exist("云主机创建时的网卡不存在"),

    cludos_novavm_create_exception("cloudos云主机创建过程中异常"),

    securitygroup_not_exist("安全组不存在"),

    novavm_name_rule("云主机名称只能由字母组成，数字，下划线组成"),

    port_still_attach_host("虚拟网卡依然挂载着主机"),

    res_not_exist("资源不存在"),

    already_has_gateway("路由器已经有外部网关"),

    already_link_route("网络已经关联到路由"),

    network_child_project_use("网段已分配给子租户，不能删除"),

    name_repeat("名称重复"),

    source_ip_format_error("源ip格式错误"),

    destination_ip_format_error("目标ip格式错误"),

    port_format_error("源端口或目的端口格式错误"),
    
    ip_wasnt_allocation("没有分配ip"),

    route_dont_set_gateway("路由器没有设置外部网关"),

    route_didnt_allocate_floatingip("路由没有分配floatingIp"),

    public_network_doesnt_exist("公网不存在"),

    cloudos_project_name_repeat("cloudos租户名重复"),

    floatingip_and_route_not_in_same_public_network("公网ip和路由器不在同一个公网"),

    floating_ip_not_exist("浮动ip不存在"),

    novavm_state_illegal("云主机状态异常"),

    novavm_deleting_not_recovery("云主机删除时不可还原"),

    not_relate_route("没有路由器关联"),

    still_relate_policieRule("还关联着防火墙规则"),

    cloudos_novaflavor_not_exist("cloudos规格不存在"),

    novavm_recycle_tasks_reach_num("进回收站云主机任务执行个数不能超过2个"),

    novavm_delete_tasks_reach_num("云主机删除任务数不能超过3个"),

    novavm_stop_can_delete("云主机关闭才可以还原"),

    project_exist_resource("租户占用资源不能删除"),

    port_not_exist("虚拟网卡不存在"),

    network_not_in_current_tenant("网络不在当前租户下"),

    conflict("与被请求资源的当前状态存在冲突，请求无法完成"),

    cloudos_login_name_exist("cloudos登录名重复"),

    cloud_host_exist_volume("云主机还存在挂载的硬盘"),

    cloud_host_lack_of_network("云主机缺少网络信息"),

    volume_cloud_host_not_exist("硬盘关联的云主机还未同步"),

    before_rule_not_exist("前于规则不存在"),

    after_rule_not_exist("后于规则不存在"),

    before_rule_front_after_rule("前于规则位于后于规则之前"),

    volume_type_error("硬盘类型错误"),

    port_attach_host_failure("虚拟网卡绑定主机失败"),

    publicshed_notice_cant_be_updated("发布的通知公告不能修改"),

    file_vdc_save_validation("vdc整图参数校验"),

    picked_public_network("选择网络是公网"),

    file_quota_failure("失败配额特殊处理的标示"),

    file_quota_success("成功配额特殊处理的标示"),

    file_cloudhost_delect_hasclouddisk("失败配额特殊处理的标示"),

    novavm_create_not_public_network("云主机初次创建不能使用公网IP"),

    network_type_error("网络类型错误"),

    only_can_attach_normal_or_stop_state_novavm("只能挂载到开机或关机状态的主机"),

    file_volume_count_or_size_quota_error("云硬盘个数或容量配额超限"),

    neutronError("cloudos的NeutronError"),

    row_and_column_must_bigger_than_one("规格至少为11"),

    file_project_error("错误提示符"),

    floatingip_already_bind_to_resource("floatingip已经绑定到资源"),

    cloud_host_exist_floatip("操作的云主机还关联着公网IP"),

    cross_user_operation("非法的跨用户操作"),

    cloudos_exception("cloudos操作失败"),

    poolhost_not_exist("主机池不存在"),

    clusters_not_exist("集群不存在"),

    asm_not_exist("资源不存在"),

    only_can_withdraw_published_notice("只能撤销发布状态的通知公告"),

    only_can_delete_draf_notice("只能删除草稿状态的通知公告"),

    system_busy_please_retry_later("系统繁忙，请稍后再试"),

    already_unset_gateway("路由已经断开外部网关"),

    only_available_voluem_can_attach_host("只有可用状态的硬盘才能挂载到主机"),

    floatingip_not_link_to_resource("floatingIp尚未关联到资源"),

    tenant_quota_not_contain("租户的配额不存在"),

    firewall_already_linked_by_route("该防火墙已被路由器关联"),

    tenant_quota_num_wrongfulness("租户的配额个数不符合"),

    image_not_exist("镜像不存在"),

    cloud_host_not_exist_iamge("云主机不存在镜像"),

    user_lock("用户被锁"),

    file_novaflavor_failure("不符合镜像的规格提示符"),

    relate_floatingip_already_in_use("有公网ip已分配给使用关联该路由器的网络的设备"),

    floatingip_use_in_novavm("已分配公网IP，不能从云主机卸载网卡"),

    route_floating_not_link_privatenetwork("公网未有连接的路由或者对应的路由未与私网连接"),

    server_status_exception("云主机存在异常，请联系管理员"),

    translate_task_than_max("正在执行转换镜像的任务"),

    already_vm_to_image("云主机正在转为镜像"),

    trans_vm_need_be_stop("云主机必须为关机状态"),

    relate_project_still_not_synchronization("关联租户信息未同步"),

    still_relate_user("仍然关联着用户"),

    diff_project_exists("租户已经存在"),

    diff_user_exists("用户已经存在"),

    diff_user_not_exists("用户还未同步"),

    diff_handle_method_error("调取的方法错误"),

    not_exist_in_iyun("在iyun上不存在"),

    not_exist_in_cloudos("在cloudos上不存在"),

    still_relate_resouces("仍然关联着资源"),

    telecome_project_not_need_sychronization("电信租户用户不需要同步"),

    cannot_get_projectid("无法获取租户id"),

    already_exist_in_iyun("iyun已存在该资源"),

    relate_policy_still_not_synchronization("关联规则集信息未同步"),

    relate_firewall_still_not_synchronization("关联防火墙信息未同步"),

    relate_route_still_not_synchronization("关联路由器信息未同步"),

    already_allocation_to_resource("公网已经分配到资源"),

    allocation_resource_still_not_synchronization("分配资源信息未同步"),

    relate_public_network_still_not_synchronization("关联公网信息未同步"),

    relate_network_still_not_synchronization("关联网络信息未同步"),

    relate_gateway_still_not_synchronization("关联网关信息未同步"),

    relate_user_still_not_synchronization("关联用户信息未同步"),

    sync_volume_but_attachment("同步云硬盘成功，但是挂载到云主机失败"),

    already_link_by_router("已被路由器挂载外部网关"),

    network_is_private("该网络为私网"),

    gwport_not_exist("外部网关网卡不存在"),

    public_network_notequal_resource_gateway("所属网络与分配资源关联路由器的外部网关不一致"),

    conflict_with_private_network("绑定的公网与挂载私网网段有冲突"),

    already_link_by_floatingip("网卡已被公网ip分配"),

    project_cannot_create_resource("租户不允许自助创建资源"),

    user_not_belong_to_project("用户不属于选定租户"),

    already_allocation_by_floatingip("已关联公网"),

    firewall_not_exist_in_cloudos("防火墙在cloudos不存在"),

    router_not_exist_in_cloudos("路由器在cloudos不存在"),

    network_not_exist_in_cloudos("网络在cloudos不存在"),

    vlbpool_not_exist_in_cloudos("负载均衡在cloudos不存在"),

    vlb_not_exist_in_cloudos("负载均衡组在cloudos不存在"),

    tenant_inconformity("租户不一致"),

    floatingIp_not_exist_in_cloudos("公网ip在cloudos不存在"),

    port_not_exist_in_cloudos("资源网卡在cloudos不存在"),

    user_inconformity("用户不一致"),

    prdclass_not_exist("产品不存在"),

    azone_not_null("可用域不为空"),

    flavor_param_error("规格参数错误"),

    flavor_not_null("规格不为空"),

    flavor_cannot_change("规格不能修改"),

    used_by_volume("正在云硬盘被使用"),

    user_not_exist_in_cloudos("用户在cloudos不存在"),

    role_cannot_auth_to_root_project("该角色只能授权给非根租户下的用户"),

    role_only_auth_to_root_project("该角色只能授权给根租户下的用户"),

    user_cannot_both_have_tenant_and_operation("租户管理员与云运营角色不能属于同一个用户"),

    user_cannot_both_have_tenant_and_cloud("租户管理员与云管理员不能属于同一个用户"),

    user_cannot_both_have_operation_and_cloud("云运营角色与云管理员不能属于同一个用户"),

    image_belong_to_other_tenant("该镜像属于其它租户"),

    azone_belong_to_other_tenant("该可用域属于其它租户"),

    image_can_not_be_used("镜像已禁用"),

    role_not_exist("角色不存在"),

    system_not_exist("第三方系统不存在"),

    resource_not_exist("资源不存在"),
	
	port_not_attach_server("网卡没有挂载到云主机"),
    
	port_already_attach_server("网卡已经挂载到云主机"),
    
    repeat_in_isso("在isso重复"),
    
    isso_exception("isso连接异常"),
    
    failure_in_isso("在isso操作失败"),
    
    value_not_null("value值不能为null"),
    
    value_not_in_range("value值不在范围内"),
    
    value_type_error("value值类型错误"),
    
    value_validator_error("value值校验失败"),
    
    minvalue_maxvalue_step_not_null("最小值、最大值、步长不能为null"),
    
    minvalue_greater_than_maxvalue("最小值必须小于最大值"),
    
    value_repeat("该值与其它预设值重叠"),
    
    attribute_type_not_be_int_or_float("属性值类型不为数值时不能设连续值"),
    
    specs2key_not_exist("属性不存在"),
    
    specs2key_not_belong_to_class("该属性不属于该产品"),
    
    specs2keyvalue_not_exist("该属性值不存在"),
    
    specs2keyvalue_not_belong_to_specs2key("属性值与属性不匹配"),
    
    only_can_definition_one_continuous_value("连续性值必须单独定价"),
    
    only_can_contains_one_continuous_value("组合规格只能包含一个连续值定价"),
    
    listprice_repeat("该规格已定价"),
    
    listprice_not_exist("有规格定价不存在"),
    
    please_choice_all_key("请选择所有属性"),
    
    continuous_value_must_have_stepprice("连续值必须设定步长价格"),
    
    group_value_must_have_listprice("组合规格必须设定规格目录价格"),
    
    discount_must_between_0_and_1("折扣必须在0至1范围内"),
    
    floating_ip_allocated_to_resource("已分配资源的公网ip不允许修改带宽"),
    
    azone_belong_to_another_prdclass("可用域属于其它产品"),
    
    key_repeat("属性重复"),
    
    select_vm_in_cas_failure("云主机在cas里面不存在"),
    
    not_support_monitor("暂时还未支持该主机监控服务"),

    not_found_manage_port("没查询到云主机的管理网卡"),
    
    cas_attach_failure("cas挂载网卡失败"),
    
    cas_dettach_failure("cas卸载网卡失败"),
    
    subnet_conflict_with_other_port("与云主机当前网卡网络重复"),
    
    flavor_already_exist("已存在该规格"),

    already_join_monitor("选择的云主机已经加入监控"),

    not_join_monitor("选择的云主机还未加入监控"),

    connection_monitor_failure("获取监控链接失败，请联系管理员"),

    not_found_init_port("未找到初始化网卡信息"),
    
    password_not_null("密码不能为空"),
    
    volume_status_not_attached("只能卸载已加载状态云硬盘"),
    
    volume_status_exception("不能删除创建中、挂载中或卸载状态的云硬盘"),
    
    project_have_role("该租户底下还有归属角色"),
    
    project_have_user("该租户底下还有归属用户"),
    
    port_restricted("该网卡限制使用"),

    port_use_in_monitor("选择的网卡已加入监控，请先移除监控"),
    
    lack_of_template_param("该租户缺少绑定网卡的配置项参数"),
    
    project_related_by_annother_school("租户已关联其它学校"),
    
    can_not_relate_root_project("不能关联根租户"),
    
    attach_only_one_manage_port("只能挂载一张管理网卡"),
    
    code_repeat("学校编码重复"),
    
    volume_not_exist_in_cloudos("硬盘在cloudos中不存在"),
    
    novavm_not_exist_in_cloudos("云主机在cloudos中不存在"),

    port_not_belong_vm("选择网卡不属于选择的云主机"),
    
    zabbix_login_failure("zabbix登录失败"),
    
    user_not_exist_in_zabbix("用户在zabbix不存在"),
    
    zabbix_create_failure("zabbix创建失败"),

    vnc_server_error("vnc服务不正常"),
    
    mac_repeat("mac地址重复"),
    
    monitor_exception("监控连接异常"),
    
    failure_in_monitor("monitor操作失败"),
    
    asset_code_repeat("编码值重复"),
    
    unallowed_status("不允许的状态"),
    
    still_in_monitor("还在监控中"),
    
    status_translate_error("使用中只能修改为退库或停用,非使用中只能修改为使用状态"),
    
    not_exist_in_monitor("监控中不存在该用户"),
    
    out_of_extcolumn_length("超过了扩展字段规定的长度"),
    
    netport_repeat("网口编号重复"),
    
    lack_of_master_message("文件缺少资产基本信息"),
    
    serial_or_assetId_repeat("序列号或资产编号重复"),
    
    trunk_master_not_null("本端设备名称不为空"),
    
    access_master_not_null("对端名称不为空"),
    
    trunk_port_error("本端端口为空或格式不正确"),
    
    access_port_not_null("对端端口为空或格式不正确"),
    
    trunk_master_not_exist("本端设备不存在"),
    
    access_master_not_exist("对端设备不存在"),
    
    serial_not_null("资产编号不能为空"),
    
    iloip_repeat("管理ip重复"),
    
    draw_row_or_col_over("机柜行或列超过机房最大行列"),
    
    draw_stop_status("机柜为停用状态"),

    not_found_netflow("不存在该月份的流量数据文件"),

    storage_item_not_exist("子存储不存在"),
    
    storage_item_not_belong_cluster("子存储归属其它集群"),
    
    unallow_alone_storage("不允许独立存储"),
    
    item_already_relate_asm("该设备已经关联资产"),
    
    asm_already_relate_item("该资产已经关联设备"),
    
    asset_type_conflict("堆叠与资产设备类型不一致"),
    
    serial_conflict("堆叠与资产设备序列号不一致"),
    
    file_content_error("文件内容错误"),
    
    still_relate_item("仍然关联子设备"),
    
    alone_asset_relate_one_master("独立设备只能关联一个资产"),
    
    database_can_not_relate_app("数据库不能直接挂载到应用"),

    not_found_parent("没找到归属类型"),

    config_parameter_error("配置参数错误"),

    host_router_format_error("主机路由格式错误"),
    
    dns_ip_format_error("DNS的ip格式错误"),
    ;

    private String opeValue;

    ResultType(String opeValue) {
        this.opeValue = opeValue;
    }

    public String getOpeValue() {
        return opeValue;
    }
}