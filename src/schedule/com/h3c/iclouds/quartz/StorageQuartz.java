package com.h3c.iclouds.quartz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.AsmMasterBiz;
import com.h3c.iclouds.biz.InterfacesBiz;
import com.h3c.iclouds.biz.StorageClustersBiz;
import com.h3c.iclouds.biz.StorageGroups2ItemsBiz;
import com.h3c.iclouds.biz.StorageGroupsBiz;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.client.Hp3parClient;
import com.h3c.iclouds.client.HpLeftHandClient;
import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.AsmMaster;
import com.h3c.iclouds.po.Interfaces;
import com.h3c.iclouds.po.Storage2Ove;
import com.h3c.iclouds.po.StorageClusters;
import com.h3c.iclouds.po.StorageGroups;
import com.h3c.iclouds.po.StorageGroups2Items;
import com.h3c.iclouds.po.StorageVolums;
import com.h3c.iclouds.utils.LogUtils;
import com.h3c.iclouds.utils.ResourceHandle;
import com.h3c.iclouds.utils.StrUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by yKF7317 on 2017/2/28.
 */
public class StorageQuartz {

    @Resource(name = "baseDAO")
    private BaseDAO<Storage2Ove> storage2OveDao;

    @Resource
    private InterfacesBiz interfacesBiz;

    @Resource
    private AsmMasterBiz asmMasterBiz;

    @Resource
    private StorageVolumsBiz storageVolumsBiz;

    @Resource
    private StorageClustersBiz storageClustersBiz;

    @Resource
    private StorageGroups2ItemsBiz storageGroups2ItemsBiz;
    
    @Resource
    private StorageGroupsBiz storageGroupsBiz;
    
    /**
     * 同步存储容量
     */
    public void synchronization(){
        sync3par();//3par独立存储
        syncLeftHand();//leftHand集群存储
    }

