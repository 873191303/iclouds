package com.h3c.iclouds.task.novavm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.biz.NetworkBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.operate.CloudosNovaVm;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.Network;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.Task;
import com.h3c.iclouds.po.VmExtra;
import com.h3c.iclouds.task.BaseTask;
import com.h3c.iclouds.task.novavm.special.WriteFreeBSDIP;
import com.h3c.iclouds.utils.IpValidator;
import com.h3c.iclouds.utils.ResourceNovaHandle;
import com.h3c.iclouds.utils.Ssh2Utils;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.PwdUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 克隆云主机
 * 1.根据原云主机的信息，创建对应个数的云主机
 * 2.将创建好的云主机关机
 * 3.替换两个增量文件
 * 4.修改三级增量文件的指向
 * 5.云主机开机，修改云主机名称,修改云主机密码,修改云主机IP
 * @author zkf5485
 *
 */
public class NovaVmCloneTask extends BaseTask {

    private NovaVmBiz novaVmBiz = SpringContextHolder.getBean("novaVmBiz");

    private NetworkBiz networkBiz = SpringContextHolder.getBean("networkBiz");

    private BaseDAO<VmExtra> vmExtraDao = SpringContextHolder.getBean("baseDAO");

    private BaseDAO<Rules> rulesDao = SpringContextHolder.getBean("baseDAO");

    private Server2VmBiz server2VmBiz = SpringContextHolder.getBean("server2VmBiz");

    private int create_query_num = StrUtils.tranInteger(CacheSingleton.getInstance().getConfigValue("create_query_num"));

    public NovaVmCloneTask(Task task) {
        super(task);
        if(create_query_num == 0) {
            create_query_num = 100;
        }
    }

    private List<NovaVm> novaVms = new CopyOnWriteArrayList<>();

    private List<String> msgList = new ArrayList<>();

    private String flavorId;

    private String azoneName;

    private String netId;

    private String imageId;

    private String azoneId;

    private Integer vcpus;

    private Integer memory;

    private Integer ramdisk;

    private String osType;

    private String mask;

    private String gateway;

    private CasClient casClient;

    private String backingStore;

    private String storeFile;

    private CloudosNovaVm cloudosNovaVm = null;

    private String netmask;

    private final static JSONObject closeObj = JSONObject.parseObject("{\"os-stop\":\"null\"}");

    private final static JSONObject startObj = JSONObject.parseObject("{\"os-start\":\"null\"}");

    private Map<String, String> hostId2IP = new HashMap<>();

    /**
     * 删除命令
     */
    private final static String CMD_RM_RF = "rm -rf $target";

    /**
     * 拷贝命令
     */
    private final static String CMD_CP = "cp $source $target";

    /**
     * 拷贝命令
     */
    private final static String CMD_CHANGE_BASE = "qemu-img rebase -b $backfile $increment";

    /**
     * 修改主机名
     */
    private final static String CMD_MODIFY_HOSTNAME = "virsh qemu-agent-command $hostname --timeout 3000 '{\"execute\":\"guest-set-hostname\",\"arguments\": {\"hostname\":\"$target\"}}'";

    /**
     * 修改IP
     */
    private final static String CMD_MODIFY_IP = "virsh qemu-agent-command $hostname --timeout 3000 '{\"execute\":\"guest-set-ip\",\"arguments\":{\"mac\":\"$mac\",\"ip\":\"$ip\",\"mask\":\"$mask\",\"gateway\":\"$gateway\",\"dns\":[]}}'";

    /**
     * 修改密码
     */
    private final static String CMD_MODIFY_PASSWORD = "virsh qemu-agent-command $hostname --timeout 3000 '{\"execute\":\"guest-set-password\",\"arguments\": {\"username\":\"root\",\"password\":\"$password\"}}'";

