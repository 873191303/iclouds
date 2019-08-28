package com.h3c.iclouds.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.biz.PolicieRuleBiz;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.PolicieRuleDao;
import com.h3c.iclouds.operate.CloudosClient;
import com.h3c.iclouds.operate.CloudosParams;
import com.h3c.iclouds.po.PolicieRule;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yKF7317 on 2016/11/23.
 */
@Service("policieRuleBiz")
public class PolicieRuleBizImpl extends BaseBizImpl<PolicieRule> implements PolicieRuleBiz {

    @Resource
    private PolicieRuleDao policieRuleDao;

    @Override
    public PageModel<PolicieRule> findForPage(PageEntity entity) {
        return policieRuleDao.findForPage(entity);
    }

    /**
     * 创建规则
     * @param policieRule
     */
    @Override
    public void save(PolicieRule policieRule) {
        int position = policieRule.getPosition();
        String hql = "from PolicieRule where position = '"+position+"'";
        List<PolicieRule> policieRules = policieRuleDao.findByHql(hql);
        if (StrUtils.checkParam(policieRules)){
            hql = "from PolicieRule where position >= '"+position+"'";
            policieRules = policieRuleDao.findByHql(hql);
            for (PolicieRule rule : policieRules) {//将之后的规则的位置都往后移一个
                rule.setPosition(rule.getPosition() + 1);
                policieRuleDao.update(rule);
            }
        }
        policieRuleDao.add(policieRule);
    }

    /**
     * 删除规则集
     * @param id
     */
    @Override
    public void delete(String id) {
        PolicieRule policieRule = policieRuleDao.findById(PolicieRule.class, id);
        int position = policieRule.getPosition();
        String hql = "from PolicieRule where position > '"+position+"'";
        List<PolicieRule> policieRules = policieRuleDao.findByHql(hql);
        if (StrUtils.checkParam(policieRules)){//将之后的规则的位置都往前移一个
            for (PolicieRule rule : policieRules) {
                rule.setPosition(rule.getPosition() - 1);
                policieRuleDao.update(rule);
            }
        }
        policieRuleDao.delete(policieRule);
    }
    
