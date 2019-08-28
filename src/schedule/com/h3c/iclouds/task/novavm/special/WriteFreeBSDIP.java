package com.h3c.iclouds.task.novavm.special;

import static java.io.File.separator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.h3c.iclouds.auth.CacheSingleton;
import com.h3c.iclouds.client.CasClient;
import com.h3c.iclouds.exception.MessageException;
import com.h3c.iclouds.po.NovaVm;
import com.h3c.iclouds.utils.Ssh2Utils;
import com.h3c.iclouds.utils.HttpUtils;
import com.h3c.iclouds.utils.StrUtils;

/**
 * Created by zkf5485 on 2017/6/9.
 */
public class WriteFreeBSDIP {

    private static final String SOURCE_FILE = "rc.conf";

    private static CacheSingleton singleton = CacheSingleton.getInstance();

    public synchronized static List<String> handle(NovaVm novaVm, Map<String, String> map) {
        // 获取新配置文件路径
        String path = writeBySourceFile(map, novaVm.getId());
        if(path == null) {
            throw MessageException.create("云主机[id:" + novaVm.getId() + "]创建新的配置文件失败");
        }
        // 获取cas中云主机信息
        CasClient casClient = CasClient.createByCasId(CasClient.CAS_INTERFACE_ID);
        if(casClient == null) {
            throw MessageException.create("Get Cas Client failure");
        }
        String casUri = "/cas/casrs/vm/vmList?uuid=" + novaVm.getUuid();
        JSONObject domain = HttpUtils.getJSONObject(casClient.get(casUri), "domain");
        if(domain == null) {
            throw MessageException.create("在Cas 获取[uuid:" + novaVm.getUuid() + "]失败");
        }

        // 获取增量文件路径
        String cloudCasId = domain.getString("id");
        JSONObject storeObj = casClient.getStorePath(cloudCasId);
        String storeFile = storeObj.getString("storeFile");
        if(storeFile == null) {
            throw MessageException.create("创建云主机[" + novaVm.getId() + "]类型不对，未获取到增量文件内容");
        }

        // 获取宿主机id
        String hostCasId = domain.getString("hostId");
        JSONObject hostObj = casClient.getHostIpById(hostCasId);
        if(hostObj == null) {
            throw MessageException.create("在Cas 获取[uuid:" + novaVm.getUuid() + "]失败");
        }
        // 宿主机ip，用于ssh连接
        String hostIp = hostObj.getString("ip");
        Ssh2Utils ssh = null;
        try {
            // TODO: 2017/6/13 由于测试环境的server地址不对，暂时写死
            hostIp = "10.88.10.60";
            // 创建ssh连接
            ssh = Ssh2Utils.createCasHostSSH(hostIp);
            if(ssh == null) {
                throw MessageException.create("SSH connection to host ip [" + hostIp + "] failure");
            }
            String targetDir = singleton.getTonghuashunConfigKey("tonghuashun.rc.conf.path");
            boolean scpResult = ssh.execScp(path, targetDir);
            if(!scpResult) {
                throw MessageException.create("SCP[hostip:" + hostIp + "] file failure. ");
            }
            String cmd = "sh ip.sh " + storeFile;
            List<String> resultList = ssh.execCmd(cmd);
            boolean result = false;
            if(StrUtils.checkCollection(resultList)) {
                for (String temp : resultList) {
                    if(temp.equals("success")) {
                        result = true;
                    }
                }
            }
            if(!result) {
                throw MessageException.create("Exec ip.sh failure. result:" + JSONObject.toJSONString(resultList));
            }
            return resultList;
        } catch (Exception e) {
            throw e;
        } finally {
            if(ssh != null) {
                ssh.close();
            }
        }
    }

    /**
     * 创建新的文件（云主机uuid+时间戳）
     * 写入替换后的内容
     * 返回完整路径
     * @param map       替换文件内容
     * @param uuid      云主机id
     * @return
     */
     private static String writeBySourceFile(Map<String, String> map, String uuid) {
        String projectPath = singleton.getProjectPath();
        File projectDir = new File(projectPath);
        String dir = projectDir.getParentFile().getParent() + separator + "rcFile" + separator + uuid + System.currentTimeMillis();
        File rcFile = new File(dir);
        if(!rcFile.exists()) {
            rcFile.mkdirs();
        }
        String newFilePath = dir + separator + SOURCE_FILE;
        File newFile = createFile(newFilePath);
        if(newFile == null) {
            return null;
        }
        Path path = Paths.get(projectPath + separator + "resource" + separator + SOURCE_FILE);
        List<String> contents = null;
        try {
            contents = Files.readAllLines(path, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(contents == null) {
            throw MessageException.create("Not read content, path: " + path.toAbsolutePath());
        }
        writeFileContent(newFile, contents, map);
        return newFilePath;
    }

    /**
     * 向文件中写入内容
     *
     * @param file      写入文件
     * @param contents   写入的内容
     * @return
     * @throws IOException
     */
    private static boolean writeFileContent(File file, List<String> contents, Map<String, String> map) {
        Boolean bool = false;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            final StringBuffer buffer = new StringBuffer();
            contents.forEach(content -> {
                if(content.contains("$$")) {
                    String[] array = content.split("\\$\\$");
                    int len = array.length;
                    for (int i = 1; i < len; i += 2) {
                        String key = array[i];
                        String value = map.get(key);
                        if(value == null) {
                            throw MessageException.create("Miss port replace Key:" + key);
                        }
                        content = content.replace("$$" + key + "$$", value);
                    }
                }
                buffer.append(content + "\r\n");
            });
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bool;
    }

    /**
     * 根据文件名创建文件
     * @param fileName    文件名称
     * @return 是否创建成功，成功则返回true
     */
    private static File createFile(String fileName) {
        File file = new File(fileName);
        try {
            //如果文件不存在，则创建新的文件
            if (!file.exists()) {
                file.createNewFile();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