    /**
     * 主线程，用于处理创建云主机前的数据处理准备及创建完云主机的信息更改
     */
    @Override
    protected String handle() {
        NovaVm sourceVm = null;
        boolean execSuccess = false;
        String resultValue = ConfigProperty.TASK_STATUS3_END_SUCCESS;
        try {
            cloudosNovaVm = new CloudosNovaVm(client);
            JSONObject inputObj = JSONObject.parseObject(task.getInput());
            scheCount = inputObj.getInteger("count");

            String novaId = inputObj.getString("novaId");
            String uuid = inputObj.getString("uuid");
            String name = inputObj.getString("name");
            mask = inputObj.getString("mask");
            gateway = inputObj.getString("gateway");
            flavorId = inputObj.getString("flavorId");
            azoneName = inputObj.getString("azoneName");
            netId = inputObj.getString("netId");
            imageId = inputObj.getString("imageId");
            azoneId = inputObj.getString("azoneId");

            vcpus = inputObj.getInteger("vcpus");
            memory = inputObj.getInteger("memory");
            ramdisk = inputObj.getInteger("ramdisk");

            Rules rule = this.rulesDao.findById(Rules.class, imageId);
            if(rule == null) {
                this.addMsg("Not find image[" + imageId + "] in iyun");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }
            osType = inputObj.getString("osType");

            sourceVm = novaVmBiz.findById(NovaVm.class, novaId);
            if(sourceVm == null) {
                this.addMsg("Not find vm[" + novaId + "] in iyun");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }

            Server2Vm server2Vm = server2VmBiz.singleByClass(Server2Vm.class, StrUtils.createMap("uuid", uuid));
            if(server2Vm == null) {
                this.addMsg("Not find vm[" + uuid + "] in cas");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }

            casClient = CasClient.createByCasId(CasClient.CAS_INTERFACE_ID);
            if(casClient == null) {
                this.addMsg("Get Cas Client failure");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }

            JSONObject baseCasVmObj = casClient.getStorePath(server2Vm.getCasId());
            if(baseCasVmObj == null) {
                this.addMsg("Get Cas vm store failure.");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }
            backingStore = baseCasVmObj.getString("backingStore");
            storeFile = baseCasVmObj.getString("storeFile");

            String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, task.getProjectId(), uuid);
            JSONObject server = HttpUtils.getJSONObject(this.client.get(uri), "server");
            if(server == null) {
                this.addMsg("Get vm in cloudos failure");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }
            // 云主机需要为关机状态
            String vmState = CloudosParams.getVmState(server.getString("status"));
            if(!"state_stop".equals(vmState)) {
                this.addMsg("Vm[" + sourceVm.getHostName() + "] status is " + vmState);
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }

            Network networkEntity = this.networkBiz.singleByClass(Network.class, StrUtils.createMap("cloudosId", netId));
            if(networkEntity == null) {
                this.addMsg("Get network failure");
                return ConfigProperty.TASK_STATUS4_END_FAILURE;
            }

            this.netmask = IpValidator.getMaskbyLen(IpValidator.getMask(networkEntity.getCidr()));

            List<Map<String, Object>> networks = new ArrayList<>();
            Map<String, Object> network = StrUtils.createMap("uuid", netId);
            networks.add(network);

            JSONArray security_groups = server.getJSONArray("security_groups");
            Map<String, Object> serverMap = StrUtils.createMap("name", name);
            serverMap.put("security_groups", security_groups);
            serverMap.put("adminPass", "1234567890");
            serverMap.put("flavorRef", flavorId);
            serverMap.put("max_count", 1);
            serverMap.put("min_count", 1);
            serverMap.put("imageRef", imageId);
            serverMap.put("availability_zone", azoneName);
            serverMap.put("security_groups", security_groups);
            serverMap.put("networks", networks);
            uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER, task.getProjectId());

            // 开始调度
            DispatchCreateVmThread dispatch = new DispatchCreateVmThread(uri, serverMap);
            CacheSingleton.getInstance().startThread(dispatch);
            // 等待2分钟
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int currentIndex = 0;
            while (!isFinish || this.novaVms.size() > currentIndex || this.execCount > 0) {
                int size = this.novaVms.size();
                if(size > currentIndex) {
                    currentIndex++;
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            execSuccess = true;
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e, "Clone vm failure");
            return ConfigProperty.TASK_STATUS4_END_FAILURE;
        } finally {
            if(!execSuccess) {
                resultValue = ConfigProperty.TASK_STATUS4_END_FAILURE;
                // 失败则还原配额
                ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
                ResultType resultType = resourceNovaHandle.updateQuota(flavorId, task.getProjectId(), false, this.scheCount);
                addMsg("Recycle quota, result: " + resultType.toString());
            }
            if(sourceVm != null) {
                sourceVm.setVmState(ConfigProperty.novaVmState.get(2));
                this.novaVmBiz.update(sourceVm);
            }

            StringBuffer resultBuffer = new StringBuffer();
            if(msgList.isEmpty()) {
                this.addMsg(ResultType.success.toString());
            }
            for (String msg : msgList) {
                resultBuffer.append(msg);
            }
            this.saveTask2Exec(resultBuffer.toString(), resultValue, 0);
        }
        return ConfigProperty.TASK_STATUS3_END_SUCCESS;
    }

    private boolean execCmd(Ssh2Utils ssh, String cmd, String hostName) {
        List<String> list = ssh.execCmd(cmd);
        if(list == null || list.isEmpty()) {
            this.addMsg("Exec cmd failure, cmd: " + cmd + ", name" + hostName);
        }
        String cmdResult = list.get(0);
        try {
            cmdResult = JSONObject.parseObject(cmdResult).getString("return");
            if(!"0".equals(cmdResult)) {
                cmdResult = null;
            }
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e, "Modify hostname failure");
            this.addMsg("Exec cmd failure, cmd: " + cmd + ", name" + hostName);
            cmdResult = null;
        }
        return cmdResult != null;
    }

