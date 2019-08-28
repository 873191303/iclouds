package com.h3c.iclouds.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.h3c.iclouds.common.ConfigProperty;
import com.h3c.iclouds.po.Department;
import com.h3c.iclouds.po.Resource;

import edu.emory.mathcs.backport.java.util.Collections;

public class TreePick {
	
	public static List<Resource> getUserResource(List<Resource> list, int index) {
		List<Resource> resultList = new ArrayList<Resource>();
		for (Resource entity : list) {
			if(index != -1) {	// 超级管理员不做过滤
				String sysType = entity.getSysType();
				if(ConfigProperty.YES.equals(sysType.substring(index, index + 1))) {	// 当前角色无权限授权该资源菜单
					resultList.add(entity);
				}
			} else {
				resultList.add(entity);
			}
		}
		return resultList;
	}
	
	public static void sortResource(List<Resource> list) {
		Collections.sort(list, new Comparator<Resource>() {
			@Override
			public int compare(Resource o1, Resource o2) {
				if(o1.getItemSeq() == null) {
					return 1;
				}
				if(o2.getItemSeq() == null) {
					return -1;
				}
				return o1.getItemSeq().compareTo(o2.getItemSeq());
			}
		});
		
		for(Resource entity : list) {
			if(entity.getChildren() != null) {
				sortResource(entity.getChildren());
			}
		}
	}
	
	public static List<Resource> pickResource(List<Resource> list) {
		return pickResource(list, null);
	}
	
	public static List<Resource> pickResource(List<Resource> list, String startId) {
		Map<String, Resource> rootMap = new HashMap<String, Resource>();
		Map<String, Resource> map = new HashMap<String, Resource>();
		Map<String, List<Resource>> childrenMap = new HashMap<String, List<Resource>>();
		if(list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Resource entity = list.get(i);
				String id = entity.getId();
				String pid = entity.getParentId();
				map.put(id, entity);
				if("-1".equals(pid)) {	//最顶层
					rootMap.put(id, entity);
				} else {
					if(map.containsKey(pid)) {	// 存在父id
						Resource p = map.get(pid);
						List<Resource> children = p.getChildren();
						if(children == null) {
							children = new ArrayList<Resource>();
							p.setChildren(children);
						}
						children.add(entity);
					} else {
						List<Resource> children = childrenMap.get(pid);
						if(children == null) {
							children = new ArrayList<Resource>();
							childrenMap.put(pid, children);
						}
						children.add(entity);
					}
				}
				if(childrenMap.containsKey(id)) {
					entity.setChildren(childrenMap.get(id));
					childrenMap.remove(id);
				}
			}
		}
		List<Resource> result = new ArrayList<Resource>();
		if(startId == null) {
			result.addAll(rootMap.values());
		} else {
			if(rootMap.containsKey(startId)) {
				result.add(rootMap.get(startId));
			}
		}
		sortResource(result);
		return result;
	}
	
	public static List<Department> pickDepartment(List<Department> list) {
		Map<String, Department> rootMap = new HashMap<String, Department>();
		Map<String, Department> map = new HashMap<String, Department>();
		Map<String, List<Department>> childrenMap = new HashMap<String, List<Department>>();
		if(list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Department entity = list.get(i);
				String id = entity.getId();
				String pid = entity.getParentId();
				map.put(id, entity);
				if("-1".equals(pid)) {	//最顶层
					rootMap.put(id, entity);
				} else {
					if(map.containsKey(pid)) {	// 存在父id
						Department p = map.get(pid);
						List<Department> children = p.getChildren();
						if(children == null) {
							children = new ArrayList<Department>();
							p.setChildren(children);
						}
						children.add(entity);
					} else {
						List<Department> children = childrenMap.get(pid);
						if(children == null) {
							children = new ArrayList<Department>();
							childrenMap.put(pid, children);
						}
						children.add(entity);
					}
				}
				if(childrenMap.containsKey(id)) {
					entity.setChildren(childrenMap.get(id));
					childrenMap.remove(id);
				}
			}
		}
		List<Department> result = new ArrayList<Department>();
		result.addAll(rootMap.values());
		return result;
	}
}
