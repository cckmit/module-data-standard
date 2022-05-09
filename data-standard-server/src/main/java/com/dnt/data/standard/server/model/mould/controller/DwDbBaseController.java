package com.dnt.data.standard.server.model.mould.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.excel.DwDbBaseExcel;
import com.dnt.data.standard.server.model.mould.entity.request.DwDbBaseRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwDbBaseResponse;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.entity.ExcelProgressRequest;
import com.dnt.data.standard.server.model.mould.service.DwDbBaseService;
import com.dnt.data.standard.server.utils.FileUtils;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据基础库--业务代码 <br>
 * @date: 2021/7/29 下午1:52 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/db/base")
@Api(value = "dwDbBase", tags = "数据基础库")
@Slf4j
public class DwDbBaseController extends BaseController {

    @Autowired
    private DwDbBaseService dwDbBaseService;

    /**获取数据基础库分页列表**/
    @ApiOperation(value="获取数据基础库分页列表", notes="获取数据基础库分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwDbBaseRequest request){
        log.info("DwDbBaseController-->pageList 获取数据基础库分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwDbBaseService.selectDbBasePage(request));
    }

    /**查看详情**/
    @ApiOperation(value="查看数据基础库详情", notes="查看数据基础库情接口")
    @GetMapping(value={"/detailDbBase/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据基础库ID", required = true, dataType = "Long")
    })
    public R detailDbBase(@PathVariable("id") Long id){
        log.info("DwDbBaseController--> detailDbBase 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看数据基础库信息时ID不能为空");
        }
        DwDbBaseResponse t = this.dwDbBaseService.detailDbBase(id);
        return Result.ok(t);
    }

    /**添加数据基础库**/
    @ApiOperation(value="添加数据基础库", notes="添加数据基础库接口")
    @PostMapping(value={"/saveDbBase"})
    public R saveDbBase(@RequestHeader String userCode,
                        @RequestHeader Long projectId,
                             @RequestBody DwDbBaseRequest request){
        log.info("DwDbBaseController-->saveDbBase 添加数据基础库 ");
        request.setProjectId(projectId);
        return  this.dwDbBaseService.saveDbBase(request,userCode);
    }

    /**修改数据基础库**/
    @ApiOperation(value="修改数据基础库", notes="修改数据基础库接口")
    @PutMapping(value={"/updateDbBase"})
    public R updateDbBase(@RequestHeader String userCode,
                               @RequestHeader Long projectId,
                               @RequestBody DwDbBaseRequest request){
        log.info("DwDbBaseController-->updateDbBase 修改数据基础库");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改数据基础库时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwDbBaseService.updateDbBase(request,userCode);
    }

    /**检验字段指定的字段与值是否重复**/
    @ApiOperation(value="检验字段值是否重复", notes="检验字段值是否重复接口")
    @GetMapping(value={"/remoteCheck"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryId", value = "分类ID", required = false, dataType = "Long")
    })
    public R remoteCheck(@RequestParam("value") String value,
                         @RequestParam("oldValue")   String oldValue,
                         @RequestParam("property")   String property,
                         @RequestParam("categoryId") Long categoryId){
        if(StringUtils.isEmpty(property)){
            return Result.fail("检查字段信息不能为空");
        }
        if(StringUtils.isEmpty(value)){
            return Result.fail("检查字段的内容信息不能为空");
        }

        boolean t = remoteCheck(property,oldValue,value,categoryId,"数据基础库 name ",dwDbBaseService);
        return Result.ok(t);
    }

    /**删除数据基础库**/
    @ApiOperation(value="删除数据基础库", notes="删除数据基础库接口")
    @DeleteMapping(value={"/deleteDbBase/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除数据基础库ID", required = true, dataType = "Long")
    })
    public R deleteDbBase(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwDbBaseController-->deleteDbBase 删除数据基础库时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除数据基础库信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwDbBaseService.deleteDbBase(id,userCode)+" 条数据");
    }


    /**
     *  下载导入数据基础库模板
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value="下载导入数据基础库模板", notes="下载导入数据基础库模板接口")
    @GetMapping(value = "/download/import/template")
    public R downloadImportTemplate(HttpServletRequest request, HttpServletResponse response){
        if(log.isDebugEnabled()){
            log.debug("DwDbBaseController-->downloadImportTemplate 下载导入数据基础库模板");
        }
        String fileName = request.getParameter("fileName");
        fileName= StringUtils.isEmpty(fileName)?"数据基础库模板":fileName;

        // 文件下载
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:template/data/dw_db_base_template.xlsx");
            InputStream inputStream = resource.getInputStream();
            File tempFile = File.createTempFile(fileName,".xlsx");
            try{
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream,tempFile);
            }finally {
                IOUtils.closeQuietly(inputStream);
            }

            response.addHeader("Content-Disposition", "attachment; filename=" + fileName+".xlsx");

            FileUtils.downFile(tempFile,request,response,fileName+".xlsx");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("下载导入数据基础库模板失败");
        }

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        return Result.ok("下载导入数据基础库模板操作成功");
    }

    /**导出数据基础库数据**/
    @ApiOperation(value="导出数据基础库数据", notes="导出数据基础库数据接口")
    @GetMapping(value = "/download/dataDbBase")
    public R downloadData(HttpServletResponse response,String fileName,
                          String name,Long categoryId) {
        if (log.isDebugEnabled()) {
            log.debug("DwDbBaseController-->downloadData 导出数据基础库数据");
        }
        try {
            fileName = StringUtils.isEmpty(fileName)?"数据基础库_" + DateFormatUtils.format(new Date(),"yyyyMMddHH"):fileName;
            fileName = URLEncoder.encode(fileName, "utf-8");
            ServletOutputStream out = response.getOutputStream();
            response.reset();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" +fileName +".xls");
            ExcelWriter writer = ExcelUtil.getWriter();
            List<DwDbBaseExcel> list = this.dwDbBaseService.selectDbBaseList(name,categoryId);

            writer.write(list);
            writer.flush(out);
            //关闭
            IoUtil.close(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        return Result.ok("操作成功");
    }


    /**
     * 文件批量导入操作
     * @param uploadFile
     * @param categoryId
     * @return
     */
    @ApiOperation(value="excel模板导入数据基础库数据", notes="excel模板导入数据基础库数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "模型文件", required = true, dataType = "MultipartFile"),
            @ApiImplicitParam(name = "userCode", value = "用户编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryId", value = "数据基础库分类ID", required = true, dataType = "Long")
    })
    @PostMapping(value = "upload/excel")
    public R uploadExcel(@RequestParam MultipartFile uploadFile,
                         @RequestHeader String userCode,
                         @RequestHeader Long projectId,
                         Long categoryId,HttpServletRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseController-->uploadExcel excel模板导入数据基础库数据 ");
        }

        String filePath = request.getServletContext().getRealPath("/");

        log.info("======filePath:===" + filePath);
        if(!Optional.fromNullable(categoryId).isPresent()){
            return Result.fail("上传数据基础库数据时分类不能为空");
        }
        String processCode = IdWorker.getId()+"";

        String newFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx" ;
        String fileName = "/tmp" + File.separatorChar+"dnt" +File.separatorChar +"uploadFile"+ File.separatorChar  +DateFormatUtils.format(new Date(), "yyyyMMdd") +File.separatorChar+"dbbase"+File.separatorChar+ newFileName;
        File uFile = new File(fileName);
        try {
            createFileMkdirs(uFile);
            uploadFile.transferTo(uFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.dwDbBaseService.uploadExcel(processCode,uFile,userCode,projectId,categoryId);

        Map<String,Object> rmp = new HashMap<>();
        rmp.put("processCode",processCode);
        return Result.ok(rmp);

    }

    /**
     * 获取导入文件进度
     * @param request
     * @return
     */
    @ApiOperation(value="上传进度条", notes="上传进度条")
    @PostMapping(value = "/getImportProgress")
    public R getImportProgress(@RequestBody ExcelProgressRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseController-->uploadExcel 获取导入文件进度 ");
        }
        if (StringUtils.isEmpty(request.getProcessCode())) {
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","操作进度的批次号不能为空");
            return Result.ok(mm);
        }

        return this.dwDbBaseService.getImportProgress(request.getProcessCode());

    }
}
