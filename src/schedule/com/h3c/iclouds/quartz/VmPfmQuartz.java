package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.biz.NovaVmBiz;
import com.h3c.iclouds.biz.PfmValueBiz;
import com.h3c.iclouds.biz.Server2VmBiz;
import com.h3c.iclouds.biz.SpePortBiz;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.client.zabbix.ZabbixDefine;
import com.h3c.iclouds.po.BasePfmValue2History;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.po.PfmValue1D;
import com.h3c.iclouds.po.PfmValue1H;
import com.h3c.iclouds.po.PfmValue6H;
import com.h3c.iclouds.po.Rules;
import com.h3c.iclouds.po.Server2Vm;
import com.h3c.iclouds.po.SpePort;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/3/1.
 */
public class VmPfmQuartz {

    @Resource
    private Server2VmBiz server2VmBiz;

    @Resource
    private PfmValueBiz pfmValueBiz;

    @Resource
    private InterfacesBiz interfacesBiz;
    
    @Resource
    private SqlQueryBiz sqlQueryBiz;
    
	@Resource(name = "baseDAO")
	private BaseDAO<BasePfmValue2History> baseDAO;

    @Resource
    private NovaVmBiz novaVmBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<Rules> rulesDao;

    @Resource
    private SpePortBiz spePortBiz;

