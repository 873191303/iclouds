package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.base.SqlQueryBiz;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.biz.PfmValueBiz;
import com.h3c.iclouds.biz.Pools2HostBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.biz.StorageClustersBiz;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.NovaVmViewDao;
import com.h3c.iclouds.dao.Storage2OveDao;
import com.h3c.iclouds.po.Cvm2Ove;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.po.NovaVmView;
import com.h3c.iclouds.po.PfmValue;
import com.h3c.iclouds.po.Pools2Host;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.po.Storage2Ove;
import com.h3c.iclouds.po.StorageClusters;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.DateUtils;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * cvk和存储容量管理相关接口
 * Created by yKF7317 on 2017/3/13.
 */
@SuppressWarnings("rawtypes")
@Api(value = "容量接口")
@RestController
@RequestMapping("/capacity")
public class CapaRest extends BaseRest {

    @Resource
    private Pools2HostBiz pools2HostBiz;

    @Resource
    private Server2OveBiz server2OveBiz;

    @Resource
    private Storage2OveDao storage2OveDao;

    @Resource(name = "baseDAO")
    private BaseDAO<Cvm2Ove> cvm2OveDao;

    @Resource
    private InterfacesBiz interfacesBiz;
    
    @Resource
    private NovaVmViewDao novaVmViewDao;
    
    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Resource
    private StorageClustersBiz storageClustersBiz;
    
    @Resource
    private SqlQueryBiz queryBiz;
    
    @Resource
    private PfmValueBiz pfmValueBiz;
    
    @ApiOperation(value = "获取cas列表")
    @RequestMapping(value = "/cas", method = RequestMethod.GET)
    public Object casList(){
        List<Interfaces> casList = interfacesBiz.findByPropertyName(Interfaces.class, "type", "cas");
        return BaseRestControl.tranReturnValue(casList);
    }

    @ApiOperation(value = "获取cvm列表")
    @RequestMapping(value = "/cvm", method = RequestMethod.GET)
    public Object cvmList(){
        List<Cvm2Ove> cvmList = cvm2OveDao.getAll(Cvm2Ove.class);
        return BaseRestControl.tranReturnValue(cvmList);
    }

    @ApiOperation(value = "获取所有cas下的主机池容量总和比")
    @RequestMapping(value = "/cas/total/pool", method = RequestMethod.GET)
    public Object casTotalCapacity(){
        List<Pools2Host> poolList = pools2HostBiz.getAll(Pools2Host.class);
        return BaseRestControl.tranReturnValue(poolList);
    }

    @ApiOperation(value = "获取单个cas下的主机池容量")
    @RequestMapping(value = "/cas/{casId}/pool", method = RequestMethod.GET)
    public Object casCapacity(@PathVariable String casId){
        List<Pools2Host> poolList = pools2HostBiz.findByPropertyName(Pools2Host.class, "belongCas", casId);
        return BaseRestControl.tranReturnValue(poolList);
    }

