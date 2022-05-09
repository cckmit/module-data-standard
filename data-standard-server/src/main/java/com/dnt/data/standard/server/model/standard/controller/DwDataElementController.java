package com.dnt.data.standard.server.model.standard.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.annotation.CacheLock;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.entity.ExcelProgressRequest;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDataElementExcel;
import com.dnt.data.standard.server.model.standard.entity.request.DwDataElementRequest;
import com.dnt.data.standard.server.model.standard.entity.response.DwDataElementResponse;
import com.dnt.data.standard.server.model.standard.service.DwDataElementService;
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
 * @description: 数据元--业务代码 <br>
 * @date: 2021/7/21 下午3:28 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/data/element")
@Api(value = "dwDataElement", tags = "数据元管理接口")
@Slf4j
public class DwDataElementController extends BaseController {
    @Autowired
    private DwDataElementService dwDataElementService;

    /**获数数据元分页列表**/
    @ApiOperation(value="获取数据元分页列表", notes="获取数据元分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwDataElementRequest request){
        log.info("DwDataElementController-->pageList 获取数据元分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwDataElementService.selectDataElementPage(request));
    }


    /**查看详情**/
    @ApiOperation(value="查看数据元详情", notes="查看数据元详情接口")
    @GetMapping(value={"/detailDataElement/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据元ID", required = true, dataType = "Long")
    })
    public R detailDataElement(@PathVariable("id") Long id){
        log.info("DwDataElementController--> detailDataElement 查询id为{}的数据元详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看数据元信息时ID不能为空");
        }
        DwDataElementResponse t = this.dwDataElementService.detailDataElement(id);
        return Result.ok(t);
    }

    /**数据元分类列表**/
    @ApiOperation(value="查看数据元分类列表", notes="查看数据元分类列表接口")
    @GetMapping(value={"/selectDataElementTypeItem"})
    public R selectDataElementTypeItem(){
        log.info("DwDataElementController--> selectDataElementTypeItem 查看数据元分类列表接口");

        List<Map<String,Object>> t = this.dwDataElementService.selectDataElementTypeItem();
        return Result.ok(t);
    }

    /**数据元数据元列表**/
    @ApiOperation(value="查看数据字典下拉列表", notes="查看数据字典下拉列表接口")
    @GetMapping(value={"/selectDictItem"})
    public R selectDictItem(){
        log.info("DwDataElementController--> selectDictItem 查询数据字典下拉列表");

        List<Map<String,Object>> t = this.dwDataElementService.selectDictItem();
        return Result.ok(t);
    }

    /**查看详情**/
    @ApiOperation(value="查看平级目录", notes="查看平缓目录接口")
    @GetMapping(value={"/selectParallelCatalogue/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "目录ID", required = true, dataType = "Long")
    })
    public R selectParallelCatalogue(@PathVariable("id") Long id){
        log.info("DwDataElementController--> selectParallelCatalogue 查询id为{}的平级目录信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看平级目录信息时ID不能为空");
        }
        List<Map<String,Object>> pcList = this.dwDataElementService.selectParallelCatalogue(id);
        return Result.ok(pcList);
    }
    /**添加数据元**/
    @ApiOperation(value="添加数据元", notes="添加数据元接口")
    @PostMapping(value={"/saveDataElement"})
    @CacheLock(prefix = "dw-save-data-element")
    public R saveDataElement(@RequestHeader String userCode,
                             @RequestHeader Long projectId,
                          @RequestBody DwDataElementRequest request){
        log.info("DwDataElementController-->saveDataElement 添加数据元 ");
        request.setProjectId(projectId);
        return  this.dwDataElementService.saveDataElement(request,userCode);
    }


    /**修改数据元**/
    @ApiOperation(value="修改数据元", notes="修改数据元接口")
    @PutMapping(value={"/updateDataElement"})
    @CacheLock(prefix = "dw-update-data-element")
    public R updateDataElement(@RequestHeader String userCode,
                               @RequestHeader Long projectId,
                            @RequestBody DwDataElementRequest request){
        log.info("DwDataElementController-->updateDataElement 修改数据元");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改数据元时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwDataElementService.updateDataElement(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"数据元 name ",dwDataElementService);
        return Result.ok(t);
    }

    /**删除数据元**/
    @ApiOperation(value="删除数据元", notes="删除数据元接口")
    @DeleteMapping(value={"/deleteDataElement/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除数据元ID", required = true, dataType = "Long")
    })
    public R deleteDataElement(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwDataElementController-->DataElement 删除数据元时ID为{}信息",id);

        return Result.ok("删除成功了："+ this.dwDataElementService.deleteDataElement(id,userCode)+" 条数据");
    }

    /**
     *  下载导入数据元模板
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value="下载数据元导入模板", notes="下载数据元导入模板接口")
    @GetMapping(value = "/download/import/template")
    public R downloadImportTemplate(HttpServletRequest request, HttpServletResponse response){
        if(log.isDebugEnabled()){
            log.debug("DwDataElementController-->downloadImportTemplate 下载导入数据元模板");
        }
        String fileName = request.getParameter("fileName");
        fileName= StringUtils.isEmpty(fileName)?"数据元模板":fileName;
        // 文件下载
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:template/data/dw_data_element_template.xlsx");
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
            log.error("下载导入数据元模板失败");
        }

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        return Result.ok("下载导入数据元模板操作成功");
    }


    @ApiOperation(value="导出数据元数据", notes="导出数据元数据接口")
    @GetMapping(value = "/download/dataDataElement")
    public R downloadData(HttpServletResponse response,String fileName,String name,Long categoryId) {
        if (log.isDebugEnabled()) {
            log.debug("DwDataElementController-->downloadData 导出数据元数据");
        }
        try {
            fileName = StringUtils.isEmpty(fileName)?"数据元_" + DateFormatUtils.format(new Date(),"yyyyMMddHH"):fileName;
            fileName = URLEncoder.encode(fileName, "utf-8");
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" +fileName +".xls");
            ExcelWriter writer = ExcelUtil.getWriter();
            List<DwDataElementExcel> list = this.dwDataElementService.selectDataElementList(name,categoryId);

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
    @ApiOperation(value="excel模板导入数据元数据", notes="excel模板导入数据元数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "模型文件", required = true, dataType = "MultipartFile"),
            @ApiImplicitParam(name = "userCode", value = "用户编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryId", value = "分类ID", required = true, dataType = "Long")
    })
    @PostMapping(value = "upload/excel")
    public R uploadExcel(@RequestParam MultipartFile uploadFile,
                         @RequestHeader String userCode,
                         @RequestHeader Long projectId,
                         Long categoryId){

        if(!Optional.fromNullable(categoryId).isPresent()){
            return Result.fail("上传数据元数据时分类不能为空");
        }
        String processCode = IdWorker.getId()+"";

        String newFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx" ;
        String fileName = "/tmp" + File.separatorChar+"dnt" +File.separatorChar +"uploadFile"+ File.separatorChar  +DateFormatUtils.format(new Date(), "yyyyMMdd") +File.separatorChar+"data-element"+File.separatorChar+ newFileName;
        File uFile = new File(fileName);
        try {
            createFileMkdirs(uFile);
            uploadFile.transferTo(uFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.dwDataElementService.uploadExcel(processCode,uFile,userCode,projectId,categoryId);

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
    @PostMapping(value = "getImportProgress")
    public R getImportProgress(@RequestBody ExcelProgressRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementController-->getImportProgress 获取导入文件进度");
        }
        if (StringUtils.isEmpty(request.getProcessCode())) {
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","操作进度的批次号不能为空");
            return Result.ok(mm);
        }

        return this.dwDataElementService.getImportProgress(request.getProcessCode());

    }

}
