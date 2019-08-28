package com.h3c.iclouds.biz.impl;

import java.util.List;

import javax.annotation.Resource;

import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.po.*;
import com.h3c.iclouds.utils.StrUtils;
import org.springframework.stereotype.Service;

import com.h3c.iclouds.base.BaseBizImpl;
import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.biz.StorageVolumsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.dao.Server2VmDao;
import com.h3c.iclouds.dao.StorageVolumsDao;

/**
 * Created by yKF7317 on 2016/11/14.
 */
@Service(value = "storageVolumsBiz")
public class StorageVolumsBizImpl extends BaseBizImpl<StorageVolums> implements StorageVolumsBiz {

    @Resource
    private StorageVolumsDao storageVolumsDao;

    @Resource
    private Server2VmDao server2VmDao;

    @Resource
    private Server2OveBiz server2OveBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<Volume2Host> volume2HostDao;

    @Override
    public PageModel<StorageVolums> findForPage(PageEntity entity) {
        return storageVolumsDao.findForPage(entity);
    }

    /**
     * 查询挂载主机名
     * @param volums
     * @return
     */
    public String findHostName(StorageVolums volums){
        String vid = volums.getId();
        List<Volume2Host> volume2Hosts = volume2HostDao.findByPropertyName(Volume2Host.class, "volumeId", vid);
        StringBuffer hostName = new StringBuffer();
        for (Volume2Host volume2Host : volume2Hosts) {
            String type = volume2Host.getType();
            String id = volume2Host.getHostId();
            if (type.equals("host")){
                Server2Ove server2Ove = server2OveBiz.findById(Server2Ove.class, id);
                hostName.append(server2Ove.getHostName());
            }
            if (type.equals("vm")){
                Server2Vm server2Vm  = server2VmDao.findById(Server2Vm.class, id);
                hostName.append(server2Vm.getVmName());
            }
            if (volume2Hosts.indexOf(volume2Host) < volume2Hosts.size() - 1){
                hostName.append(",");
            }
        }
        return hostName.toString();
    }

    /**
     * 往原本的对象里面添加hostName属性
     * @param page
     * @return
     */
    public PageList<StorageVolums> transPage(PageList<StorageVolums> page){
        List<StorageVolums> volumeList = page.getAaData();
        for (StorageVolums storageVolums : volumeList) {
            String hostName = findHostName(storageVolums);
            storageVolums.setHostName(hostName);
        }
        return page;
    }
    
    @Override
    public void deleteVolumn (StorageVolums volums) {
        List<Volume2Host> volume2Hosts = volume2HostDao.findByPropertyName(Volume2Host.class, "volumeId", volums.getId());
        if (StrUtils.checkCollection(volume2Hosts)) {
            for (Volume2Host volume2Host : volume2Hosts) {
                volume2HostDao.delete(volume2Host);
            }
        }
        storageVolumsDao.delete(volums);
    }
}
