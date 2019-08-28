package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.ClustersBiz;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.biz.Pools2HostBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.Clusters;
import com.h3c.iclouds.po.Cvm2Ove;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.po.Pools2Host;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/2/23.
 */
public class HostPoolQuartz {

    //全局变量

    private Integer totalMem;//总内存

    private Integer assignMem;//分配内存

    private Integer totalCpu;//总cpu

    private Integer assignCpu;//分配cpu

    private Integer totalVmCpu;//主机下虚拟机的总cpu

    private Integer totalVmMem;//主机下内存的总cpu

    private List<Pools2Host> pools2HostList = new ArrayList<>();//同步过来的主机池集合

    private List<Clusters> clustersList = new ArrayList<>();//同步过来的集群集合

    private List<Server2Ove> server2OveList = new ArrayList<>();//同步过来的主机集合

    private List<Server2Vm> server2VmList = new ArrayList<>();//同步过来的虚拟机池集合

    public Logger log = LoggerFactory.getLogger(HostPoolQuartz.class);

    @Resource
    private Pools2HostBiz pools2HostBiz;

    @Resource
    private ClustersBiz clustersBiz;

    @Resource
    private Server2OveBiz server2OveBiz;

    @Resource
    private Server2VmBiz server2VmBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<Cvm2Ove> cvm2OveDao;

    @Resource
    private InterfacesBiz interfacesBiz;

