package com.h3c.iclouds.check;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseLogs;
import com.h3c.iclouds.base.SpringContextHolder;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.client.zabbix.ZabbixDefine;
import com.h3c.iclouds.po.HealthyInstance;
import com.h3c.iclouds.po.HealthyType2Item;
import com.h3c.iclouds.po.HealthyValue;
import com.h3c.iclouds.po.SlaInstance2Original;
import com.h3c.iclouds.utils.StrUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/8/29.
 * 脚本的方式获取监控结果，
 * 脚本执行服务器为Zabbix Server
 * 使用Zabbix Api的script方式
 */
public abstract class BaseCheck extends BaseLogs {

    HealthyInstance entity;

    JSONObject jsonObject;

    String ip;

    Integer port;

    ZabbixApi zabbixApi;

    Map<String, HealthyType2Item> type2ItemMap = new HashMap();

    CacheSingleton singleton = CacheSingleton.getInstance();

    BaseDAO<SlaInstance2Original> originDao = SpringContextHolder.getBean("baseDAO");

    BaseDAO<HealthyValue> historyDao = SpringContextHolder.getBean("baseDAO");

    Date collectTime = new Date();

    /**
     * 根据实体创建
     * @param entity
     * @return
     */
    public static BaseCheck createByEntity(HealthyInstance entity) {
        JSONObject obj = JSONObject.parseObject(entity.getConfig());
        BaseCheck check = null;
        switch (entity.getType()) {
            case CloudOSCheck.name:
                check = new CloudOSCheck(obj, entity);
                break;
            case CvmCheck.name:
                check = new CvmCheck(obj, entity);
                break;
            case FcSanCheck.name:
                check = new FcSanCheck(obj, entity);
                break;
            case IpSanCheck.name:
                check = new IpSanCheck(obj, entity);
                break;
            case NetworkCheck.name:
                check = new NetworkCheck(obj, entity);
                break;
            case OpenStackCheck.name:
                check = new OpenStackCheck(obj, entity);
                break;
            case SdnCheck.name:
                check = new SdnCheck(obj, entity);
                break;
        }
        return check;
    }

    public BaseCheck(JSONObject params, HealthyInstance entity) {
        this.jsonObject = params;
        this.entity = entity;
        this.ip = params.getString("IP");
        this.port = params.getInteger("port");

        // 获取健康度指标管理
        BaseDAO<HealthyType2Item> type2itemDao = SpringContextHolder.getBean("baseDAO");
        List<HealthyType2Item> type2Items = type2itemDao.findByPropertyName(HealthyType2Item.class, "type", entity.getType());
        if(StrUtils.checkCollection(type2Items)) {
            type2Items.forEach(type2Item -> type2ItemMap.put(type2Item.getItemName(), type2Item));
        }
    }

    public abstract String getName();

    /**
     * 默认验证，按照ping/telnet，权重为0.5/0.5
     * @return
     */
    public Double check() {
        String result = this.excuteScript();
        if (result == null) {    // 未能获取到结果
            return 0.;
        }
        Double ping;
        Double telnet;
        try {
            String[] array = result.split("\\n");
            ping = this.ping(array[0], .5);
            telnet = this.telnet(array[1], .5);
            if (ping == null || telnet == null) {    //  ping 或 telnet为nul则强制为0
                return 0.;
            }
        } catch (Exception e) {
            this.exception(e, "计算健康度失败, 实例id: " + this.entity.getId());
            return 0.;
        }
        return ping + telnet;
    }

    /**
     * 默认脚本为ping+telnet
     * @return
     */
    public String getCommand() {
        // 不存在则创建
        StringBuilder builder = new StringBuilder();
        // fping $ip
        builder.append("fping ").append(this.ip).append(";");
        // zabbix_get -s 127.0.0.1 -k net.tcp.port[$ip,$port]
        builder.append("zabbix_get -s 127.0.0.1 -k net.tcp.port[").append(this.ip).append(",").append(this.port).append("]");
        return builder.toString();
    }

    /**
     * 保存最后计算值
     * @param value
     */
    public void saveHistory(Double value) {
        HealthyValue history = new HealthyValue();
        history.setCollectTime(collectTime);
        history.setUpdatedDate(collectTime);
        history.setCreatedDate(collectTime);

        history.setInstanceId(this.entity.getId());

        history.setHealthValue(value);
        historyDao.add(history);
    }

    /**
     * 保存根数据源
     * @param value
     * @param weight
     * @param itemId
     */
    void saveOrigin(Double value, Double weight, String itemId) {
        SlaInstance2Original origin = new SlaInstance2Original();

        origin.setWeight(weight);
        origin.setItemValue(value);
        origin.setItemId(itemId);

        origin.setInstanceId(this.entity.getId());

        origin.setCollectTime(collectTime);
        origin.setUpdatedDate(collectTime);
        origin.setCreatedDate(collectTime);
        this.originDao.add(origin);
    }

