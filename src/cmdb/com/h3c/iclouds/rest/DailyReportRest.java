package com.h3c.iclouds.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.SeriesType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Series;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.Cvm2OveBiz;
import com.h3c.iclouds.biz.Server2OveBiz;
import com.h3c.iclouds.biz.Storage2OveBiz;
import com.h3c.iclouds.po.Cvm2Ove;
import com.h3c.iclouds.po.Server2Ove;
import com.h3c.iclouds.po.Storage2Ove;
import com.h3c.iclouds.utils.PictureSingleton;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "日报表", description = "日报表")
@RestController
@RequestMapping("/dailyreport")
public class DailyReportRest {

	@Resource
	private Server2OveBiz server2OveBiz;

	@Resource
	private Storage2OveBiz storage2OveBiz;

	@Resource
	private Cvm2OveBiz cvm2OveBiz;

	@ApiOperation(value = "日报表")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object report() {
		List<Server2Ove> server2Oves = server2OveBiz.findTop5();
		List<Cvm2Ove> cvm2Oves = cvm2OveBiz.findTop5();
		List<Storage2Ove> storage2Oves = storage2OveBiz.findTop5();
		
		JSONObject serverOption=getOption(server2Oves,"主机");
		JSONObject cvmOption=getOption(cvm2Oves,"CVM");
		JSONObject storageOption=getOption(storage2Oves,"储存");
		
		Map<String, JSONObject> map=new HashMap<>();
		map.put("server2Ove", serverOption);
		map.put("cvm2Ove", cvmOption);
		map.put("storage2Ove", storageOption);
		
		return BaseRestControl.tranReturnValue(map);
	}

	@ApiOperation(value = "页面返回图片")
	@RequestMapping(value = "/picture", method = RequestMethod.POST)
	public void rePicture(@RequestBody Map<String, String> map) {
		PictureSingleton pic = PictureSingleton.getInstance();
		if (map != null && !map.isEmpty()) {
			for (String key : map.keySet()) {
				if ("server2Ove".equals(key)) {
					pic.setServer2Ove(map.get(key));
				} else if ("cvm2Ove".equals(key)) {
					pic.setCvm2Ove(map.get(key));
				} else if ("storage2Ove".equals(key)) {
					pic.setStorage2Ove(map.get(key));
				}
			}
		}
	}

	public JSONObject getOption(Object obj,String type) {
		
		String[] riqiArr = null;
		String[] legendName = null;
		List<Series> seriess = null;
		Option option=new GsonOption();
		Bar bar1 = new Bar() ;
	    Bar bar2 = new Bar() ;
		if("主机".equals(type)){
			List<Server2Ove> list=(List<Server2Ove>)obj;
			legendName= new String[]{"CPU超配率", "存储超配率"};
			riqiArr=new String[list.size()];
			for(int i=0;i<list.size();i++){
				seriess = new ArrayList<Series>() ;
				riqiArr[i]=list.get(i).getHostName();
				bar1.name("CPU超配率").type(SeriesType.bar).data(list.get(i).getCpuOverSize()/list.get(i).getCpus()*100).setBarWidth(35);
				bar2.name("存储超配率").type(SeriesType.bar).data(list.get(i).getRamOverSize()/list.get(i).getRam()*100).setBarWidth(35);
				seriess.add(bar1) ;
				seriess.add(bar2) ;
				option.setSeries(seriess);	
			}
		}else if("CVM".equals(type)){
			List<Cvm2Ove> list=(List<Cvm2Ove>)obj;
			riqiArr=new String[list.size()];
			for(int i=0;i<list.size();i++){
				seriess = new ArrayList<Series>() ;
				riqiArr[i]=list.get(i).getCvmName();
				bar1.name("CPU超配率").type(SeriesType.bar).data(((float)list.get(i).getAssignCpu())/list.get(i).getTotalCpu()*100).setBarWidth(35);
				bar2.name("存储超配率").type(SeriesType.bar).data(((float)list.get(i).getAssignMem())/list.get(i).getTotalMem()*100).setBarWidth(35);
				seriess.add(bar1) ;
				seriess.add(bar2) ;
				option.setSeries(seriess);	
			}
			legendName= new String[]{"CPU超配率", "存储超配率"};
		}else if("储存".equals(type)){
			List<Storage2Ove> list=(List<Storage2Ove>)obj;
			riqiArr=new String[list.size()];
			for(int i=0;i<list.size();i++){
				seriess = new ArrayList<Series>() ;
				riqiArr[i]=list.get(i).getName();
				bar2.name("存储超配率").type(SeriesType.bar).data(list.get(i).getCapaOverflow()).setBarWidth(35);
				seriess.add(bar2) ;
				option.setSeries(seriess);	
			}
			legendName= new String[]{"存储超配率"};
		}
		
		
		   option.title().text(type+"超配");
		   option.legend().data(legendName);
		   CategoryAxis xaxis = new CategoryAxis();
		   xaxis.type(AxisType.category).data(riqiArr).setName(type+"名称");;
		   ValueAxis yaxis = new ValueAxis();
		   yaxis.type(AxisType.value).setName("超配率（%）");;
		   option.xAxis(xaxis);
		   option.yAxis(yaxis);
		  
		   
		   JSONObject jsonObj=JSONObject.parseObject(option.toString());
		   return jsonObj;
	}
}
