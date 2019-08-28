package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.BillBiz;
import com.h3c.iclouds.biz.InitCodeBiz;
import com.h3c.iclouds.biz.MeasureDetailBiz;
import com.h3c.iclouds.biz.VdeviceBiz;
import com.h3c.iclouds.client.zabbix.ZabbixApi;
import com.h3c.iclouds.client.zabbix.ZabbixDefine;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.common.SqlQueryProperty;
import com.h3c.iclouds.dao.ListPriceDao;
import com.h3c.iclouds.dao.OriginalsDao;
import com.h3c.iclouds.dao.ProjectDao;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.Originals;
import com.h3c.iclouds.po.Originals2Day;
import com.h3c.iclouds.po.Project;
import com.h3c.iclouds.po.Vdevice;
import com.h3c.iclouds.po.business.FlavorParam;
import com.h3c.iclouds.po.business.Instance;
import com.h3c.iclouds.po.business.ListPrice;
import com.h3c.iclouds.po.business.MeasureDetail;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.Ssh2Utils;
import com.h3c.iclouds.utils.StrUtils;
import com.h3c.iclouds.utils.ThreadContext;
import com.h3c.iclouds.utils.UploadFileUtils;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zkf5485 on 2017/9/1.
 */
public class NetworkFlowQuartz extends BaseQuartz {

    @Resource
    private OriginalsDao originalsDao;

    @Resource
    private SqlQueryBiz sqlQueryBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<Originals2Day> originals2DayDao;

    @Resource
    private ProjectDao projectDao;

    @Resource
    private MeasureDetailBiz measureDetailBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<Instance> instanceDao;

    @Resource
    private ListPriceDao listPriceDao;

    @Resource
    private BillBiz billBiz;
    
    @Resource
    private VdeviceBiz vdeviceBiz;
    
    @Resource
    private InitCodeBiz initCodeBiz;

    @Override
    public void startQuartz() {
            this.exportExcel("2017-10-31");
        if(true) {
            return;
        }

        // 先做数据统计
        this.condense();

        //保存虚拟防火墙流量数据
        //this.vfFlow();
        
        // 保存同花顺的流量数据
        this.thsFlow();
    }

    private void condense() {
        Calendar calendar = Calendar.getInstance();
        // 计划启用时间为01:00:00
        int hour = calendar.get(Calendar.HOUR);
        if(hour != 1) {
            return;
        }
        int minute = calendar.get(Calendar.MINUTE);
        if(minute >= 5) {
            return;
        }

        int date = calendar.get(Calendar.DATE);

        // 设置查询时间范围
        calendar.add(Calendar.DATE, -1);
        String day = DateUtils.getDate(calendar.getTime(), DateUtils.dateFormat);

        // 每个月的第一天则需要做租户数据导出
        if(date == 1) {
            this.exportExcel(day);
        }

        Date startDate = DateUtils.getDateByString(day + " 00:00:00");
        Date endDate = DateUtils.getDateByString(day + " 23:59:59");

        Map<String, Object> queryMap = StrUtils.createMap("startDate", startDate);
        queryMap.put("endDate", endDate);
        List<Map<String, Object>> list = sqlQueryBiz.queryByName(SqlQueryProperty.QUERY_NETFLOW_GROUP, queryMap);
        if(StrUtils.checkCollection(list)) {
            list.forEach(map -> {
                String vassetId = StrUtils.tranString(map.get("vassetid"));
                String type = StrUtils.tranString(map.get("type"));
                long value = StrUtils.tranLong(map.get("total"));
                Originals2Day entity = Originals2Day.create(vassetId, type, endDate, value);
                originals2DayDao.add(entity);
            });
        }
    }

    /**
     * 保存同花顺性能数据
     */
    private void thsFlow() {
        // 进口监控项
        String in_itemId = singleton.getTonghuashunConfigKey("ths.in.item");
        // 出口监控项
        String out_itemId = singleton.getTonghuashunConfigKey("ths.out.item");

        // 同花顺id
        String thsTenantId = singleton.getThsTenantId();

        // 最高权限Api
        ZabbixApi api = ZabbixApi.createAdmin();
        if(api == null) {
            throw MessageException.create("获取Zabbix Api 连接异常.");
        }
        // 进口
        this.saveThsFlow(ConfigProperty.NETFLOW_IN, in_itemId, thsTenantId, api);
        // 出口
        this.saveThsFlow(ConfigProperty.NETFLOW_OUT, out_itemId, thsTenantId, api);
    }