    String getDefaultMonitorId() {
        String defaultMonitorId = singleton.getDefaultMonitorId();
        if(defaultMonitorId != null) {
            return defaultMonitorId;
        }
        ZabbixApi zabbixApi = this.zabbixApi;
        // 是否已经存在
        Map<String, Object> filter = StrUtils.createMap("host", singleton.getMonitorApi("monitor.default.name"));
        Map<String, Object> queryMap = StrUtils.createMap("filter", filter);
        queryMap.put("output", new String[]{"hostid"});   // 只查询单个字段
        JSONObject jsonObject = zabbixApi.get(ZabbixDefine.HOST, queryMap);
        if(ZabbixApi.requestSuccess(jsonObject)) {
            JSONArray array = jsonObject.getJSONArray("result");
            if(StrUtils.checkCollection(array)) {
                defaultMonitorId = array.getJSONObject(0).getString("hostid");
                singleton.setDefaultMonitorId(defaultMonitorId);
            }
        }
        return defaultMonitorId;
    }

    String getScriptId(String command) {
        return this.getScriptId(command, "1");
    }

    /**
     * 获取执行脚本id
     * @return
     */
    String getScriptId(String command, String executeOn) {
        String scriptId = ZabbixApi.getScriptId(this.entity.getId(), this.zabbixApi);
        if(scriptId != null) {
            return scriptId;
        }

        Map<String, Object> createMap = StrUtils.createMap("name", this.entity.getId());
        createMap.put("command", command);
        createMap.put("execute_on", executeOn);
        jsonObject = zabbixApi.create(ZabbixDefine.SCRIPT, createMap);
        if(ZabbixApi.requestSuccess(jsonObject)) {
            scriptId = ZabbixApi.getCreateId(jsonObject, "scriptids");
        } else {
            this.warn("创建Zabbix脚本失败, 创建对象[" + this.entity.getId() + "], 创建结果:" + jsonObject.toString());
        }
        return scriptId;
    }

    /**
     * telnet得分
     * @param value
     * @return
     */
    Double telnet(String value, double defaultWeight) {
        HealthyType2Item type2Item = this.type2ItemMap.get("telnet");
        double weight = type2Item == null ? defaultWeight : type2Item.getWeight();
        String itemId = type2Item == null ? null : type2Item.getId();

        if(value.equals("0")) {
            this.saveOrigin(0., weight, itemId);
            return null;
        }
        this.saveOrigin(1., weight, itemId);
        return weight;
    }

    /**
     * cpu使用率得分
     * 81-100   ~   0.6
     * 61-80    ~   0.8
     * 0-60     ~   1
     * @param value
     * @return
     */
    Double cpu(Double value) {
        HealthyType2Item type2Item = this.type2ItemMap.get("cpu");
        double weight = type2Item == null ? .1 : type2Item.getWeight();
        String itemId = type2Item == null ? null : type2Item.getId();

        double calc;
        if(value >= 81.) {
            calc = 0.6;
        } else if(value >= 61.) {
            calc = 0.8;
        } else {
            calc = 1.;
        }

        this.saveOrigin(calc, weight, itemId);
        return weight * calc;
    }

    /**
     * ping得分
     * @param value
     * @return
     */
    Double ping(String value, double defaultWeight) {
        HealthyType2Item type2Item = this.type2ItemMap.get("ping");
        double weight = type2Item == null ? defaultWeight : type2Item.getWeight();
        String itemId = type2Item == null ? null : type2Item.getId();

        if(!value.contains("alive")) {   // 正常的标志
            this.saveOrigin(0., weight, itemId);
            return null;    // 不正常则返回空
        }
        this.saveOrigin(1., weight, itemId);
        return weight;
    }

    /**
     * 执行脚本，使用Zabbix下发，
     * 不存在脚本则使用getCommand方法创建脚本
     * @return
     */
    String excuteScript() {
        String scriptId = this.getScriptId(this.getCommand());
        if(scriptId == null) {
            this.warn("未获取到Zabbix脚本id");
            return null;
        }
        String defaultMonitorId = this.getDefaultMonitorId();
        if(defaultMonitorId == null) {
            this.warn("未获取到Zabbix默认主机id");
            return null;
        }
        String result = null;
        try {
            ZabbixApi zabbixApi = this.zabbixApi;
            Map<String, Object> excuteMap = StrUtils.createMap("scriptid", scriptId);
            excuteMap.put("hostid", defaultMonitorId);
            JSONObject jsonObject = zabbixApi.oper(ZabbixDefine.SCRIPT, ZabbixDefine.EXECUTE, excuteMap);
            if(!ZabbixApi.requestSuccess(jsonObject)) {
                this.warn("执行脚本失败, 失败原因: " + jsonObject.toString());
                return null;
            }
            result = jsonObject.getJSONObject("result").getString("value");
        } catch (Exception e) {
            this.exception(e, "执行脚本失败");
        }
        return result;
    }

    public void setZabbixApi(ZabbixApi zabbixApi) {
        this.zabbixApi = zabbixApi;
    }
}