    /**
     * 同步3par独立存储容量
     */
    public void sync3par(){
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("type", "3par");
            List<Interfaces> interfacesList = interfacesBiz.findByMap(Interfaces.class, queryMap);
            if (StrUtils.checkParam(interfacesList)) {
                for (Interfaces interfaces : interfacesList) {
                    String ip = interfaces.getIp();
                    String port = interfaces.getPort();
                    String userName = interfaces.getAdmin();
                    String password = interfaces.getPasswd();
                    String hp3parBelongId = interfaces.getId();
                    Hp3parClient hp3parClient = new Hp3parClient(ip, Integer.parseInt(port), userName, password);
                    String url = "/api/v1/system";
                    JSONObject systemObject = hp3parClient.get(url);
                    if (ResourceHandle.judgeResponse(systemObject)){
                        List<String> clusterKeepIds = new ArrayList<>();
                        JSONObject systemRecord = systemObject.getJSONObject("record");
                        if (StrUtils.checkParam(systemRecord)){
                            syncSystem(systemRecord, hp3parClient, hp3parBelongId, clusterKeepIds);
                        }
                        List<StorageClusters> clusterList = storageClustersBiz.findByPropertyName(StorageClusters.class, "belongId", hp3parBelongId);
                        if (StrUtils.checkCollection(clusterList)){
                            for (StorageClusters storageCluster : clusterList) {
                                if (!clusterKeepIds.contains(storageCluster.getId())){
                                    storageClustersBiz.deleteCluster(storageCluster);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
    }

    public void syncSystem(JSONObject systemRecord, Hp3parClient hp3parClient, String hp3parBelongId, List<String> clusterKeepIds){
        Date date = new Date();
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) + "";
        String month = now.get(Calendar.MONTH) + 1 + "";
        String day = now.get(Calendar.DAY_OF_MONTH) + "";
        String id = systemRecord.getString("id");
        String name = systemRecord.getString("name");
        String ipAddress = systemRecord.getString("IPv4Addr");
        String serialNum = systemRecord.getString("serialNumber");
        Float totalCapa = systemRecord.getFloat("totalCapacityMiB");//总容量
        Float allocatedCapa = systemRecord.getFloat("allocatedCapacityMiB");//已分配容量
        Float freeCapa = systemRecord.getFloat("freeCapacityMiB");//剩余容量
        try {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("hpId", id);
            queryMap.put("belongId", hp3parBelongId);
            Storage2Ove storage2Ove = storage2OveDao.singleByClass(Storage2Ove.class, queryMap);
            if (!StrUtils.checkParam(storage2Ove)){//没有数据则新建；有数据则更新
                storage2Ove = new Storage2Ove();
                storage2Ove.setHpId(id);
                storage2Ove.setName(name);
                storage2Ove.setAllocationCapa(allocatedCapa);
                storage2Ove.setFreeCapa(freeCapa);
                storage2Ove.setTotalCapa(totalCapa);
                storage2Ove.setDate(date);
                storage2Ove.setYear(year);
                storage2Ove.setMonth(month);
                storage2Ove.setDay(day);
                storage2Ove.setId(UUID.randomUUID().toString());
                storage2Ove.setCapaOverflow((float)0);
                storage2Ove.setBelongId(hp3parBelongId);
                storage2OveDao.add(storage2Ove);
            }else {
                storage2Ove.setAllocationCapa(allocatedCapa);
                storage2Ove.setFreeCapa(freeCapa);
                storage2Ove.setTotalCapa(totalCapa);
                storage2OveDao.update(storage2Ove);
            }
            StorageClusters storageClusters = storageClustersBiz.singleByClass(StorageClusters.class, queryMap);
            if (!StrUtils.checkParam(storageClusters)){
                storageClusters = new StorageClusters();
                storageClusters.setId(UUID.randomUUID().toString());
                storageClusters.setName(name);
                storageClusters.setIp(ipAddress);
                storageClusters.setType("0");
                storageClusters.setHpId(id);
                storageClusters.setBelongId(hp3parBelongId);
                storageClusters.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                storageClustersBiz.add(storageClusters);
            }
            clusterKeepIds.add(storageClusters.getId());
            queryMap.clear();
            queryMap.put("serial", serialNum);
            queryMap.put("assetId", id);
            AsmMaster asmMaster = asmMasterBiz.singleByClass(AsmMaster.class, queryMap);
            float totalDisk = totalCapa;
            if (!StrUtils.checkParam(asmMaster)){
                asmMaster = new AsmMaster();
                asmMaster.setAssetId(id);
                asmMaster.setAssetName(name);
                asmMaster.setSerial(serialNum);
                asmMaster.setIloIP(ipAddress);
                asmMaster.setAssetType("8a8a700d57a30ea80157a30eccf80003");
                asmMaster.setAssetTypeCode("stock");
                asmMaster.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                asmMaster.setBegDate(new Date());
                asmMaster.setStatus("2");
                asmMaster.setDepart("8a92408456ba52ab0156ba5339330000");
                asmMaster.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");
                asmMaster.setProvide("admin");
                asmMaster.setDiskTotal((int)totalDisk);
                asmMasterBiz.add(asmMaster);
            } else {
                asmMaster.setDiskTotal((int)totalDisk);
                asmMaster.setSerial(serialNum);
                asmMaster.setIloIP(ipAddress);
                asmMaster.setAssetName(name);
                asmMaster.setDepart("8a92408456ba52ab0156ba5339330000");
                asmMaster.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");
                asmMaster.setProvide("admin");
                asmMasterBiz.update(asmMaster);
            }
            queryMap.clear();
            queryMap.put("masterId", asmMaster.getId());
            queryMap.put("cid", storageClusters.getId());
            StorageGroups2Items groups2Items = storageGroups2ItemsBiz.singleByClass(StorageGroups2Items.class, queryMap);
            if (!StrUtils.checkParam(groups2Items)){
                groups2Items = new StorageGroups2Items();
                groups2Items.setId(UUID.randomUUID().toString());
                groups2Items.setIp(ipAddress);
                groups2Items.setMasterId(asmMaster.getId());
                groups2Items.setCid(storageClusters.getId());
                groups2Items.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                storageGroups2ItemsBiz.add(groups2Items);
            }
            Map<String, Float> map = new HashMap<>();
            map.put("overFlowCapa", (float)0);
            map.put("usedCapa", (float)0);
            String url = "/api/v1/volumes";
            JSONObject volumeObject = hp3parClient.get(url);
            if (ResourceHandle.judgeResponse(volumeObject)){
                JSONObject volumeRecord = volumeObject.getJSONObject("record");
                JSONArray volumeArray = volumeRecord.getJSONArray("members");
                List<String> volumeKeepIds = new ArrayList<>();
                if (StrUtils.checkParam(volumeArray)){
                    for (int i = 0; i < volumeArray.size(); i++) {
                        JSONObject volumeJson = volumeArray.getJSONObject(i);
                        map = sync3parVolume(volumeJson, map, storageClusters.getId(), ipAddress, hp3parBelongId, volumeKeepIds);
                    }
                }
                //更新统计后的超配使用率等信息
                Float overFlowCapa = map.get("overFlowCapa");
                Float usedCapa = map.get("usedCapa");
                if (overFlowCapa > allocatedCapa){
                    storage2Ove.setCapaOverflow((overFlowCapa - allocatedCapa)/allocatedCapa);
                }
                storage2Ove.setUsedCapa(usedCapa);
                storage2Ove.setCapaUsage(usedCapa/totalCapa);
                storage2OveDao.update(storage2Ove);
                List<StorageVolums> volumeList = storageVolumsBiz.findByPropertyName(StorageVolums.class, "sid",
                        storageClusters.getId());
                if (StrUtils.checkCollection(volumeList)){
                    for (StorageVolums storageVolums : volumeList) {
                        if (!volumeKeepIds.contains(storageVolums.getId())){
                            storageVolumsBiz.delete(storageVolums);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
    }

    public Map<String, Float> sync3parVolume(JSONObject volumeJson, Map<String, Float> map, String clusterId, String ip,
                                             String belongId, List<String> keepIds){
        String uuid = volumeJson.getString("uuid");
        String id = volumeJson.getString("id");
        String name = volumeJson.getString("name");
        String wwn = volumeJson.getString("wwn");
        Float overFlowCapa = map.get("overFlowCapa");
        Float usedCapa = map.get("usedCapa");
        Float capa = volumeJson.getFloat("sizeMiB");
        overFlowCapa = overFlowCapa + capa;
        JSONObject adminSpace = volumeJson.getJSONObject("adminSpace");
        Float adminUsed = adminSpace.getFloat("usedMiB");
        JSONObject userSpace = volumeJson.getJSONObject("userSpace");
        Float userUsed = userSpace.getFloat("usedMiB");
        JSONObject snapshotSpace = volumeJson.getJSONObject("snapshotSpace");
        Float snapUsed = snapshotSpace.getFloat("usedMiB");
        Float used = adminUsed + userUsed + snapUsed;
        usedCapa = usedCapa + used;
        map.put("usedCapa", usedCapa);
        map.put("overFlowCapa", overFlowCapa);
        StorageVolums volume = storageVolumsBiz.findById(StorageVolums.class, uuid);
        if (!StrUtils.checkParam(volume)){
            volume = new StorageVolums();
            volume.setId(uuid);
            volume.setSid(clusterId);
            volume.setIp(ip);
            volume.setSize(capa);
            volume.setStoryType("0");
            volume.setUsedSize(used);
            volume.setUnit("MiB");
            volume.setWwn(wwn);
            volume.setVolumeName(name);
            volume.setBelongId(belongId);
            volume.setHpId(id);
            volume.createdUser("989116e3-79a2-426b-bfbe-668165104885");
            storageVolumsBiz.add(volume);
        } else {
            volume.setSid(clusterId);
            volume.setIp(ip);
            volume.setSize(capa);
            volume.setUsedSize(used);
            volume.setWwn(wwn);
            volume.setVolumeName(name);
            storageVolumsBiz.update(volume);
        }
        keepIds.add(volume.getId());
        return map;
    }

    /**
     * 同步leftHand集群存储容量
     */
    public void syncLeftHand(){
        try {
            Map<String, String> queryFaceMap = new HashMap<>();
            queryFaceMap.put("type", "leftHand");
            List<Interfaces> interfacesList = interfacesBiz.findByMap(Interfaces.class, queryFaceMap);
            if (!StrUtils.checkCollection(interfacesList)) {
                return;
            }
            for (Interfaces interfaces : interfacesList) {
                String ip = interfaces.getIp();
                String port = interfaces.getPort();
                String userName = interfaces.getAdmin();
                String password = interfaces.getPasswd();
                String hpLeftHandBelongId = interfaces.getId();
                HpLeftHandClient hpLeftHandClient = new HpLeftHandClient(ip, Integer.parseInt(port), userName, password);
                Map<String, Map<String, Float>> storageClusterMap = new HashMap<>();
                String clusterUri = "/lhos/clusters";
                JSONObject clusterObject = hpLeftHandClient.get(clusterUri);
                if (!ResourceHandle.judgeResponse(clusterObject)) {
                    LogUtils.warn(this.getClass(), JSONObject.toJSON(clusterObject));
                    continue;
                }
                clusterObject = clusterObject.getJSONObject("record");
                if (!StrUtils.checkParam(clusterObject)){
                    continue;
                }
                JSONArray clusterArray = clusterObject.getJSONArray("members");
                if (!StrUtils.checkCollection(clusterArray)){
                    continue;
                }
                List<String> volumeKeepIds = new ArrayList<>();
                List<String> clusterKeepIds = new ArrayList<>();
                //同步集群
                for (int i = 0; i < clusterArray.size(); i++) {
                    JSONObject clusterJson = clusterArray.getJSONObject(i);
                    storageClusterMap = syncCluster(clusterJson, storageClusterMap, hpLeftHandBelongId, ip,
                            clusterKeepIds, hpLeftHandClient);
                }
                //同步存储卷
                String volumeUri = "/lhos/volumes";
                JSONObject volumeObject = hpLeftHandClient.get(volumeUri);
                if (ResourceHandle.judgeResponse(volumeObject)){
                    JSONObject volumeRecord = volumeObject.getJSONObject("record");
                    if (StrUtils.checkParam(volumeRecord)){
                        JSONArray volumeArray = volumeRecord.getJSONArray("members");
                        if (StrUtils.checkCollection(volumeArray)){
                            for (int i = 0; i < volumeArray.size(); i++) {
                                JSONObject volumeJson = volumeArray.getJSONObject(i);
                                storageClusterMap = syncleftHandVolume(volumeJson, storageClusterMap, hpLeftHandBelongId, ip, volumeKeepIds);
                            }
                        }
                    }
                }
                //统计容量信息
                for (int i = 0; i < clusterArray.size(); i++) {
                    JSONObject clusterJson = clusterArray.getJSONObject(i);
                    String id = clusterJson.getString("id");
                    Map<String, Object> queryMap = new HashMap<>();
                    queryMap.put("hpId", id);
                    queryMap.put("belongId", hpLeftHandBelongId);
                    Storage2Ove storage2Ove = storage2OveDao.singleByClass(Storage2Ove.class, queryMap);
                    Map<String, Float> map = storageClusterMap.get(id);
                    Float usages = map.get("usages");
                    storage2Ove.setUsedCapa(usages);
                    storage2Ove.setCapaUsage(usages/storage2Ove.getTotalCapa());
                    storage2OveDao.update(storage2Ove);
                }
                //删除多余存储卷
                List<StorageVolums> volumeList = storageVolumsBiz.findByPropertyName(StorageVolums.class, "belongId",
                        hpLeftHandBelongId);
                if (StrUtils.checkCollection(volumeList)){
                    for (StorageVolums storageVolums : volumeList) {
                        if (!volumeKeepIds.contains(storageVolums.getId())){
                            storageVolumsBiz.deleteVolumn(storageVolums);
                        }
                    }
                }
                //删除多余存储集群
                List<StorageClusters> clusterList = storageClustersBiz.findByPropertyName(StorageClusters.class, "belongId",
                        hpLeftHandBelongId);
                if (StrUtils.checkCollection(clusterList)){
                    for (StorageClusters storageCluster : clusterList) {
                        if (!clusterKeepIds.contains(storageCluster.getId())){
                            storageClustersBiz.deleteCluster(storageCluster);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
    }

    public Map<String, Map<String, Float>> syncCluster(JSONObject clusterJson, Map<String, Map<String, Float>>
            storageClusterMap, String hpLeftHandBelongId, String ip, List<String> clusterKeepIds, HpLeftHandClient hpLeftHandClient){
        Date date = new Date();
        Calendar now = Calendar.getInstance();
        String year = now.get(Calendar.YEAR) + "";
        String month = now.get(Calendar.MONTH) + 1 + "";
        String day = now.get(Calendar.DAY_OF_MONTH) + "";
        String id = clusterJson.getString("id");
        Map<String, Float> map = new HashMap<>();
        map.put("usages", (float)0);
        storageClusterMap.put(id, map);
        String name = clusterJson.getString("name");
        Float spaceAvailable = clusterJson.getFloat("spaceAvailable")/1024;
        Float spaceTotal = clusterJson.getFloat("spaceTotal")/1024;
        Float creationSpace = (float)0;
        JSONArray creationSpaceArray = clusterJson.getJSONArray("volumeCreationSpace");
        if (StrUtils.checkParam(creationSpaceArray)){
            for (int i = 0; i < creationSpaceArray.size(); i++) {
                JSONObject creationJson = creationSpaceArray.getJSONObject(i);
                Float availableSpace = creationJson.getFloat("availableSpace")/1024;
                creationSpace = creationSpace + availableSpace;
            }
        }
        try {
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("hpId", id);
            queryMap.put("belongId", hpLeftHandBelongId);
            Storage2Ove storage2Ove = storage2OveDao.singleByClass(Storage2Ove.class, queryMap);
            if (!StrUtils.checkParam(storage2Ove)){//没有则新建
                storage2Ove = new Storage2Ove();
                storage2Ove.setId(UUID.randomUUID().toString());
                storage2Ove.setDate(date);
                storage2Ove.setYear(year);
                storage2Ove.setMonth(month);
                storage2Ove.setDay(day);
                storage2Ove.setHpId(id);
                storage2Ove.setName(name);
                storage2Ove.setTotalCapa(spaceTotal);
                storage2Ove.setAllocationCapa(spaceTotal - spaceAvailable);
                storage2Ove.setFreeCapa(spaceAvailable);
                if (creationSpace > storage2Ove.getAllocationCapa()){
                    storage2Ove.setCapaOverflow((creationSpace - storage2Ove.getAllocationCapa())/storage2Ove.getAllocationCapa());
                }
                storage2Ove.setBelongId(hpLeftHandBelongId);
                storage2OveDao.add(storage2Ove);
            }else {//数据已存在则更新
                storage2Ove.setTotalCapa(spaceTotal);
                storage2Ove.setAllocationCapa(spaceTotal - spaceAvailable);
                storage2Ove.setFreeCapa(spaceAvailable);
                if (creationSpace > storage2Ove.getAllocationCapa()){
                    storage2Ove.setCapaOverflow((creationSpace - storage2Ove.getAllocationCapa())/storage2Ove.getAllocationCapa());
                }
                storage2OveDao.update(storage2Ove);
            }
            StorageClusters storageClusters = storageClustersBiz.singleByClass(StorageClusters.class, queryMap);
            if (!StrUtils.checkParam(storageClusters)){
                StorageGroups storageGroups = new StorageGroups();
                storageGroups.setName(name.toUpperCase());
                storageGroups.setId(UUID.randomUUID().toString());
                storageGroups.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                storageGroups.setManager("admin");
                storageGroupsBiz.syncGroup(storageGroups);
                storageClusters = new StorageClusters();
                storageClusters.setId(UUID.randomUUID().toString());
                storageClusters.setName(name);
                storageClusters.setIp(ip);
                storageClusters.setType("1");
                storageClusters.setHpId(id);
                storageClusters.setBelongId(hpLeftHandBelongId);
                storageClusters.setGid(storageGroups.getId());
                storageClusters.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                storageClustersBiz.syncCluster(storageClusters, ConfigProperty.STORAGE_CLUSTER);
            }
            if (!StrUtils.checkParam(storageClusters.getGid())) {
                StorageGroups storageGroups = new StorageGroups();
                storageGroups.setName(name.toUpperCase());
                storageGroups.setId(UUID.randomUUID().toString());
                storageGroups.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                storageGroups.setManager("admin");
                storageGroupsBiz.syncGroup(storageGroups);
                storageClusters.setGid(storageGroups.getId());
                storageClustersBiz.update(storageClusters);
            }
            clusterKeepIds.add(storageClusters.getId());
            List<String> serverKeepIds = new ArrayList<>();
            //同步子存储
            String serverUri = "/lhos/servers";
            JSONObject serverObject = hpLeftHandClient.get(serverUri);
            if (ResourceHandle.judgeResponse(serverObject)){
                serverObject = serverObject.getJSONObject("record");
                if (StrUtils.checkParam(serverObject)){
                    JSONArray serverArray = serverObject.getJSONArray("members");
                    if (StrUtils.checkCollection(serverArray)){
                        for (int i = 0; i < serverArray.size(); i++) {
                            JSONObject serverJson = serverArray.getJSONObject(i);
                            syncServer(serverJson, storageClusters.getId(), serverKeepIds);
                        }
                    }
                }
            }
            //删除多余子存储
            List<StorageGroups2Items> groups2ItemsList = storageGroups2ItemsBiz.findByPropertyName
                    (StorageGroups2Items.class, "cid", storageClusters.getId());
            if (StrUtils.checkCollection(groups2ItemsList)) {
                for (StorageGroups2Items groups2Items : groups2ItemsList) {
                    if (!serverKeepIds.contains(groups2Items.getId())) {
                        storageGroups2ItemsBiz.deleteItem(groups2Items);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
        return storageClusterMap;
    }
    
    public void syncServer(JSONObject serverJson, String clusterId, List<String> serverKeepIds) {
        try {
            String hpId = serverJson.getString("id");
            String name = serverJson.getString("name");
            String iqn = serverJson.getString("iscsiIQN");
            AsmMaster asmMaster = asmMasterBiz.singleByClass(AsmMaster.class, StrUtils.createMap("assetId", hpId));
            if (!StrUtils.checkParam(asmMaster)) {
                asmMaster = new AsmMaster();
                asmMaster.setAssetId(hpId);
                asmMaster.setAssetName(name);
                asmMaster.setAssetType("8a8a700d57a30ea80157a30eccf80003");
                asmMaster.setAssetTypeCode("stock");
                asmMaster.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                asmMaster.setBegDate(new Date());
                asmMaster.setStatus("2");
                asmMaster.setDepart("8a92408456ba52ab0156ba5339330000");
                asmMaster.setAssetUser("989116e3-79a2-426b-bfbe-668165104885");
                asmMaster.setProvide("admin");
                asmMaster.setSerial(iqn);
                asmMasterBiz.add(asmMaster);
            }
            StorageGroups2Items groups2Items = storageGroups2ItemsBiz.singleByClass(StorageGroups2Items.class, StrUtils
                    .createMap("masterId", asmMaster.getId()));
            if (!StrUtils.checkParam(groups2Items)) {
                groups2Items = new StorageGroups2Items();
                groups2Items.setId(UUID.randomUUID().toString());
                groups2Items.setCid(clusterId);
                groups2Items.setMasterId(asmMaster.getId());
                groups2Items.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                groups2Items.setName(name);
                storageGroups2ItemsBiz.syncItem(groups2Items);
            }
            serverKeepIds.add(groups2Items.getId());
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
    }
    
    public Map<String, Map<String, Float>> syncleftHandVolume(JSONObject volumeJson, Map<String, Map<String, Float>> storageClusterMap, String hpLeftHandBelongId, String ip, List<String> volumeKeepIds){
        String id = volumeJson.getString("id");
        String name = volumeJson.getString("name");
        String iqn = volumeJson.getString("iscsiIqn");
        String serialNum = volumeJson.getString("serialNumber");
        Float writtenBytes = volumeJson.getFloat("bytesWritten")/1024;
//      Float provisionedSpace = volumeJson.getFloat("provisionedSpace")/1024;
        Float sizeSpace = volumeJson.getFloat("size")/1024;
        String clusterId = volumeJson.getString("clusterId");
        Map<String, Float> map = storageClusterMap.get(clusterId);
        Float usages = map.get("usages");
        usages = usages + writtenBytes;
        map.put("usages", usages);
        storageClusterMap.put(clusterId, map);
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("hpId", clusterId);
        queryMap.put("belongId", hpLeftHandBelongId);
        StorageClusters clusters = storageClustersBiz.singleByClass(StorageClusters.class, queryMap);
        queryMap.put("hpId", id);
        try {
            StorageVolums volume = storageVolumsBiz.singleByClass(StorageVolums.class, queryMap);
            if (!StrUtils.checkParam(volume)){
                volume = new StorageVolums();
                volume.setId(serialNum);
                volume.setSid(clusters.getId());
                volume.setIp(ip);
                volume.setSize(sizeSpace);
                volume.setIqn(iqn);
                volume.setStoryType("1");
                volume.setUsedSize(writtenBytes);
                volume.setUnit("MiB");
                volume.setVolumeName(name);
                volume.setBelongId(hpLeftHandBelongId);
                volume.setHpId(id);
                volume.createdUser("989116e3-79a2-426b-bfbe-668165104885");
                storageVolumsBiz.add(volume);
            }
            volumeKeepIds.add(volume.getId());
        } catch (Exception e) {
            LogUtils.exception(this.getClass(), e);
        }
        return storageClusterMap;
    }
}