//    private Ssh2Utils createCasHostSSH(String hostIp) {
//        if (null == hostIp) {
//            hostIp = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.ip");
//        }
//        String password = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.password");
//        String userName = CacheSingleton.getInstance().getConfigValue("iyun.cas.host.username");
//        this.addMsg("Connection to " + hostIp + ", user: " + userName);
//        Ssh2Utils ssh = Ssh2Utils.create(hostIp, userName, password);
//        if(!ssh.isAuthenticated()) {
//            this.addMsg("Connection to cas host error");
//            return null;
//        }
//        return ssh;
//    }

    /**
     * 计划创建个数
     */
    private int scheCount = 0;

    /**
     * 允许同时创建云主机个数
     */
    private final static int MAX_CREATE_COUNT = 3;

    /**
     * 正在执行的个数
     */
    private volatile int execCount = 0;

    /**
     * 失败的个数
     */
    private volatile int failureCount = 0;

    /**
     * 完成的个数
     */
    private volatile int finishCount = 0;

    /**
     * 调度结束的标志
     */
    private volatile boolean isFinish = false;

    /**
     * 用于创建云主机的调度
     * @author zkf5485
     *
     */
    private class DispatchCreateVmThread implements Runnable {

        private Map<String, Object> createObj;

        private String uri;

        public DispatchCreateVmThread(String uri, Map<String, Object> createObj) {
            this.createObj = createObj;
            this.uri = uri;
        }

        @Override
        public void run() {
            try {
                initThreadContext();	// 设置线程打印内容
                while (scheCount > finishCount) {
                    if(execCount >= MAX_CREATE_COUNT) {    // 大于最多执行次数
                        try {
                            TimeUnit.SECONDS.sleep(30);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ++execCount;
                        String createStr = JSONObject.toJSONString(createObj);
                        CreateVmThread createVmThread = new CreateVmThread(uri, JSONObject.parseObject(createStr), ++finishCount);
                        CacheSingleton.getInstance().startThread(createVmThread);
                    }
                }
            } catch (Exception e) {
                LogUtils.exception(this.getClass(), e, "Create Vm failure");
            } finally {
                ThreadContext.clear();
                isFinish = true;
            }
        }
    }

    /**
     * 真正创建云主机的线程
     * @author zkf5485
     *
     */
    private class CreateVmThread implements Runnable {

        private Map<String, Object> createObj;

        private String uri;

        private int num;

        public CreateVmThread(String uri, Map<String, Object> createObj, int num) {
            this.createObj = createObj;
            this.uri = uri;
            this.num = num;
        }

        @Override
        public void run() {
            boolean isSuccess = false;
            NovaVm novaVm = null;
            Ssh2Utils ssh = null;
            String password = PwdUtils.getPwd();
            try {
                initThreadContext();	// 设置线程打印内容
                String name = StrUtils.tranString(createObj.get("name")) + "-" + num;
                createObj.put("name", name);
                createObj.put("adminPass", password);	// 设置密码
                JSONObject server = null;
                // 创建尝试次数
                for (int i = 0; i < 3; i++) {
                    JSONObject jsonObj = clientQuery(CloudosParams.POST, uri, StrUtils.createMap("server", createObj));
                    server = HttpUtils.getJSONObject(jsonObj, "server");
                    if(server == null) {
                        addMsg("Create Vm failure, current num: [" + num + ":" + i + "], cloudos api message:" + jsonObj.toJSONString());
                        continue;
                    }
                    isSuccess = true;
                    break;
                }
                if(isSuccess) {
                    String serverId = server.getString("id");
                    String uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, task.getProjectId(), serverId);
                    String status = getVmStatus(uri, "BUILD", false);
                    if(status == null) {
                        addMsg("Create Vm failure, num: " + num);
                        isSuccess = false;
                    } else {
                        addMsg("Create Vm success, Vm name: [" + name + "], status: " + status);
//						String vmState = CloudosParams.getVmState(status);
                        novaVm = new NovaVm();
                        novaVm.setAzoneId(azoneId);
                        novaVm.setFlavorId(flavorId);
                        novaVm.setHostName(name);
                        novaVm.setHost("宿主机");
                        novaVm.setUuid(serverId);
                        novaVm.setVmState(ConfigProperty.novaVmState.get(19));
                        novaVm.setVcpus(vcpus);
                        novaVm.setMemory(memory);
                        novaVm.setRamdisk(ramdisk);
                        novaVm.setOsType(osType);
                        novaVm.setPowerState(1);
                        novaVm.setProjectId(task.getProjectId());
                        novaVm.setOwner(task.getCreatedBy());
                        novaVm.createdUser(task.getCreatedBy());
                        novaVm.setImageRef(imageId);
                        novaVmBiz.add(novaVm);

                        VmExtra vmExtra = new VmExtra();
                        vmExtra.setOsUser(novaVm.getOwner());
                        vmExtra.setId(novaVm.getId());
                        vmExtra.setSshKey(null);
                        // 修改bean的密码
                        String encryptPassword = PwdUtils.encrypt(password, novaVm.getId() + novaVm.getOwner());
                        vmExtra.setOsPasswd(encryptPassword);
                        vmExtraDao.add(vmExtra);

                        // 获取网络
                        uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_OSINTERFACE, task.getProjectId(), novaVm.getUuid());
                        JSONArray interfaceAttachments = HttpUtils.getJSONArray(clientQuery(CloudosParams.GET, uri, null), "interfaceAttachments");
                        if(StrUtils.checkCollection(interfaceAttachments)) {
                            for (int j = 0; j < interfaceAttachments.size(); j++) {
                                JSONObject interfaceAttachment = interfaceAttachments.getJSONObject(j);
                                cloudosNovaVm.savePort(novaVm, interfaceAttachment, task.getProjectId());
                            }
                        }
                        uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_ACTION, task.getProjectId(), novaVm.getUuid());
                        JSONObject closeResult = clientQuery(CloudosParams.POST, uri, closeObj);
                        String result = closeResult.getString("result");
                        // 设置关机状态
                        if(result.startsWith("2")) {
//                        	uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, task.getProjectId(), serverId);
//                            status = getVmStatus(uri, "SHUTOFF", true);
                            novaVm.setVmState(ConfigProperty.novaVmState.get(19));
                        } else {
                            novaVm.setVmState(ConfigProperty.novaVmState.get(4));
                            addMsg("Shutdown Vm[" + novaVm.getHostName() + "][" + novaVm.getId() + "] failure, result: " + closeResult.toJSONString());
                        }
                        novaVmBiz.update(novaVm);
                        // 多等待30秒，增加容错时间，防止云主机关闭过慢
                        TimeUnit.SECONDS.sleep(15);

                        try {
                            status = novaVm.getVmState();
                            String cloneUUID = novaVm.getUuid();
                            if(!ConfigProperty.novaVmState.get(19).equals(status)) {
                                addMsg("Vm state is: " + status + ", can't do next");
                                isSuccess = false;
                                return;
                            }
                            String casUri = "/cas/casrs/vm/vmList?uuid=" + cloneUUID;
                            JSONObject domain = HttpUtils.getJSONObject(queryCas(casUri, "get"), "domain");
                            if(domain == null) {
                                addMsg("Get vm in cas failure, uuid: " + cloneUUID + ", name" + novaVm.getHostName());
                                isSuccess = false;
                                return;
                            }

                            String hostIp = null;
                            String hostCasId = domain.getString("hostId");
                            if(hostId2IP.containsKey(hostCasId)) {
                                hostIp = hostId2IP.get(hostCasId);
                            } else {
                                JSONObject hostObj = queryCas(hostCasId, "hostIP");
                                if(hostObj == null) {
                                    addMsg("Query host [" + hostCasId + "] IP failure");
                                    isSuccess = false;
                                    return;
                                }
                                hostIp = hostObj.getString("ip");
                                hostId2IP.put(hostCasId, hostIp);
                            }
                            // TODO: 2017/6/13 由于测试环境的server地址不对，暂时写死
                            hostIp = "10.88.10.60";
                            ssh = Ssh2Utils.createCasHostSSH(hostIp);
                            if(ssh == null) {
                                addMsg("SSH connection to host ip [" + hostIp + "] failure");
                                isSuccess = false;
                                return;
                            }
                            String casVmId = domain.getString("id");
                            JSONObject casVmObj = queryCas(casVmId, "store");
                            String cloneBackingStore = casVmObj.getString("backingStore");
                            String cloneStoreFile = casVmObj.getString("storeFile");

                            String ipAddr = casVmObj.getString("ipAddr");
                            String mac = casVmObj.getString("mac");
                            String cloneName = casVmObj.getString("name");

                            // 删除文件
                            String cmd = CMD_RM_RF.replace("$target", cloneBackingStore);
                            if(ssh.execCmd(cmd) == null) {
                                addMsg("Exec cmd failure, cmd: " + cmd + ", name" + novaVm.getHostName());
                                isSuccess = false;
                                return;
                            }

                            TimeUnit.SECONDS.sleep(3);

                            cmd = CMD_RM_RF.replace("$target", cloneStoreFile);
                            if(ssh.execCmd(cmd) == null) {
                                addMsg("Exec cmd failure, cmd: " + cmd + ", name" + novaVm.getHostName());
                                isSuccess = false;
                                return;
                            }

                            TimeUnit.SECONDS.sleep(3);

                            // 拷贝文件
                            cmd = CMD_CP.replace("$source", storeFile).replace("$target", cloneStoreFile);
                            if(ssh.execCmd(cmd) == null) {
                                addMsg("Exec cmd failure, cmd: " + cmd + ", name" + novaVm.getHostName());
                                isSuccess = false;
                                return;
                            }

                            TimeUnit.SECONDS.sleep(3);

                            cmd = CMD_CP.replace("$source", backingStore).replace("$target", cloneBackingStore);
                            if(ssh.execCmd(cmd) == null) {
                                addMsg("Exec cmd failure, cmd: " + cmd + ", name" + novaVm.getHostName());
                                isSuccess = false;
                                return;
                            }

                            TimeUnit.SECONDS.sleep(3);

                            // 修改增量文件指向
                            cmd = CMD_CHANGE_BASE.replace("$backfile", cloneBackingStore).replace("$increment", cloneStoreFile);
                            if(ssh.execCmd(cmd) == null) {
                                addMsg("Exec cmd failure, cmd: " + cmd + ", name" + novaVm.getHostName());
                                isSuccess = false;
                                return;
                            }

                            TimeUnit.SECONDS.sleep(3);

                            // 为FreeBSD类型
                            if(StrUtils.isFreeBSDImage(osType)) {
                                addMsg("云主机：" + novaVm.getHostName() + "的类型为" + osType + ",使用挂载的方式修改信息");
                                Map<String, String> map = new HashMap<>();
                                map.put("hostname", novaVm.getHostName());
                                map.put("ip", ipAddr);
                                map.put("netmask", netmask);
                                try {
                                    List<String> list = WriteFreeBSDIP.handle(novaVm, map);
                                    addMsg("Modify freeBSD success, result:" + JSONObject.toJSONString(list));
                                } catch (MessageException e) {
                                    if (e.getResultCode() == null) {
                                        addMsg("Modify freeBSD failure, cause:" + e.getMessage());
                                    } else {
                                        addMsg("Modify freeBSD failure, cause:" + e.getResultCode());
                                    }
                                    isSuccess = false;
                                    return;
                                } catch (Exception e) {
                                    LogUtils.exception(this.getClass(), e, "修改FreeBSD信息异常");
                                    addMsg("修改FreeBSD信息异常");
                                    isSuccess = false;
                                    return;
                                }
                            }

                            uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_ACTION, task.getProjectId(), novaVm.getUuid());
                            closeResult = clientQuery(CloudosParams.POST, uri, startObj);
                            result = closeResult.getString("result");
                            if(result.startsWith("2")) {
                                uri = HttpUtils.getUrl(CloudosParams.CLOUDOS_API_SERVER_GET, task.getProjectId(), novaVm.getUuid());
                                status = getVmStatus(uri, "ACTIVE", true);
//	                            vmState = CloudosParams.getVmState(status);
//	                            entity.setVmState(vmState);

                            } else {
                                addMsg("Start Vm[" + novaVm.getHostName() + "][" + novaVm.getId() + "] failure, result: " + closeResult.toJSONString());
                                isSuccess = false;
                                return;
                            }

                            // 不为FreeBSD类型
                            if(!StrUtils.isFreeBSDImage(osType)) {
                                // 等待60秒，防止可能由于开机速度过慢导致后续命令下发失败
                                TimeUnit.SECONDS.sleep(60);

                                // 修改云主机名
                                cmd = CMD_MODIFY_HOSTNAME.replace("$hostname", cloneName).replace("$target", novaVm.getHostName());
                                if(!execCmd(ssh, cmd, novaVm.getHostName())) {
                                    isSuccess = false;
                                    addMsg("Modify hostname [" + novaVm.getHostName() + "][" + novaVm.getId() + "] failure");
                                    return;
                                }

                                TimeUnit.SECONDS.sleep(3);

                                // 修改密码
                                cmd = CMD_MODIFY_PASSWORD.replace("$hostname", cloneName).replace("$password", password);
                                if(!execCmd(ssh, cmd, novaVm.getHostName())) {
                                    isSuccess = false;
                                    addMsg("Modify password [" + novaVm.getHostName() + "][" + novaVm.getId() + "] failure");
                                    return;
                                }

                                TimeUnit.SECONDS.sleep(3);

                                // 修改IP
                                cmd = CMD_MODIFY_IP.replace("$hostname", cloneName)
                                        .replace("$mac", mac)
                                        .replace("$ip", ipAddr)
                                        .replace("$mask", mask)
                                        .replace("$gateway", gateway);
                                if(!execCmd(ssh, cmd, novaVm.getHostName())) {
                                    isSuccess = false;
                                    addMsg("Modify ip [" + novaVm.getHostName() + "][" + novaVm.getId() + "] failure");
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //克隆失败还原配额
                    ResourceNovaHandle resourceNovaHandle = new ResourceNovaHandle();
                    resourceNovaHandle.updateQuota(flavorId, task.getProjectId(), false, 1);
                    addMsg("Create Vm failure, num: " + num);
                }
            } catch (Exception e) {
                LogUtils.exception(this.getClass(), e, "Create Vm failure");
            } finally {
                if(ssh != null) {
                    ssh.close();
                }
                if(novaVm != null) {
                    if(!isSuccess) {	// 创建异常
                        if(!ConfigProperty.novaVmState.get(4).equals(novaVm.getVmState())) {
                            novaVm.setVmState(ConfigProperty.novaVmState.get(4));
                        }
                        ++failureCount;
                    } else {
                        novaVm.setVmState(ConfigProperty.novaVmState.get(1));
                    }
                    novaVmBiz.update(novaVm);
                } else {
                    ++failureCount;
                }
                novaVms.add(novaVm);
                --execCount;
                ThreadContext.clear();
            }
        }
    }

    public synchronized JSONObject queryCas(String params, String type) {
        if("get".equals(type)) {
            return casClient.get(params);
        } else if("store".equals(type)) {
            return casClient.getStorePath(params);
        } else if("hostIP".equals(type)) {
            return casClient.getHostIpById(params);
        }
        return null;
    }

    /**
     * 获取云主机状态
     * @param uri
     * @param targetStatus
     * @param isEqual       是否需要一致性匹配：true:需要一致匹配，false：需要不一致匹配
     * @return
     */
    public String getVmStatus(String uri, String targetStatus, boolean isEqual) {
        String status = null;
        for (int i = 0; i < create_query_num; i++) {
            JSONObject jsonObj = clientQuery(CloudosParams.GET, uri, null);
            JSONObject server = HttpUtils.getJSONObject(jsonObj, "server");
            if(server == null) {
                break;
            }
            status = server.getString("status");
            if (null != status) {
                if(isEqual) {   // 需要与目标的一致
                    if(targetStatus.equals(status)) {
                        break;
                    }
                } else {
                    if(!targetStatus.equals(status)) {
                        break;
                    }
                }
            }
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    private void addMsg(String msg) {
        this.msgList.add(msg + ".</br>");
    }

}