    @ApiOperation(value = "获取cvm下的cvk容量")
    @RequestMapping(value = "/cvm/{cvmId}/cvk", method = RequestMethod.GET)
    public Object cvkCapacity(@PathVariable String cvmId){
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(cvmId);
        PageModel<Server2Ove> pageModel = server2OveBiz.findForPage(entity);
        PageList<Server2Ove> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取所有存储容量之和比")
    @RequestMapping(value = "/storage/total", method = RequestMethod.GET)
    public Object storageTotalCapacity(){
        List<Storage2Ove> storage2OveList = storage2OveDao.getAll(Storage2Ove.class);
        return BaseRestControl.tranReturnValue(storage2OveList);
    }

    @ApiOperation(value = "获取单个存储容量")
    @RequestMapping(value = "/storage/{storageId}", method = RequestMethod.GET)
    public Object storageCapacity(@PathVariable String storageId){
        StorageClusters storageClusters = storageClustersBiz.findById(StorageClusters.class, storageId);
        if (!StrUtils.checkParam(storageClusters)) {
            return BaseRestControl.tranReturnValue(ResultType.deleted);
        }
        return BaseRestControl.tranReturnValue(storageClusters);
    }

    @ApiOperation(value = "获取单个存储下卷的容量")
    @RequestMapping(value = "/storage/{storageId}/volume", method = RequestMethod.GET)
    public Object storageVolumeCapacity(@PathVariable String storageId){
        List<StorageVolums> storageVolumss = storageVolumsBiz.findByPropertyName(StorageVolums.class, "sid",
                storageId);
        return BaseRestControl.tranReturnValue(storageVolumss);
    }

    @ApiOperation(value = "获取单个卷的容量")
    @RequestMapping(value = "/volume/{volumeId}", method = RequestMethod.GET)
    public Object volumeCapacity(@PathVariable String volumeId){
        StorageVolums storageVolums = storageVolumsBiz.findById(StorageVolums.class, volumeId);
        return BaseRestControl.tranReturnValue(storageVolums);
    }
    
    @ApiOperation(value = "资源分配容量的top5列表")
    @RequestMapping(value = "/{param}/top", method = RequestMethod.GET)
    public Object topList(@PathVariable String param){
        switch (param) {
            case "cpu":
                return BaseRestControl.tranReturnValue(server2OveBiz.cpuTopList());
            case "memory":
                return BaseRestControl.tranReturnValue(server2OveBiz.memoryTopList());
            case "store":
                return BaseRestControl.tranReturnValue(storage2OveDao.storageTopList());
            default:
                return BaseRestControl.tranReturnValue(ResultType.parameter_error);
        }
    }
    
    @ApiOperation(value = "获取")
    @RequestMapping(value = "/{param}/count", method = RequestMethod.GET)
    public Object countList(@PathVariable String param){
        return getCountMap(param);
    }
    
    @Override
    public Object list() {
        return null;
    }

    @Override
    public Object get(@PathVariable String id) {
        return null;
    }

    @Override
    public Object delete(@PathVariable String id) {
        return null;
    }

    @Override
    public Object save(@RequestBody Serializable entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody Serializable entity) throws IOException {
        return null;
    }
    
    private Object getCountMap(String param){
        String sql = null;
        if ("cpu".equals(param)) {
            sql = "SELECT sum(cpus) AS total, sum(cpuoversize) + sum(cpus) AS allocation FROM cmdb_cap_server2ovelflow";
        } else if ("memory".equals(param)) {
            sql = "SELECT sum(ram) AS total, sum(ramoversize) + sum(ram) AS allocation FROM cmdb_cap_server2ovelflow";
        } else if ("store".equals(param)) {
            sql = "SELECT sum(totalsize) AS total, sum(allocation) AS allocation FROM cmdb_cap_storage2ovelflow";
        } else {
            return BaseRestControl.tranReturnValue(ResultType.parameter_error);
        }
        List<Map<String, Object>> countList = queryBiz.queryBySql(sql);
        if (StrUtils.checkCollection(countList)) {
            return BaseRestControl.tranReturnValue(countList.get(0));
        } else {
            return BaseRestControl.tranReturnValue(new HashMap<>());
        }
    }
    
    @ApiOperation(value="当前用户拥有的虚拟机的使用情况")
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public Object listByUser(@PathVariable String id) {
    	List<Map<String, Object>> resultList = new ArrayList<>();
    	PageEntity entity = this.beforeList();
		PageModel<NovaVmView> pageModel = novaVmViewDao.findForPage(entity);
		List<NovaVmView> list = pageModel.getDatas();
		if(list != null && !list.isEmpty()){
			resultList.addAll(this.getRate(list));
		}
		return BaseRestControl.tranReturnValue(resultList);
	}
    
    public List<Map<String, Object>> getRate(List<NovaVmView> novaVms) {
    	Map<String, String> queryMap = new HashMap<>();
    	List<Map<String, Object>> resultList = new ArrayList<>();
		List<PfmValue> pfmValues = null;
		for(NovaVmView nv : novaVms){
			Map<String, Object> map = StrUtils.createMap("novaVm", nv);
			queryMap.put("uuid", nv.getUuid());
			queryMap.put("itemId", "1"); // "1" 表示cpu使用率
			pfmValues = pfmValueBiz.findByMap(PfmValue.class, queryMap);
			if(pfmValues!=null && !pfmValues.isEmpty()){
				Float cpuRate = pfmValues.get(0).getKeyValue();
				map.put("cpuRate", cpuRate);
				String updatedDate = DateUtils.getDate(pfmValues.get(0).getUpdatedDate(), DateUtils.format);
				map.put("updatedDate", updatedDate);
			}
			queryMap.put("itemId", "2");  // "2" 表示内存使用率
			pfmValues = pfmValueBiz.findByMap(PfmValue.class, queryMap);
			if(pfmValues!=null && !pfmValues.isEmpty()){
				Float memRate = pfmValues.get(0).getKeyValue();
				map.put("memRate", memRate);
				String updatedDate = DateUtils.getDate(pfmValues.get(0).getUpdatedDate(), DateUtils.format);
				map.put("updatedDate", updatedDate);
			}
			resultList.add(map);
		}
		return resultList;
	}
}
