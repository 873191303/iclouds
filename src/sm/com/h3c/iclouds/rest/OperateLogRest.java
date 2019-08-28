package com.h3c.iclouds.rest;

import com.h3c.iclouds.base.BaseDAO;
import com.h3c.iclouds.base.BaseRest;
import com.h3c.iclouds.base.BaseRestControl;
import com.h3c.iclouds.biz.OperateLogsBiz;
import com.h3c.iclouds.common.PageEntity;
import com.h3c.iclouds.common.PageList;
import com.h3c.iclouds.common.PageModel;
import com.h3c.iclouds.common.ResultType;
import com.h3c.iclouds.po.LogType;
import com.h3c.iclouds.po.OperateLogs;
import com.h3c.iclouds.utils.StrUtils;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志
 * Created by yKF7317 on 2017/2/10.
 */
@Api(value = "日志信息")
@RestController
@RequestMapping("/operateLog")
public class OperateLogRest extends BaseRest<OperateLogs> {

    @Resource
    private OperateLogsBiz operateLogBiz;

    @Resource(name = "baseDAO")
    private BaseDAO<LogType> logTypeDao;

    @Override
    @ApiOperation(value = "获取日志分页列表", response = OperateLogs.class)
    @RequestMapping(method = RequestMethod.GET)
    public Object list() {
        PageEntity entity = this.beforeList();
        entity = getPageEntity(entity);
        PageModel<OperateLogs> pageModel = operateLogBiz.findForPage(entity);
        PageList<OperateLogs> page = new PageList<>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
    }

    @ApiOperation(value = "获取某个资源的日志分页列表", response = OperateLogs.class)
    @RequestMapping(value = "/resource/{resourceId}", method = RequestMethod.GET)
    public Object resourceList(@PathVariable String resourceId) {
        PageEntity entity = this.beforeList();
        entity.setSpecialParam(resourceId);
        PageModel<OperateLogs> pageModel = operateLogBiz.findForPage(entity);
        PageList<OperateLogs> page = new PageList<OperateLogs>(pageModel, entity.getsEcho());
        return BaseRestControl.tranReturnValue(page);
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
    public Object save(@RequestBody OperateLogs entity) {
        return null;
    }

    @Override
    public Object update(@PathVariable String id, @RequestBody OperateLogs entity) throws IOException {
        return null;
    }

    @ApiOperation(value = "导出日志列表", response = OperateLogs.class)
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) throws IOException, WriteException {
        PageEntity entity = this.beforeList();
        entity = getPageEntity(entity);
        entity.setPageSize(-1);
        PageModel<OperateLogs> pageModel = operateLogBiz.findForPage(entity);
        PageList<OperateLogs> page = new PageList<OperateLogs>(pageModel, entity.getsEcho());
        List<OperateLogs> operateLogsList = page.getAaData();
        String fileName = "vmLog.xls";
        try {
            OutputStream os = response.getOutputStream();// 取得输出流
            response.reset();// 清空输出流
            response.setHeader("Content-disposition", "attachment; filename="+ java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/msexcel");// 定义输出类型
            String [] titleList = new String[6];
            titleList[0] = "资源名称";
            titleList[1] = "描述";
            titleList[2] = "结果";
            titleList[3] = "用户名称";
            titleList[4] = "登录ip";
            titleList[5] = "备注";
            exportExcel(titleList, operateLogsList, os);
        } catch (Exception e) {
            this.exception(e);
        }
    }

    public List<String> getLogTypeId(String type){
        List<String> logTypeIds = new ArrayList<>();
        String hql = "from LogType where description like '%"+type+"%'";
        List<LogType> logTypes = logTypeDao.findByHql(hql);
        if (StrUtils.checkParam(logTypes)){
            for (LogType logType : logTypes) {
                logTypeIds.add(logType.getId());
            }
        }
        return logTypeIds;
    }

    public void exportExcel(String[] title, List<OperateLogs> operateLogsList, OutputStream os) {
        try {
            //创建工作簿
            WritableWorkbook workbook = Workbook.createWorkbook(os);
            //创建工作表
            WritableSheet sheet = workbook.createSheet("Sheet1", 0);
            //设置纵横打印（默认为纵打）、打印纸
            jxl.SheetSettings sheetset = sheet.getSettings();
            sheetset.setProtected(false);
            //设置单元格字体
            WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 10);
            WritableFont BoldFont = new WritableFont(WritableFont.ARIAL, 10,WritableFont.BOLD);

            // 用于标题居中
            WritableCellFormat wcf_center = new WritableCellFormat(BoldFont);
            wcf_center.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
            wcf_center.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
            wcf_center.setAlignment(Alignment.CENTRE); // 文字水平对齐
            wcf_center.setWrap(false); // 文字是否换行

            // 用于正文居左
            WritableCellFormat wcf_left = new WritableCellFormat(NormalFont);
            wcf_left.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
            wcf_left.setVerticalAlignment(VerticalAlignment.CENTRE); // 文字垂直对齐
            wcf_left.setAlignment(Alignment.LEFT); // 文字水平对齐
            wcf_left.setWrap(false); // 文字是否换行

            //以下是EXCEL第一行列标题
            for (int i = 0; i < title.length; i++) {
                sheet.addCell(new Label(i, 0, title[i], wcf_center));
            }

            //以下是EXCEL正文数据
            for (int i = 0; i < operateLogsList.size(); i++) {
                OperateLogs operateLogs = operateLogsList.get(i);
                int row = i + 1;
                String result = getResult(operateLogs.getResult());
                sheet.addCell(new Label(0, row, operateLogs.getResourceName(), wcf_left));
                sheet.addCell(new Label(1, row, operateLogs.getLogTypeName(), wcf_left));
                sheet.addCell(new Label(2, row, result, wcf_left));
                sheet.addCell(new Label(3, row, operateLogs.getUserName(), wcf_left));
                sheet.addCell(new Label(4, row, operateLogs.getIp(), wcf_left));
                sheet.addCell(new Label(5, row, operateLogs.getRemark(), wcf_left));
            }

            workbook.write();
            workbook.close();
        } catch (IOException e) {
            this.exception(OperateLogs.class, e);
        } catch (WriteException e) {
            this.exception(OperateLogs.class, e);
        }
    }

    public PageEntity getPageEntity(PageEntity entity){
        if (StrUtils.checkParam(entity.getQueryMap().get("type"))){
            String type = entity.getQueryMap().get("type").toString();
            List<String> logTypeIds = getLogTypeId(type);
            if (StrUtils.checkParam(logTypeIds)){
                entity.setSpecialParams(logTypeIds.toArray(new String [logTypeIds.size()]));
            }else {
                entity.setSpecialParams(new String[]{"null"});
            }
        }
        if (StrUtils.checkParam(entity.getQueryMap().get("resourceId"))){
            String resourceId = entity.getQueryMap().get("resourceId").toString();
            entity.setSpecialParam(resourceId);
        }
        return entity;
    }
    
    private String getResult(String result) {
        try {
            ResultType rs = ResultType.valueOf(result);
            return rs.getOpeValue();
        } catch (IllegalArgumentException e) {
            return result;
        }
    }
}
