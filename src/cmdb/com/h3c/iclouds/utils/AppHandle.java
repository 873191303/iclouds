package com.h3c.iclouds.utils;

import java.util.List;

import com.h3c.iclouds.po.bean.ApplicationBean;
import com.h3c.iclouds.common.ConfigProperty;

public class AppHandle {

//	private ApplicationMasterBiz applicationMasterBiz=SpringContextHolder.getBean("applicationMasterBiz");
//	
//	private AppViewsBiz appViewsBiz=SpringContextHolder.getBean("appViewsBiz");
//	
//	private AppRelationsBiz appRelationsBiz=SpringContextHolder.getBean("appRelationsBiz");
//	
//	private DatabaseMasterBiz databaseMasterBiz=SpringContextHolder.getBean("databaseMasterBiz");
//	
//	private AppItemsBiz appItemsBiz=SpringContextHolder.getBean("appItemsBiz");
//	
//	private ServiceClusterBiz serviceClusterBiz=SpringContextHolder.getBean("serviceClusterBiz");
//	
//	private ServiceMasterBiz serviceMasterBiz=SpringContextHolder.getBean("serviceMasterBiz");
	
	/**
     * 给视图操作数据规定一个操作顺序的值
     * 删除-0，增加-1，修改-2
     * app-0，culster-12|13，网络-2，负载均衡-3
     * @param list
     */
    public void sortAppInfo(List<ApplicationBean> list){
        if (StrUtils.checkParam(list)){
            for (ApplicationBean  bean: list) {
                String option = bean.getOption();
                String type = bean.getType();
                StringBuffer sequence = new StringBuffer();
                switch (option){
                    case ConfigProperty.RESOURCE_OPTION_DELETE:
                        sequence.append(ConfigProperty.RESOURCE_OPTION_DELETE);
                        break;
                    case ConfigProperty.RESOURCE_OPTION_ADD:
                        sequence.append(ConfigProperty.RESOURCE_OPTION_ADD);
                        break;
                    case ConfigProperty.RESOURCE_OPTION_UPDATE:
                        sequence.append(ConfigProperty.RESOURCE_OPTION_UPDATE);
                        break;
                    case ConfigProperty.RESOURCE_OPTION_NOCHANGE:
                        sequence.append(ConfigProperty.RESOURCE_OPTION_NOCHANGE);
                        break;
                    default:break;
                }
                switch (type){
                    case "0":
                        sequence.append("0");
                        break;
                    case "12":
                        sequence.append("12");
                        break;
                    case "2":
                        sequence.append("2");
                        break;
                    case "13":
                        sequence.append("13");
                        break;
                    case "3":
                        sequence.append("3");
                    default:break;
                }
                bean.setSequence(sequence.toString());
                //throw new MessageException();
            }
        }
    }
   

    

}