    @Override
    public String cloudDelete (String ruleCloudId, String pyCloudId, CloudosClient client) {
        String uri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_RULE_ACTION);
        uri = HttpUtils.tranUrl(uri, ruleCloudId);
        Map<String, Object> removeRuleMap = new HashMap<>();
        removeRuleMap.put("firewall_rule_id", ruleCloudId);
        String removeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_REMOVE_RULE);
        removeUri = HttpUtils.tranUrl(removeUri, pyCloudId);
        JSONObject removeResponse = client.put(removeUri, removeRuleMap);
        if (!ResourceHandle.judgeResponse(removeResponse)){
            this.warn("REMOVE FIREWALL RULE ERROR : " + removeResponse.getString("record"));
            return removeResponse.getString("record");
        }
        JSONObject response = client.delete(uri);
        if (!ResourceHandle.judgeResponse(response)){
            this.warn("DELETE FIREWALL RULE ERROR : " + response.getString("record"));
            return response.getString("record");
        }
        return "success";
    }
    
    @Override
    public String cloudSave (PolicieRule entity, String pyCloudId, String beforeCloudId, String afterCloudId,
                             CloudosClient client) {
        Map<String, Object> prDataMap = ResourceHandle.tranToMap(entity, ConfigProperty.RESOURCE_TYPE_FIREWALL_POLICY_RULE);
        if (StrUtils.checkParam(entity.getProtocol()) && entity.getProtocol().equals("ANY")){
            prDataMap.remove("protocol");
            entity.setProtocol(null);
        }
        Map<String, Object> prParams = ResourceHandle.getParamMap(prDataMap, "firewall_rule");//转换成跟cloudos对接的参数
        String prUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_RULE);//获取uri
        JSONObject prResponse = client.post(prUri, prParams);//创建规则集规则
        if (!ResourceHandle.judgeResponse(prResponse)){//请求失败时,抛出异常
            this.warn("CREATE FIREWALL RULE ERROR : " + prResponse.getString("record"));
            return prResponse.getString("record");
        }
        String cloudRuleId = ResourceHandle.getId(prResponse, "firewall_rule");//获取规则集规则真实id
        entity.setCloudosId(cloudRuleId);
        //调用cloudos接口规则集插入规则
        Map<String, Object> insertRuleMap = new HashMap<>();
        insertRuleMap.put("firewall_rule_id", cloudRuleId);
        if (StrUtils.checkParam(afterCloudId)) {
            insertRuleMap.put("insert_after", afterCloudId);
        }
        if (StrUtils.checkParam(beforeCloudId)) {
            insertRuleMap.put("insert_before", beforeCloudId);
        }
        String insertUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_INSERT_RULE);
        insertUri = HttpUtils.tranUrl(insertUri, pyCloudId);
        JSONObject insertResponse = client.put(insertUri, insertRuleMap);
        if (!ResourceHandle.judgeResponse(insertResponse)){//请求失败时,抛出异常并删除规则
            String prDeUri = singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_RULE_ACTION);
            prDeUri = HttpUtils.tranUrl(prDeUri, cloudRuleId);
            client.delete(prDeUri);
            this.warn("INSERT FIREWALL RULE ERROR : " + insertResponse.getString("record"));
            return insertResponse.getString("record");
        }
        return "success";
    }
    
    @Override
    public String cloudSave (PolicieRule entity, String pyCloudId, CloudosClient client) {
        String beforeCdId = null;
        String afterCdId = null;
        if (StrUtils.checkParam(entity.getAfterId())){
            PolicieRule afterRule = policieRuleDao.findById(PolicieRule.class, entity.getAfterId());
            afterCdId = afterRule.getCloudosId();
        }
        if (StrUtils.checkParam(entity.getBeforeId())){
            PolicieRule beforeRule = policieRuleDao.findById(PolicieRule.class, entity.getBeforeId());
            beforeCdId = beforeRule.getCloudosId();
        }
        return cloudSave(entity, pyCloudId, beforeCdId, afterCdId, client);
    }
    
    @Override
    public JSONObject getRuleJson (String cloudosId, CloudosClient client) {
        if (!StrUtils.checkParam(cloudosId)) {
            return null;
        }
        String uri = HttpUtils.tranUrl(singleton.getCloudosApi(CloudosParams.CLOUDOS_API_POLICIE_RULE_ACTION),
                cloudosId);
        JSONObject json = HttpUtils.getJson(uri, "firewall_rule", client);
        return json;
    }
    
    @Override
    public PolicieRule getRulebyJson (JSONObject ruleJson) {
        PolicieRule rule = new PolicieRule();
        String action = ruleJson.getString("action");
        String ruDescription = ruleJson.getString("description");
        String name = ruleJson.getString("name");
        String ruleCdId = ruleJson.getString("id");
        String tenantId = ruleJson.getString("tenant_id");
        Integer position = ruleJson.getInteger("position");
        String protocol = ruleJson.getString("protocol");
        Integer ipVersion = ruleJson.getInteger("ip_version");
        String sourceIp = ruleJson.getString("source_ip_address");
        String destinationIp = ruleJson.getString("destination_ip_address");
        Boolean ruShared = ruleJson.getBoolean("shared");
        Boolean ruEnabled = ruleJson.getBoolean("enabled");
        String destinationPort = ruleJson.getString("destination_port");
        String sourcePort = ruleJson.getString("source_port");
        Integer sourceMin = null;
        Integer sourceMax = null;
        Integer destinationMin = null;
        Integer destinationMax = null;
        if (StrUtils.checkParam(sourcePort)){
            String [] ports = sourcePort.split(":");
            sourceMin = Integer.parseInt(ports[0]);
            if (ports.length == 1){
                sourceMax = sourceMin;
            }
            if (ports.length == 2){
                sourceMax = Integer.parseInt(ports[1]);
            }
        }
        if (StrUtils.checkParam(destinationPort)){
            String [] ports = destinationPort.split(":");
            if (ports.length == 1){
                destinationMin = destinationMax = Integer.parseInt(destinationPort);
            }
            if (ports.length == 2){
                destinationMin = Integer.parseInt(ports[0]);
                destinationMax = Integer.parseInt(ports[1]);
            }
        }
        rule.setShared(ruShared);
        rule.setEnabled(ruEnabled);
        rule.setSourcePortRangeMin(sourceMin);
        rule.setSourcePortRangeMax(sourceMax);
        rule.setDestinationPortRangeMin(destinationMin);
        rule.setDestinationPortRangeMax(destinationMax);
        rule.setDescription(ruDescription);
        rule.setCloudosId(ruleCdId);
        rule.setTenantId(tenantId);
        rule.setAction(action);
        rule.setDestinationIp(destinationIp);
        rule.setSourceIp(sourceIp);
        rule.createdUser(ConfigProperty.SYSTEM_FLAG);
        rule.setName(name);
        rule.setPosition(position);
        rule.setProtocol(protocol != null ? protocol.toUpperCase() : null);
        rule.setIpVersion(ipVersion);
        return rule;
    }
}
