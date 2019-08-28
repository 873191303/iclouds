package com.h3c.iclouds.utils;

import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.common.SimpleCache;
import com.h3c.iclouds.common.UploadFileModal;
import com.h3c.iclouds.po.WorkFlow;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class UploadFileUtils {
	
	public static Logger log = LoggerFactory.getLogger(UploadFileUtils.class);
	
	/**
	 * 获取流程上传路径
	 * @return
	 */
	public static String getWorkFlowUploadPath() {
		String realPath = CacheSingleton.getInstance().getProjectPath();
		File dir = new File(realPath);
		// 获取webapps路径
		String separator = File.separator;
		File targetDir = new File(dir.getParentFile().getParent() + separator + "workflow" + separator);
		if(!targetDir.exists()) {
			targetDir.mkdirs();
		}
		return targetDir.getAbsolutePath();
	}

	/**
	 * 获取流量统计保存路径
	 * @return
	 */
	public static String getNetFlowPath() {
		String realPath = CacheSingleton.getInstance().getProjectPath();
		File dir = new File(realPath);
		File targetDir = new File(dir.getParentFile().getParent() + File.separator + "netflow" + File.separator);
		if(!targetDir.exists()) {
			targetDir.mkdirs();
		}
		return targetDir.getAbsolutePath();
	}
	
	/**
	 * 获取附件上传路径
	 * @return
	 */
	public static String getUploadPath() {
		String realPath = CacheSingleton.getInstance().getProjectPath();
		File dir = new File(realPath);
		String separator = File.separator;
		File targetDir = new File(dir.getParentFile().getParent() + separator + "upload" + separator);
		if(!targetDir.exists()) {
			targetDir.mkdirs();
		}
		return targetDir.getAbsolutePath();
	}
	
	/**
	 * 删除文件
	 * @param fileName
	 * @param path
	 * @return
	 */
	public static boolean deleteFile(String fileName, String path) {
		if(fileName != null && !"".equals(fileName)) {
			File oldFile = new File(path + fileName);
			try {
				if (oldFile != null && oldFile.exists()){
					log.info("Delete file:" + oldFile.getAbsolutePath());
					oldFile.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 删除文件
	 * @param oldFile
	 * @return
	 */
	public static boolean deleteFile(File oldFile) {
		try {
			if (oldFile != null && oldFile.exists()){
				oldFile.delete();
			}
		} catch (Exception e) {
			LogUtils.warn(UploadFileUtils.class, "删除文件异常：" + oldFile.getAbsolutePath());
			LogUtils.exception(UploadFileUtils.class, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 上传文件
	 * @param entity
	 * @param file
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String uploadWorkFlowFile(WorkFlow entity, MultipartFile file, HttpServletRequest request) throws IOException {
		String path = UploadFileUtils.getWorkFlowUploadPath();	// 获取webapps路径
		// 文件原名
		String originalFilename = file.getOriginalFilename();
		// 防止重名
		String fileName = entity.getId() + "_" + originalFilename;
		entity.setFileName(fileName);
		entity.setUploadDate(new Date());
		FileUtils.copyInputStreamToFile(file.getInputStream(), new File(path, fileName));
		return fileName;
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static File uploadAttathmentFile(MultipartFile file, String fileName) {
		String path = UploadFileUtils.getUploadPath();	// 获取webapps路径
		File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File targetFile = new File(path, fileName);
		try {
			file.transferTo(targetFile);
		} catch (IllegalStateException | IOException e1) {
			LogUtils.exception(MultipartFile.class, e1);
		}
		return targetFile;
	}
	
	/**
	 * 删除附件实体及文件
	 * @param key
	 * @param entity
	 */
	public static void deleteFile(String key, UploadFileModal entity) {
		if(entity != null) {
			Vector<String> list = entity.getFileNames();
			for (String fileName : list) {
				String path = UploadFileUtils.getUploadPath();	// 获取webapps路径
				deleteFile(fileName, path);
			}
			entity.setFileNames(null);
			entity = null;
		}
		if(StrUtils.checkParam(key)) {
			SimpleCache.UPLOAD_FILE_MAP.remove(key);	
		}
		RedisUtils.delete(key);	// 删除key
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File uploadAttathmentFile(MultipartFile file) {
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		return uploadAttathmentFile(file, fileName);
	}
	
	/**
	 * 解析流程文件，提取需要使用的内容
	 * @param file
	 * @return
	 */
	public static Map<String, Object> analysisTaskFile(File file) {
		SAXReader reader = new SAXReader();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, String>> roleList = new ArrayList<Map<String, String>>();
		try {
			Document doc = null;
			try {
				doc = reader.read(new FileInputStream(file));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			if(doc == null) {
				return map;
			}
			Element root = doc.getRootElement();
			List<Element> elementList = root.elements();
			for (Element element : elementList) {
				if("process".equals(element.getName())) {
					for(Object a : element.attributes()) {
						DefaultAttribute as = (DefaultAttribute) a;
						if("id".equals(as.getName())) {
							map.put(as.getName(), as.getData().toString());
						}
					}
				}
				Iterator<Element> it = element.elementIterator();
				while(it.hasNext()) {
					Element e = (Element) it.next();
					if("userTask".equals(e.getName())) {
						Map<String, String> taskMap = new HashMap<String, String>();
						for(Object a : e.attributes()) {
							DefaultAttribute as = (DefaultAttribute) a;
							String name = as.getName();
							LogUtils.info(UploadFileUtils.class, as.getName() + ":" + as.getData());
							taskMap.put(name, as.getData().toString());
						}
						List<Element> userChildList = e.elements();
						for(Element childElemet : userChildList) {
							if(childElemet.getName().equals("extensionElements")) {
								List<Element> extensionChildList = childElemet.elements();
								for(Element extensionChildElemet : extensionChildList) {
									boolean flag = false;
									for(Object a : extensionChildElemet.attributes()) {
										DefaultAttribute as = (DefaultAttribute) a;
										String name = as.getData().toString();
										if(name.equals("level")) {
											flag = true;
											break;
										}
										LogUtils.info(UploadFileUtils.class, as.getName() + ":" + as.getData());
									}
									if(flag) {
										for(Object a : extensionChildElemet.attributes()) {
											DefaultAttribute as = (DefaultAttribute) a;
											String name = as.getName();
											if(name.equals("value")) {
												taskMap.put(name, as.getData().toString());	
											}
										}
										break;
									}
								}
							}
						}
						roleList.add(taskMap);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		map.put("roleList", roleList);
		return map;
	}
	
	public static void writeFile(File file, HttpServletResponse response, String contentType) {
		if (null != file && file.exists()) {
			FileInputStream fileInputStream = null;
			try {
				OutputStream outputStream = response.getOutputStream();
				fileInputStream = new FileInputStream(file);
				// 读数据
				byte[] data = new byte[fileInputStream.available()];
				fileInputStream.read(data);
				fileInputStream.close();
				// 回写
				response.setContentType(contentType);
				outputStream.write(data);
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (fileInputStream != null)
						fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 判断是否是图片格式
	 * @param file
	 * @return
	 */
	public static boolean checkPicture(MultipartFile file) {
		//通过文件后缀名判断是否为图片
		String fileName = file.getOriginalFilename();
		String suffixName = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		String [] imageNames = new String[] {"bmp", "jpeg", "png", "jpg"};
 		List<String> imagenameList = Arrays.asList(imageNames);
 		if (!imagenameList.contains(suffixName)) {
			return false;
		}
		//通过查看文件是否能转换成图片判断是否为图片
		Image img = null;
		try {
			InputStream inputStream = file.getInputStream();
			img = ImageIO.read(inputStream);
			if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			img = null;
		}
	}
	
	public static File getUploadFile(String fileName) {
		if(fileName != null && !"".equals(fileName)) {
			String path = getUploadPath();
			File file = new File(path + File.separator + fileName);
			if (null != file && file.exists()){
				return file;
			}
		}
		return null;
	}
	
	/**
	 * 获取项目文件路径
	 * @return
	 */
	public static String getFilePath() {
		String path = CacheSingleton.getInstance().getProjectPath();
		String separator = File.separator;
		return path + "resource" + separator;
	}
	
	/**
	 * 获取项目文件
	 * @param fileName
	 * @return
	 */
	public static File getFile(String fileName) {
		if(fileName != null && !"".equals(fileName)) {
			String path = getFilePath();
			File file = new File(path + fileName);
			if (null != file && file.exists()){
				return file;
			}
		}
		return null;
	}
}
