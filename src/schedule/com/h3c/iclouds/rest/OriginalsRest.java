package com.h3c.iclouds.rest;

import com.h3c.iclouds.auth.SessionBean;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.dao.OriginalsDao;
import com.h3c.iclouds.po.Originals;
import com.h3c.iclouds.utils.UploadFileUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by zkf5485 on 2017/9/5.
 */
@RestController
@RequestMapping("/netflow")
@Api(value = "同步差异处理")
public class OriginalsRest extends BaseRestControl {

    @Resource
    private OriginalsDao originalsDao;

    @ApiOperation(value = "获取流量数据", response = Originals.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list (
            @RequestParam(value = "startDate") Long startDate,
            @RequestParam(value = "endDate") Long endDate,
            @RequestParam(value = "tenantId") String tenantId) {

        // 判断租户
        SessionBean sessionBean = this.getSessionBean();
        if(!sessionBean.getSuperUser() && !tenantId.equals(sessionBean.getProjectId())) {
            return BaseRestControl.tranReturnValue(new PageList<Originals>());
        }
        return BaseRestControl.tranReturnValue(this.originalsDao.findOneDateData(tenantId, new Date(startDate), new Date(endDate)));
    }

    @ApiOperation(value = "验证流量月份文件是否存在", response = Originals.class)
    @RequestMapping(value = "/{tenantId}/check", method = RequestMethod.GET)
    public Object get(@PathVariable String tenantId, @RequestParam(value = "date") String date) {
        // 判断租户
        SessionBean sessionBean = this.getSessionBean();
        if(!sessionBean.getSuperUser() && !tenantId.equals(sessionBean.getProjectId())) {
            return BaseRestControl.tranReturnValue(ResultType.unAuthorized);
        }

        String fileName = date + "." + tenantId + ".xls";
        File file = new File(UploadFileUtils.getNetFlowPath(), fileName);
        if(!file.exists()) {
            return BaseRestControl.tranReturnValue(ResultType.not_found_netflow);
        }
        return BaseRestControl.tranReturnValue(ResultType.success);
    }

    @ApiOperation(value = "下载流量月份文件", response = Originals.class)
    @RequestMapping(value = "/{tenantId}/download", method = RequestMethod.GET)
    public void download(@PathVariable String tenantId, @RequestParam(value = "date") String date, HttpServletResponse response) {
        // 判断租户
        SessionBean sessionBean = this.getSessionBean();
        if(!sessionBean.getSuperUser() && !tenantId.equals(sessionBean.getProjectId())) {
            try {
                response.getOutputStream().write(ResultType.unAuthorized.getOpeValue().getBytes(Charset.forName("UTF-8")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String fileName = date + "." + tenantId + ".xls";
        File file = new File(UploadFileUtils.getNetFlowPath(), fileName);
        try {
            if(!file.exists()) {
                response.getOutputStream().write(ResultType.not_found_netflow.getOpeValue().getBytes(Charset.forName("UTF-8")));
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            String projectName = singleton.getProjectNameMap().get(tenantId);
            String loadName = date + "." + projectName + ".xls";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(loadName, "UTF-8"));
            response.setHeader("Connection", "close");
            in = new BufferedInputStream(new FileInputStream(file));
            out = new BufferedOutputStream(response.getOutputStream());
            byte[] data = new byte[1024];
            int len;
            while (-1 != (len=in.read(data, 0, data.length))) {
                out.write(data, 0, len);
            }
        } catch (IOException e) {
            this.exception(e, "Download file failure");
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                this.exception(e, "Close stream failure.");
            }
        }
    }

}