    /**
     * 执行同步任务
     */
    public void synchronization(){
    	ThreadContext.clear();
    	ThreadContext.set(ConfigProperty.LOG_WRITE_TYPE, ConfigProperty.LOG_WRITE_TYPE_STOP);
        Date date = new Date();
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) + "";
        String month = now.get(Calendar.MONTH) + 1 + "";
        String day = now.get(Calendar.DAY_OF_MONTH) + "";
        CasClient casClient = null;
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("type", "cas");
            List<Interfaces> interfacesList = interfacesBiz.findByMap(Interfaces.class, queryMap);
            if (StrUtils.checkCollection(interfacesList)) {
                for (Interfaces interfaces : interfacesList) {
                    String ip = interfaces.getIp();
                    String port = interfaces.getPort();
                    String userName = interfaces.getAdmin();
                    String password = interfaces.getPasswd();
                    casClient = new CasClient(ip, Integer.parseInt(port), userName, password, true);
                    String belongCasId = interfaces.getId();
                    totalCpu = 0;
                    assignCpu = 0;
                    totalMem = 0;
                    assignMem = 0;
                    pools2HostList.clear();
                    //初始化cvm数据
                    Cvm2Ove cvm2Ove = cvm2OveDao.findById(Cvm2Ove.class, belongCasId);
                    if (!StrUtils.checkParam(cvm2Ove)){//不存在则新建
                        cvm2Ove = new Cvm2Ove();
                        cvm2Ove.setId(belongCasId);
                        cvm2Ove.setCvmName(ip);
                        cvm2Ove.setDate(date);
                        cvm2Ove.setYear(year);
                        cvm2Ove.setMonth(month);
                        cvm2Ove.setDay(day);
                        cvm2OveDao.add(cvm2Ove);
                    }
                    JSONObject hpObject = casClient.get("/cas/casrs/hostpool/all");
                    if (ResourceHandle.judgeResponse(hpObject)){
                        JSONObject hpRecord = hpObject.getJSONObject("record");
                        try {
                            JSONArray hpArray = hpRecord.getJSONArray("hostPool");//获取主机池数组
                            if (StrUtils.checkParam(hpArray)){
                                for (int i = 0; i < hpArray.size(); i++) {
                                    JSONObject hpJson = hpArray.getJSONObject(i);
                                    synHostPool(hpJson, casClient, belongCasId);//同步主机池
                                }
                            }
                        } catch (Exception e) {
                            JSONObject hpJson = hpRecord.getJSONObject("hostPool");//只有一个时直接获取
                            if (StrUtils.checkParam(hpJson)){
                                synHostPool(hpJson, casClient, belongCasId);//同步主机池
                            }
                        }
                        //统计cvm下总内存、总cpu和已分配内存、cpu
                        cvm2Ove.setAssignCpu(assignCpu);
                        cvm2Ove.setAssignMem(assignMem);
                        cvm2Ove.setTotalCpu(totalCpu);
                        cvm2Ove.setTotalMem(totalMem);
                        cvm2Ove.setCpuUsage(((float)assignCpu/totalCpu * 100));
                        cvm2Ove.setMemUsage(((float)assignMem/totalMem * 100));
                        cvm2OveDao.update(cvm2Ove);
                        //该cas下所有的主机池
                        List<Pools2Host> totalPool2HostList = pools2HostBiz.findByPropertyName(Pools2Host.class, "belongCas", belongCasId);
                        if (StrUtils.checkParam(totalPool2HostList) && StrUtils.checkParam(pools2HostList)){
                            List<String> poolIds = new ArrayList<>();
                            for (Pools2Host pools2Host : pools2HostList) {
                                poolIds.add(pools2Host.getId());
                            }
                            for (Pools2Host pools2Host : totalPool2HostList) {
                                if (!poolIds.contains(pools2Host.getId())){//不在同步的集合里面时删除
                                    pools2HostBiz.synDelete(pools2Host);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(HostPoolQuartz.class, e);
        } finally {
        	if(casClient != null) {
    			casClient.closeClient(casClient.getCasClient());
    		}
        	ThreadContext.clear();
        }
    }

    /**
     * 同步主机池数据
     * @param hpJson
     */
    public void synHostPool(JSONObject hpJson, CasClient casClient, String belongCasId){
        clustersList.clear();
        server2OveList.clear();
        try {
            String casId = hpJson.getString("id");
            String name = hpJson.getString("name");
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("casId", casId);
            queryMap.put("belongCas", belongCasId);
            Pools2Host pools2Host = pools2HostBiz.singleByClass(Pools2Host.class, queryMap);
            if (!StrUtils.checkParam(pools2Host)){//不存在时新建
                pools2Host = new Pools2Host();
                pools2Host.setBelongCas(belongCasId);
                pools2Host.setCasId(casId);
                pools2Host.setPoolName(name);
                pools2HostBiz.synAdd(pools2Host);
            }
            pools2HostList.add(pools2Host);
            String hpId = pools2Host.getId();
            JSONObject hpChildObject = casClient.get("/cas/casrs/hostpool/" + casId + "/allChildNode");
            if (ResourceHandle.judgeResponse(hpChildObject)){
                JSONObject hpChildRecord = hpChildObject.getJSONObject("record");
                try {
                    JSONArray cluArray = hpChildRecord.getJSONArray("clusterList");//获取集群集合
                    if (StrUtils.checkParam(cluArray)){
                        for (int i = 0; i < cluArray.size(); i++) {
                            JSONObject cluJson = cluArray.getJSONObject(i);
                            synCluster(cluJson, hpId, casClient, belongCasId);
                        }
                    }
                } catch (Exception e) {
                    JSONObject cluJson = hpChildRecord.getJSONObject("clusterList");//直接获取（只有一个时）
                    if (StrUtils.checkParam(cluJson)){
                        synCluster(cluJson, hpId, casClient, belongCasId);
                    }
                }
                try {
                    JSONArray hostArray = hpChildRecord.getJSONArray("hostList");//获取主机集合
                    if (StrUtils.checkParam(hostArray)){
                        for (int i = 0; i < hostArray.size(); i++) {
                            JSONObject hostJson = hostArray.getJSONObject(i);
                            synHost(hostJson, hpId, null, casClient, belongCasId);
                        }
                    }
                } catch (Exception e) {
                    JSONObject hostJson = hpChildRecord.getJSONObject("hostList");//直接获取（只有一个时）
                    if (StrUtils.checkParam(hostJson)){
                        synHost(hostJson, hpId, null, casClient, belongCasId);
                    }
                }
                Map<String, String> query = new HashMap<>();
                query.put("belongCas", belongCasId);
                query.put("poolId", hpId);
                List<Server2Ove> totalServer2OveList = server2OveBiz.findByMap(Server2Ove.class, query);
                if (StrUtils.checkParam(totalServer2OveList) && StrUtils.checkParam(server2OveList)){
                    List<String> hostIds = new ArrayList<>();
                    for (Server2Ove Server2Ove : server2OveList) {
                        hostIds.add(Server2Ove.getId());
                    }
                    for (Server2Ove Server2Ove : totalServer2OveList) {//删除不存在的主机
                        if (!hostIds.contains(Server2Ove.getId())){
                            server2OveBiz.synDelete(Server2Ove);
                        }
                    }
                }
                query.remove("poolId");
                query.put("phostId", hpId);
                List<Clusters> totalClustersList = clustersBiz.findByMap(Clusters.class, query);
                if (StrUtils.checkParam(totalClustersList) && StrUtils.checkParam(clustersList)){
                    List<String> cluIds = new ArrayList<>();
                    for (Clusters clusters : clustersList) {
                        cluIds.add(clusters.getId());
                    }
                    for (Clusters clusters : totalClustersList) {//删除不存在的集群
                        if (!cluIds.contains(clusters.getId())){
                            clustersBiz.synDelete(clusters);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(HostPoolQuartz.class, e);
        }
    }

    /**
     * 同步集群数据
     * @param cluJson
     * @param hpId
     */
    public void synCluster(JSONObject cluJson, String hpId, CasClient casClient, String belongCasId){
        try {
            String casId = cluJson.getString("id");
            String name = cluJson.getString("name");
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("casId", casId);
            queryMap.put("belongCas", belongCasId);
            queryMap.put("phostId", hpId);
            Clusters clusters = clustersBiz.singleByClass(Clusters.class, queryMap);
            if (!StrUtils.checkParam(clusters)){//不存在时新建
                clusters = new Clusters();
                clusters.setCasId(casId);
                clusters.setBelongCas(belongCasId);
                clusters.setCname(name);
                clusters.setPhostId(hpId);
                clustersBiz.synAdd(clusters);
            }
            clustersList.add(clusters);
            String cluId = clusters.getId();
            Map<String, Object> params = new HashMap<>();
            params.put("clusterId", casId);
            JSONObject hostObject = casClient.get("/cas/casrs/host", params);
            if (ResourceHandle.judgeResponse(hostObject)){
                JSONObject hostRecord = hostObject.getJSONObject("record");
                try {
                    JSONArray hostArray = hostRecord.getJSONArray("host");//获取主机数组
                    if (StrUtils.checkParam(hostArray)){
                        for (int i = 0; i < hostArray.size(); i++) {
                            JSONObject hostJson = hostArray.getJSONObject(i);
                            synHost(hostJson, hpId, cluId, casClient, belongCasId);
                        }
                    }
                } catch (Exception e) {
                    JSONObject hostJson = hostRecord.getJSONObject("host");//直接获取（只有一个时）
                    if (StrUtils.checkParam(hostJson)){
                        synHost(hostJson, hpId, cluId, casClient, belongCasId);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(HostPoolQuartz.class, e);
        }
    }

    /**
     * 同步主机数据
     * @param hostJson
     * @param hpId
     * @param cluId
     */
    public void synHost(JSONObject hostJson, String hpId, String cluId, CasClient casClient, String belongCasId){
        server2VmList.clear();
        Date date = new Date();
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) + "";
        String month = now.get(Calendar.MONTH) + 1 + "";
        String day = now.get(Calendar.DAY_OF_MONTH) + "";
        try {
            String casId = hostJson.getString("id");
            JSONObject hostObject = casClient.get("/cas/casrs/host/id/" + casId);
            if (ResourceHandle.judgeResponse(hostObject)){
                hostJson = hostObject.getJSONObject("record");
                String name = hostJson.getString("name");
                String ip = hostJson.getString("ip");
                Integer cpus = hostJson.getInteger("cpuCount");
                Integer mems = hostJson.getInteger("memorySize");
                StringBuffer mac = null;
                try {
                    JSONArray pnicArray = hostJson.getJSONArray("pNIC");
                    if (StrUtils.checkParam(pnicArray)){
                        for (int i = 0; i < pnicArray.size(); i++) {
                            JSONObject pnicJson = pnicArray.getJSONObject(i);
                            mac = new StringBuffer();
                            mac.append(pnicJson.getString("macAddr") + ",");
                        }
                    }
                } catch (Exception e) {
                    JSONObject pnicJson = hostJson.getJSONObject("pNIC");
                    if (StrUtils.checkParam(pnicJson)){
                        mac = new StringBuffer();
                        mac.append(pnicJson.getString("macAddr") + ",");
                    }
                }
                totalCpu = totalCpu + cpus;
                totalMem = totalMem + mems;
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("casId", casId);
                queryMap.put("belongCas", belongCasId);
                Server2Ove server2Ove = server2OveBiz.singleByClass(Server2Ove.class, queryMap);
                if (!StrUtils.checkParam(server2Ove)){//不存在时新建
                    server2Ove = new Server2Ove();
                    server2Ove.setCasId(casId);
                    server2Ove.setDate(date);
                    server2Ove.setDay(day);
                    server2Ove.setYear(year);
                    server2Ove.setMonth(month);
                    server2Ove.setBelongCas(belongCasId);
                    server2Ove.setBelongId(belongCasId);
                    server2Ove.setCpus(cpus);
                    server2Ove.setRam(mems);
                    server2Ove.setCusterId(cluId);
                    server2Ove.setPoolId(hpId);
                    server2Ove.setHostName(name);
                    server2Ove.setIp(ip);
                    server2Ove.setCpuOverSize((float)0);
                    server2Ove.setRamOverSize((float)0);
                    if (StrUtils.checkParam(mac)){
                        server2Ove.setMac(mac.substring(0, mac.length() - 1));
                    }
                    server2OveBiz.synAdd(server2Ove);
                }
                server2OveList.add(server2Ove);
                String hostId = server2Ove.getId();
                Map<String, Object> params = new HashMap<>();
                params.put("hostId", casId);
                JSONObject vmObject = casClient.get("/cas/casrs/vm/vmList", params);
                if (ResourceHandle.judgeResponse(vmObject)){
                    JSONObject vmRecord = vmObject.getJSONObject("record");
                    totalVmCpu = 0;
                    totalVmMem = 0;
                    try {
                        JSONArray vmArray = vmRecord.getJSONArray("domain");//获取数组
                        if (StrUtils.checkParam(vmArray)){
                            for (int i = 0; i < vmArray.size(); i++) {
                                JSONObject vmJson = vmArray.getJSONObject(i);
                                synVm(vmJson, hostId, casClient, belongCasId);
                            }
                        }
                    } catch (Exception e) {
                        JSONObject vmJson = vmRecord.getJSONObject("domain");//直接获取（只有一个时）
                        if (StrUtils.checkParam(vmJson)){
                            synVm(vmJson, hostId, casClient, belongCasId);
                        }
                    }
                    //统计cpu和内存的超配记录
                    if (totalVmCpu > cpus){
                        server2Ove.setCpuOverSize((float)totalVmCpu - cpus);
                    }
                    if (totalVmMem > mems){
                        server2Ove.setRamOverSize((float)totalVmMem - mems);
                    }
                    assignCpu = assignCpu + totalVmCpu;
                    assignMem = assignMem + totalVmMem;
                    Map<String, String> query = new HashMap<>();
                    query.put("belongCas", belongCasId);
                    query.put("hostId", hostId);
                    List<Server2Vm> totalServer2VmList = server2VmBiz.findByMap(Server2Vm.class, query);
                    if (StrUtils.checkParam(totalServer2VmList) && StrUtils.checkParam(server2VmList)){
                        List<String> vmIds = new ArrayList<>();
                        for (Server2Vm vm : server2VmList) {
                            vmIds.add(vm.getId());
                        }
                        for (Server2Vm vm : totalServer2VmList) {//删除不存在的
                            if (!vmIds.contains(vm.getId())){
                                server2VmBiz.delete(vm);
                            }
                        }
                    }
                }
                Map<String, Object> query = new HashMap<>();
                query.put("belongCas", belongCasId);
                query.put("hostId", hostId);
                int vms = server2VmBiz.count(Server2Vm.class, query);
                server2Ove.setVms(vms);
                server2OveBiz.update(server2Ove);
            }
        } catch (Exception e) {
            LogUtils.exception(HostPoolQuartz.class, e);
        }
    }

    /**
     * 同步虚拟机数据
     * @param vmJson
     * @param hostId
     */
    public void synVm(JSONObject vmJson, String hostId, CasClient casClient, String belongCasId){
        Date date = new Date();
        try {
            String casId = vmJson.getString("id");
            JSONObject vmObject = casClient.get("/cas/casrs/vm/" + casId);
            if (ResourceHandle.judgeResponse(vmObject)){
                JSONObject vmJsonDetail = vmObject.getJSONObject("record");
                String uuid = vmJsonDetail.getString("uuid");//获取uuid
                Integer storages = 0;
                //统计虚拟机磁盘大小
                try {
                    JSONArray storageArray = vmJsonDetail.getJSONArray("storage");
                    if (StrUtils.checkParam(storageArray)){
                        for (int i = 0; i < storageArray.size(); i++) {
                            JSONObject storageJson = storageArray.getJSONObject(i);
                            Integer capacity = storageJson.getInteger("capacity");
                            storages = storages + capacity;
                        }
                    }
                } catch (Exception e) {
                    JSONObject storageJson = vmJsonDetail.getJSONObject("storage");
                    if (StrUtils.checkParam(storageJson)){
                        Integer capacity = storageJson.getInteger("capacity");
                        storages = storages + capacity;
                    }
                }
                String name = vmJson.getString("name");
                Integer cpus = vmJson.getInteger("cpu");
                Integer mems = vmJson.getInteger("memory");
                totalVmCpu = totalVmCpu + cpus;
                totalVmMem = totalVmMem + mems;
                String os = vmJson.getString("osDesc");
                String type = vmJson.getString("type");
                String note = vmJson.getString("description");
                String status = vmJson.getString("vmStatus");
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("hostId", hostId);
                queryMap.put("uuid", uuid);
                queryMap.put("casId", casId);
                queryMap.put("belongCas", belongCasId);
                Server2Vm server2Vm = server2VmBiz.singleByClass(Server2Vm.class, queryMap);
                if (!StrUtils.checkParam(server2Vm)){
                    server2Vm = new Server2Vm();
                    server2Vm.setHostId(hostId);
                    server2Vm.setUuid(uuid);
                    server2Vm.setVmName(name);
                    server2Vm.setCpu(cpus);
                    server2Vm.setMemory(mems);
                    server2Vm.setStorage(storages);
                    server2Vm.setLastSyncDate(date);
                    server2Vm.setNote(note);
                    server2Vm.setOs(os);
                    server2Vm.setStatus(status);
                    server2Vm.setSyncType(type);
                    server2Vm.setCasId(casId);
                    server2Vm.setBelongCas(belongCasId);
                    server2VmBiz.synAdd(server2Vm);
                }
                server2VmList.add(server2Vm);
            }
        } catch (Exception e) {
            LogUtils.exception(HostPoolQuartz.class, e);
        }
    }

}