    /**
     * 同步性能数据
     */
    @SuppressWarnings("unused")
	public void synchronization(){
    	ThreadContext.clear();
    	ThreadContext.set(ConfigProperty.LOG_WRITE_TYPE, ConfigProperty.LOG_WRITE_TYPE_STOP);
    	server2VmBiz = SpringContextHolder.getBean("server2VmBiz");
    	pfmValueBiz = SpringContextHolder.getBean("pfmValueBiz");
    	interfacesBiz = SpringContextHolder.getBean("interfacesBiz");
    	sqlQueryBiz = SpringContextHolder.getBean("sqlQueryBiz");
    	baseDAO = SpringContextHolder.getBean("baseDAO");
    	CasClient casClient = null;
        ZabbixApi zabbixApi = ZabbixApi.createAdmin();
    	try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("type", "cas");
            List<Interfaces> interfacesList = interfacesBiz.findByMap(Interfaces.class, queryMap);
            if (StrUtils.checkParam(interfacesList)) {
            	boolean alreadyMove = false;	// 是否经过压缩数据
                for (Interfaces interfaces : interfacesList) {
                	boolean checkTime = false;	// 是否经过压缩数据
                	Date collectTime = new Date();
                	
                    String ip = interfaces.getIp();
                    String port = interfaces.getPort();
                    String userName = interfaces.getAdmin();
                    String password = interfaces.getPasswd();
                    casClient = new CasClient(ip, Integer.parseInt(port), userName, password, true);
                    String belongCasId = interfaces.getId();
                    List<Server2Vm> server2Vms = server2VmBiz.findByPropertyName(Server2Vm.class, "belongCas", belongCasId);
                    if (StrUtils.checkCollection(server2Vms)){
                        for (Server2Vm server2Vm : server2Vms) {
                            NovaVm entity = this.novaVmBiz.singleByClass(NovaVm.class, StrUtils.createMap("uuid", server2Vm.getUuid()));
                            if(entity == null) {    // 找不到云主机则不做后续处理
                                continue;
                            }

                            SpePort spePort = spePortBiz.singleByClass(SpePort.class, StrUtils.createMap("uuid", server2Vm.getUuid()));
                            // 存在监控对象则不查询cas
                            if(spePort != null && StrUtils.checkParam(spePort.getMonitorId())) {
                                if(zabbixApi == null) {
                                    LogUtils.warn(this.getClass(), "获取zabbix连接失败");
                                } else {
                                    // 正确保存了监控数据则不做cas查询
                                    if(this.saveByZabbix(server2Vm, entity, spePort, zabbixApi)) {
                                        continue;
                                    }
                                }
                            }
                            String casId = server2Vm.getCasId();
                            String uuid = server2Vm.getUuid();
                            String url = "/cas/casrs/vm/id/" + casId + "/monitor";
                            JSONObject monitorObject = casClient.get(url);
                            if (ResourceHandle.judgeResponse(monitorObject)){
                                JSONObject monitorJson = monitorObject.getJSONObject("record");
                                try {
                                    JSONObject diskJson = monitorJson.getJSONObject("disk");
                                    if (StrUtils.checkParam(diskJson)){
                                        collectTime = syncDisk(diskJson, uuid, collectTime);
                                    }
                                } catch (Exception e) {
                                    JSONArray diskArray = monitorJson.getJSONArray("disk");
                                    if (StrUtils.checkParam(diskArray)){
                                        for (int i = 0; i < diskArray.size(); i++) {
                                            JSONObject diskJson = diskArray.getJSONObject(i);
                                            collectTime = syncDisk(diskJson, uuid, collectTime);
                                            //TODO 多个disk的处理
                                            break;
                                        }
                                    }
                                }
                                try {
                                    JSONObject netJson = monitorJson.getJSONObject("net");
                                    if (StrUtils.checkParam(netJson, uuid)){
                                        collectTime = syncNet(netJson, uuid, collectTime);
                                    }
                                } catch (Exception e) {
                                    JSONArray netArray = monitorJson.getJSONArray("net");
                                    if (StrUtils.checkParam(netArray)){
                                        for (int i = 0; i < netArray.size(); i++) {
                                            JSONObject netJson = netArray.getJSONObject(i);
                                            collectTime = syncNet(netJson, uuid, collectTime);
                                            //TODO 多个net的处理
                                            break;
                                        }
                                    }
                                }
                                try {
                                    //TODO 逻辑硬盘的数据同步
                                    JSONObject partitionJson = monitorJson.getJSONObject("partition");
                                    if (StrUtils.checkParam(partitionJson)){
                                        //syncPartition(partitionJson, uuid, collectTime);
                                    }
                                } catch (Exception e) {
                                    JSONArray partitionArray = monitorJson.getJSONArray("partition");
                                    if (StrUtils.checkParam(partitionArray)){
                                        for (int i = 0; i < partitionArray.size(); i++) {
                                            JSONObject partitionJson = partitionArray.getJSONObject(i);
                                            //syncPartition(partitionJson, uuid, collectTime);
                                        }
                                    }
                                }
                                Float cpuRate = monitorJson.getFloat("cpuRate");//cpu利用率
                                pfmValueBiz.save(uuid, "vm", cpuRate, "cpuRate", collectTime);
                                Float memRate = monitorJson.getFloat("memRate");//内存利用率
                                pfmValueBiz.save(uuid, "vm", memRate, "memRate", collectTime);
                            }
                        }
                    }

                    if(!checkTime) {
                    	checkTime = true;
                    	alreadyMove = this.checkTime(collectTime, interfaces.getId(), alreadyMove);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        } finally {
        	if(casClient != null) {
        		casClient.closeClient(casClient.getCasClient());
        	}
            if(zabbixApi != null) {
                zabbixApi.logout();
            }
        	ThreadContext.clear();
        }
    }

    /**
     * 保存主机在zabbix里面的监控数据
     * @param server2Vm
     * @param novaVm
     * @param spePort
     * @return  true:正确保存了，false:中间出现异常，继续采用cas的方式保存
     */
    private boolean saveByZabbix(Server2Vm server2Vm, NovaVm novaVm, SpePort spePort, ZabbixApi zabbixApi) {
        String keyProfix = "tonghuashun.centos";
        if(StrUtils.checkParam(novaVm.getImageRef())) {
            Rules image = this.rulesDao.findById(Rules.class, novaVm.getImageRef());
            if(image != null && StrUtils.isFreeBSDImage(image.getOsType())) {
                keyProfix = "tonghuashun.freebsd";
            }
        }
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : CacheSingleton.getInstance().getTonghuashunConfigMap().entrySet()) {
            if(entry.getKey().startsWith(keyProfix)) {
                values.add(entry.getValue());
            }
        }
        if(values.isEmpty()) {
            LogUtils.warn(this.getClass(), "未设置监控类型");
            return false;
        }
        Map<String, Object> queryMap = StrUtils.createMap("hostids", spePort.getMonitorId());
        for (String key : values) {
            String[] array = key.split("\\|\\|");
            String zabbixKey = array[1];
            Date collectTime = null;
            Float lastvalue = 0f;
            Long lastclock = null;
            if(zabbixKey.contains("$$")) {
                String profix = zabbixKey.split("\\$\\$")[0];
                String suffix = zabbixKey.split("\\$\\$")[1];
                Map<String, Object> search = StrUtils.createMap("key_", profix);
                queryMap.put("search", search);
                JSONObject jsonObject = zabbixApi.get(ZabbixDefine.ITEM, queryMap);
                queryMap.remove("search");
                JSONArray result = HttpUtils.getCasJSONArray(jsonObject, "result");
                if(StrUtils.checkCollection(result)) {
                    lastclock = result.getJSONObject(0).getLong("lastclock");
                    for(int i = 0; i < result.size(); i++) {
                        JSONObject valueObj = result.getJSONObject(i);
                        String key_ = valueObj.getString("key_");
                        if((!suffix.contains(",") && !key_.contains(",")) ||
                                (suffix.contains(",") && key_.contains(suffix))) {
                            Float tempValue = valueObj.getFloat("lastvalue");
                            if(tempValue != null) {
                                lastvalue += tempValue;
                            }
                        }
                    }
                }
            } else {
                Map<String, Object> filter = StrUtils.createMap("key_", zabbixKey);
                queryMap.put("filter", filter);
                JSONObject jsonObject = zabbixApi.get(ZabbixDefine.ITEM, queryMap);
                queryMap.remove("filter");
                JSONObject result = HttpUtils.getCasJSONObject(jsonObject, "result");
                if(result != null) {
                    lastclock = result.getLong("lastclock");
                    lastvalue = result.getFloat("lastvalue");
                }
            }
            if(lastclock != null && lastclock.intValue() != 0) {
                if(lastvalue != null) {
                    collectTime = new Date(lastclock * 1000);
                }
                if(array.length > 2) {
                    if(array[2].equals("*100")) {
                        lastvalue *= 100;
                    }
                }
            }

            if(collectTime != null) {
                pfmValueBiz.save(server2Vm.getUuid(), "vm", lastvalue, array[0], collectTime, true);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String key = "net_writeFlow||net.if.in[$$]";
        String[] array = key.split("\\|\\|");
        System.out.println(array[0]);
        ZabbixApi zabbixApi = ZabbixApi.createAdmin();
        Map<String, Object> queryMap = StrUtils.createMap("hostids", "10381");
        Map<String, Object> filter = StrUtils.createMap("key_", "system.cpu.utils[]");
        queryMap.put("filter", filter);
        JSONObject jsonObject = zabbixApi.get(ZabbixDefine.ITEM, queryMap);
        System.out.println(jsonObject);
//        JSONObject result = HttpUtils.getCasJSONObject(jsonObject, "result");
//        System.out.println(result);
    }

    public Date syncDisk(JSONObject diskJson, String uuid, Date collectTime){
        try {
//            String diskName = diskJson.getString("device");//磁盘镜像物理名称
            Long time = diskJson.getLong("time") * 1000;
            collectTime = new Date(time);
            Float ioState = diskJson.getFloat("ioStat");//I/O吞吐量
            pfmValueBiz.save(uuid, "vm", ioState, "disk_ioState", collectTime);
            Float readState = diskJson.getFloat("readStat");//I/O读吞吐量
            pfmValueBiz.save(uuid, "vm", readState, "disk_readState", collectTime);
            Float writeState = diskJson.getFloat("writeStat");//I/O写吞吐量
            pfmValueBiz.save(uuid, "vm", writeState, "disk_writeState", collectTime);
            Float readLatency = diskJson.getFloat("readLatency");//磁盘读IOPS
            pfmValueBiz.save(uuid, "vm", readLatency, "disk_readLatency", collectTime);
            Float writeLatency = diskJson.getFloat("writeLatency");//磁盘写IOPS
            pfmValueBiz.save(uuid, "vm", writeLatency, "disk_writeLatency", collectTime);
            Float readRequest = diskJson.getFloat("readReqest");//磁盘I/O读延迟
            pfmValueBiz.save(uuid, "vm", readRequest, "disk_readReqest", collectTime);
            Float writeReqest = diskJson.getFloat("writeReqest");//磁盘I/O写延迟
            pfmValueBiz.save(uuid, "vm", writeReqest, "disk_writeReqest", collectTime);
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
        return collectTime;
    }

    public Date syncNet(JSONObject netJson, String uuid, Date collectTime){
        try {
//            String mac = netJson.getString("mac");//网卡MAC地址
            if (!StrUtils.checkParam(collectTime)){
                Long time = netJson.getLong("time") * 1000;
                collectTime = new Date(time);
            }
            Float readFlow = netJson.getFloat("readFlow");//网卡读流量
            pfmValueBiz.save(uuid, "vm", readFlow, "net_readFlow", collectTime);
            Float writeFlow = netJson.getFloat("writeFlow");//网卡写流量
            pfmValueBiz.save(uuid, "vm", writeFlow, "net_writeFlow", collectTime);
            Float readPackets = netJson.getFloat("readPackets");//网卡读报文数
            pfmValueBiz.save(uuid, "vm", readPackets, "net_readPackets", collectTime);
            Float writePackets = netJson.getFloat("writePackets");//网卡写报文数
            pfmValueBiz.save(uuid, "vm", writePackets, "net_writePackets", collectTime);
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
        return collectTime;
    }

    public void syncPartition(JSONObject partitionJson, String uuid, Date collectTime){
        try {
//            String device = partitionJson.getString("device");//逻辑磁盘名称
            Float usage = partitionJson.getFloat("usage");//磁盘利用率
            pfmValueBiz.save(uuid, "vm", usage, "usage", collectTime);
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
    }

    /**
     * 检查当前时间，是否要做分表，按周分表
     * 				是否要做压缩数据
     */
    public boolean checkTime(Date now, String interfaceId, boolean alreadyMove) {
    	Calendar calDate = Calendar.getInstance();
    	calDate.setTime(now);
    	int minute = calDate.get(Calendar.MINUTE);
    	if(minute < 5) {	// 开始节点
    		calDate.set(Calendar.SECOND, 0);	// 秒设置为0
    		calDate.set(Calendar.MINUTE, 0);	// 分设置为0
    		int hour = calDate.get(Calendar.HOUR_OF_DAY);
    		if(hour == 0 && !alreadyMove) {	// 需要做24小时压缩
    			alreadyMove = true;
    			this.saveCondense(calDate, 24, interfaceId);
    			String date = DateUtils.getDate(now, DateUtils.dateFormat);
    			List<Map<String, Object>> list = sqlQueryBiz.queryBySql(" SELECT mova_data('" + date + "');");
        		if(!list.isEmpty()) {
        			Map<String, Object> map = list.get(0);
        			LogUtils.warn(this.getClass(), "Move data result: " + map.toString());
        		}
    		}
    		if(hour % 6 == 0) {	// 需要做6小时压缩
    			this.saveCondense(calDate, 6, interfaceId);
    		}
    		// 需要做1小时压缩
    		this.saveCondense(calDate, 1, interfaceId);
    	}
    	return alreadyMove;
    }

    @SuppressWarnings("unchecked")
	private void saveCondense(Calendar calDate, int interval, String interfaceId) {
    	Date endTime = calDate.getTime();
    	calDate.add(Calendar.HOUR_OF_DAY, - interval);
    	Date startTime = calDate.getTime();
    	// 还原时间
    	calDate.add(Calendar.HOUR_OF_DAY, interval);
    	
    	Map<String, Object> queryMap = StrUtils.createMap("STARTTIME", startTime);
    	queryMap.put("ENDTIME", endTime);
    	queryMap.put("BELCAS", interfaceId);
    	List<Map<String, Object>> list = this.sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_PFM_GROUP_VALUE, queryMap);
    	
    	if(StrUtils.checkCollection(list)) {
    		BasePfmValue2History entity = null;
        	for (Map<String, Object> map : list) {
        		Float avg = StrUtils.tranFloat(map.get("v_avg"));
        		Float max = StrUtils.tranFloat(map.get("v_max"));
        		Float min = StrUtils.tranFloat(map.get("v_min"));
        		switch (interval) {
	    			case 24:
	    				entity = new PfmValue1D(max, min);
	    				break;
	    			case 6:
	    				entity = new PfmValue6H(max, min);
	    				break;
	    			case 1:
	    				entity = new PfmValue1H(max, min);
	    				break;
	    			default:
	    				break;
        		}
        		if(entity != null) {
        			entity.setUuid(StrUtils.tranString(map.get("uuid")));
            		entity.setResType(StrUtils.tranString(map.get("restype")));
            		entity.setItemId(StrUtils.tranString(map.get("item")));
            		entity.setKeyValue(avg);
            		entity.setCollectTime(endTime);
            		entity.setCreatedDate(endTime);
            		entity.setUpdatedDate(endTime);
            		this.baseDAO.add(entity);
        		}
			}
    	}
    }
}