    private void saveThsFlow(String type, String itemId, String thsTenantId, ZabbixApi zabbixApi) {
        Map<String, Object> queryMap = StrUtils.createMap("itemids", itemId);
        queryMap.put("history", "3");
        queryMap.put("sortfield", "clock");
        queryMap.put("sortorder", "ASC");
        List<Originals> list = originalsDao.findLastData(thsTenantId, type);
        if(StrUtils.checkCollection(list)) {
            queryMap.put("time_from", (list.get(0).getCollectTime().getTime() / 1000) + 1);
        }
//        queryMap.put("output", new String[]{"clock", "value"});
        JSONObject jsonObject = zabbixApi.get(ZabbixDefine.HISTORY, queryMap);
        if(ZabbixApi.requestSuccess(jsonObject)) {
            JSONArray array = jsonObject.getJSONArray("result");
            for (int i = 0; i < array.size(); i++) {
                Originals entity = Originals.create(thsTenantId, type, array.getJSONObject(i));
                this.originalsDao.add(entity);
            }
        }
    }
    
    /**
     * 保存虚拟防火墙流量信息
     */
    private void vfFlow() {
        String assetTypeId = initCodeBiz.getByTypeCode(ConfigProperty.CMDB_ASSET_TYPE_FIREWALL, ConfigProperty.CMDB_ASSET_TYPE).getId();
        List<Vdevice> vdevices = vdeviceBiz.findByPropertyName(Vdevice.class, "assetTypeId", assetTypeId);
        String path = ConfigProperty.VF_FLOW_SHELL_PATH;
        for (Vdevice vdevice : vdevices) {
            String ip = vdevice.getIp();
            String community = vdevice.getCommunity();
            if (StrUtils.checkParam(ip, community)) {
                String command = path + " -FwIp " + ip + " -FwCommunity " + community;
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    int result = process.waitFor();
                    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuffer flowList = new StringBuffer();
                    String line = "";
                    while ((line = input.readLine()) != null) {
                        flowList.append(line);
                    }
                    String flows = flowList.toString();
                    if (0 != result) {
                        LogUtils.warn(this.getClass(), "Local Execute Command Failure, command:" + command + ";" + "error:" + flows);
                        String hostIp = singleton.getConfigValue(ConfigProperty.VF_FLOW_SHELL_HOST_IP);
                        String userName = singleton.getConfigValue(ConfigProperty.VF_FLOW_SHELL_HOST_USERNAME);
                        String password = singleton.getConfigValue(ConfigProperty.VF_FLOW_SHELL_HOST_PASSWORD);
                        Ssh2Utils ssh2Utils = new Ssh2Utils(hostIp , userName, password);
                        List<String> sshResult = ssh2Utils.execCmd(command);
                        if (sshResult.size() > 0) {
                            flows = sshResult.get(0);
                        } else {
                            LogUtils.warn(this.getClass(), "SSH Execute Command Failure, hostIp:" + hostIp + ", command:" +
                                    command);
                            return;
                        }
                    }
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(flows);
                        this.saveFwFlow(jsonObject, vdevice);
                    } catch (Exception e) {
                        LogUtils.warn(this.getClass(), "Get Data Error, command:" + command + ";error:" + flows);
                    }
                }catch (Exception e) {
                    LogUtils.exception(this.getClass(), e);
                }
            }
        }
    }
    
    private void saveFwFlow(JSONObject jsonObject, Vdevice vdevice) {
        String status = jsonObject.getString("status");
        if (ResultType.success.toString().equals(status)) {
            JSONObject flow = null;
            JSONArray dataList = jsonObject.getJSONArray("data");
            if (StrUtils.checkCollection(dataList)) {
                if (dataList.size() > 1) {
                    for (int i = 0; i < dataList.size(); i++) {
                        JSONObject json = dataList.getJSONObject(i);
                        if (json.getString("ifname").equals(vdevice.getPortName())) {
                            flow = json;
                            break;
                        }
                    }
                } else {
                    flow = dataList.getJSONObject(0);
                }
                
                //入口流量
                long inFlow = flow.getLong("ifin");
                long lastInFlow = 0;//上一次的入口流量总值
                List<Originals> inLastDatas = originalsDao.findLastData(vdevice.getId(), "inFlow");
                if (StrUtils.checkCollection(inLastDatas)) {
                    lastInFlow = inLastDatas.get(0).getCurrentTotal();
                }
                Originals originals = Originals.create(vdevice, inFlow, lastInFlow, "inFlow");
                originalsDao.add(originals);
    
                //出口流量
                long outFlow = flow.getLong("ifout");
                long lastOutFlow = 0;//上一次的出口流量总值
                List<Originals> outLastDatas = originalsDao.findLastData(vdevice.getId(), "outFlow");
                if (StrUtils.checkCollection(outLastDatas)) {
                    lastOutFlow = outLastDatas.get(0).getCurrentTotal();
                }
                originals = Originals.create(vdevice, outFlow, lastOutFlow, "outFlow");
                originalsDao.add(originals);
            }
        } else {
            LogUtils.warn(this.getClass(), "Get Data Failure");
        }
    }

    /**
     * 导出excel
     * @param day
     */
    private void exportExcel(String day) {
        Date endDate = DateUtils.getDateByString(day + " 23:59:59");
        Date startDate = DateUtils.getDateByString(day + " 00:00:00");
        startDate.setDate(1);
        String month = DateUtils.getDate(startDate, "yyyy.MM");
        List<Project> projects = projectDao.getAll(Project.class);
        projects.forEach(project -> {
            if(!project.getId().equals("5c7eeb3c0a2f4cca8683981227a541f4")) {
                return;
            }
            WritableWorkbook workbook = null;
            long total = 0l;
            try {
                List<Originals> list = this.originalsDao.findOneDateData(project.getId(), startDate, endDate);
                if(!StrUtils.checkCollection(list)) {
                    this.info("租户[" + project.getName() + ":" + project.getId() + "]未查询到流量数据");
                    return;
                }
                List<String[]> datas = new ArrayList<>();
                int in_index = 0;
                int out_index = 0;
                for (Originals entity : list) {
                    String[] array = null;
                    if(ConfigProperty.NETFLOW_IN.equals(entity.getType())) {
                        if(datas.size() == in_index) {
                            array = new String[4];
                            datas.add(array);
                        } else {
                            array = datas.get(in_index);
                        }
                        array[0] = DateUtils.getDate(entity.getCollectTime());
                        array[1] = String.valueOf(entity.getValue());
                        ++in_index;
                    } else {
                        if(datas.size() == out_index) {
                            array = new String[4];
                            datas.add(array);
                        } else {
                            array = datas.get(out_index);
                        }
                        array[2] = DateUtils.getDate(entity.getCollectTime());
                        array[3] = String.valueOf(entity.getValue());
                        ++out_index;
                    }
                    total += entity.getValue();
                }


                String fileName = month + "." + project.getId() + ".xls";
                File file = new File(UploadFileUtils.getNetFlowPath(), fileName);
                this.info("File path: " + file.getAbsolutePath());
                file.createNewFile();
                workbook = Workbook.createWorkbook(file);
                WritableSheet sheet = workbook.createSheet("流量数据", 0); // 创建工作簿
                int i = 0;
                this.writeRow(sheet, new String[]{"进口流量采集时间", "进口流量", "出口流量采集时间", "出口流量"}, i++);
                for (String[] data : datas) {
                    this.writeRow(sheet, data, i++);
                }
            } catch (IOException e) {
                e.printStackTrace();
                this.exception(e, "保存租户流量数据异常");
            } finally {
                if(workbook != null) {
                    try {
                        workbook.write();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        workbook.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }

                // 将total转为GB,原单位为Bps
                if(total != 0) {
                    double value = (double) total / 1024 / 1024 / 1024;
                    total = (long) Math.ceil(value);    // 向上取整
                }
                // 处理流量计费
                this.saveBill(total, project.getId());
            }
        });
    }

    /**
     * 保存账单数据\
     * @param flow
     * @param tenantId
     */
    private void saveBill(long flow, String tenantId) {
        MeasureDetail detail = null;
        Instance entity = instanceDao.singleByClass(Instance.class, StrUtils.createMap("instance", tenantId));
        if(entity == null) {
//            this.warn("租户[" + tenantId + "]未购买公网流量服务");
//            return;
            String measureId = this.createMeasure(tenantId);
            detail = measureDetailBiz.findById(MeasureDetail.class, measureId);
        } else {
            detail = measureDetailBiz.singleByClass(MeasureDetail.class, StrUtils.createMap("instanceId", entity.getId()));
        }
        if(detail != null) {
            billBiz.create(detail, ConfigProperty.SYSTEM_FLAG, ConfigProperty.BILL_TYPE_AUTO, flow);
        }
    }

    private String createMeasure(String tenantId) {
        ListPrice entity = this.listPriceDao.findLastModifyPrice(ConfigProperty.NETWORK_FLOW_CLASSID);
        if(entity == null) {
            this.warn("公网流量未设定服务定价数据");
            return null;
        }
        FlavorParam flavorParam = new FlavorParam();
        flavorParam.setClassId(ConfigProperty.NETWORK_FLOW_CLASSID);
        flavorParam.setSpecId(entity.getId());
        flavorParam.setUserId(ConfigProperty.SYSTEM_FLAG);
        flavorParam.setTenantId(tenantId);
        flavorParam.setResourceId(tenantId);
        flavorParam.setCreatedBy(ConfigProperty.SYSTEM_FLAG);
        measureDetailBiz.save(flavorParam, "创建");
        // 获取新创建存入的对象id
        String id = (String) ThreadContext.get("measureId");
        return id;
    }

    /**
     * 写入行数据
     * @param sheet
     * @param cols
     * @param rowNum
     */
    private void writeRow(WritableSheet sheet, String[] cols, int rowNum) {
        for (int i = 0; i < cols.length; i++) { // 写入每一列
            String value = StrUtils.tranString(cols[i]);
            Label label = new Label(i, rowNum, value);
            try {
                sheet.addCell(label);
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
    }
    
}
