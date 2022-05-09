package com.dnt.data.standard.server.model.standard.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.entity.ExcelProgressRequest;
import com.dnt.data.standard.server.model.mould.entity.DwOperationLog;
import com.dnt.data.standard.server.model.mould.service.DwMouldService;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDictExcel;
import com.dnt.data.standard.server.model.standard.entity.request.DwDictPhysicsRequest;
import com.dnt.data.standard.server.model.standard.entity.request.DwDictRequest;
import com.dnt.data.standard.server.model.standard.entity.response.DwDictResponse;
import com.dnt.data.standard.server.model.standard.service.DwDictService;
import com.dnt.data.standard.server.utils.FileUtils;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
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
import java.util.concurrent.TimeUnit;

/**
 * @description: 数据字典--业务代码 <br>
 * @date: 2021/7/12 下午1:37 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/dict")
@Api(value = "dwDict", tags = "数据字典接口")
@Slf4j
public class DwDictController extends BaseController {
    @Autowired
    private DwDictService dwDictService;
    @Autowired
    private DwMouldService dwMouldService;


    //获数据字典分页列表
    @ApiOperation(value="获取数据字典分页列表", notes="获取数据字典分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwDictRequest request){
        log.info("DwDictController-->pageList 获取数据字典分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwDictService.selectDictPage(request));
    }

    //查看详情
    @ApiOperation(value="查看数据字典详情", notes="查看数据字典详情接口")
    @GetMapping(value={"/detailDict/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据字典ID", required = true, dataType = "Long")
    })
    public R detailDict(@PathVariable("id") Long id){
        log.info("DwDictController--> detailDict 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看数据字典信息时ID不能为空");
        }
        DwDictResponse t = this.dwDictService.detailDict(id);
        return Result.ok(t);
    }

    //添加数据字典
    @ApiOperation(value="添加数据字典", notes="添加数据字典接口")
    @PostMapping(value={"/saveDict"})
    public R saveDict(@RequestHeader String userCode,
                          @RequestBody DwDictRequest request){
        log.info("DwDictController-->saveDict 添加数据字典 ");

        return  this.dwDictService.saveDict(request,userCode);
    }


    //修改数据字典
    @ApiOperation(value="修改数据字典", notes="修改数据字典接口")
    @PutMapping(value={"/updateDict"})
    public R updateDict(@RequestHeader String userCode,
                        @RequestHeader Long projectId,
                            @RequestBody DwDictRequest request){
        log.info("DwDictController-->updateDict 修改数据字典");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改数据字典时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwDictService.updateDict(request,userCode);
    }

    //检验字段指定的字段与值是否重复
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"数据字典 name ",dwDictService);
        return Result.ok(t);
    }
    //删除分类
    @ApiOperation(value="删除数据字典", notes="删除数据字典接口")
    @DeleteMapping(value={"/deleteDict/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除数据字典ID", required = true, dataType = "Long")
    })
    public R deleteDict(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwDictController-->deleteDict 删除数据字典时ID为{}信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看数据字典时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwDictService.deleteDict(id,userCode)+" 条数据");
    }

    //删除分类
    @ApiOperation(value="删除数据字典关联字段", notes="删除数据字典关联字段接口")
    @DeleteMapping(value={"/deleteDictField/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除数据字典关联字段ID", required = true, dataType = "Long")
    })
    public R deleteDictField(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwDictController-->deleteDict 删除数据字典关联字段时ID为{}信息",id);

        if (!Optional.fromNullable(id).isPresent()){
            return Result.fail("修改数据字典关联字段时ID不能为空");
        }

        return Result.ok("删除成功了："+ this.dwDictService.deleteDictField(id,userCode)+" 条数据");
    }

    /**
     *  下载导入数据字典模板
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value="下载数据字典导入模板", notes="下载数据字典导入模板接口")
    @GetMapping(value = "/download/import/template")
    public R downloadImportTemplate(HttpServletRequest request, HttpServletResponse response){
        if(log.isDebugEnabled()){
            log.debug("DwDictController-->downloadImportTemplate 下载导入数据字典模板");
        }
        String fileName = request.getParameter("fileName");
        fileName= StringUtils.isEmpty(fileName)?"数据字典模板":fileName;
        // 文件下载
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:template/data/dw_dict_template.xlsx");
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
            log.error("下载导入数据字典模板失败");
        }

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        return Result.ok("下载导入数据字典模板操作成功");
    }


    @ApiOperation(value="导出数据字典数据", notes="导出数据字典数据接口")
    @GetMapping(value = "/download/dataDict")
    public R downloadData(HttpServletResponse response,String fileName) {
        if (log.isDebugEnabled()) {
            log.debug("DwDictController-->downloadData 导出数据字典数据");
        }
        try {
            fileName =StringUtils.isEmpty(fileName)?"数据字典_" + DateFormatUtils.format(new Date(),"yyyyMMddHH"):fileName;
            fileName = URLEncoder.encode(fileName, "utf-8");
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment; filename=" +fileName +".xls");
            ExcelWriter writer = ExcelUtil.getWriter();
            List<DwDictExcel> list = this.dwDictService.selectDictList();

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
    @ApiOperation(value="excel模板导入数据字典数据", notes="excel模板导入数据字典数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "模型文件", required = true, dataType = "MultipartFile"),
            @ApiImplicitParam(name = "userCode", value = "用户编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryId", value = "数据字典分类ID", required = true, dataType = "Long")
    })
    @PostMapping(value = "/upload/excel")
    public R uploadExcel(@RequestParam MultipartFile uploadFile,
                         @RequestHeader String userCode,
                         @RequestHeader Long projectId,
                         Long categoryId){
        if(log.isInfoEnabled()) {
            log.info("DwDictController-->uploadExcel excel模板导入数据字典数据");
        }

        if(!Optional.fromNullable(categoryId).isPresent()){
            return Result.fail("上传数据字典数据时分类不能为空");
        }
        String processCode = IdWorker.getId()+"";
        String newFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx" ;
        String fileName = "/tmp" + File.separatorChar+"dnt" +File.separatorChar +"uploadFile"+ File.separatorChar  +DateFormatUtils.format(new Date(), "yyyyMMdd") +File.separatorChar+"dict"+File.separatorChar+ newFileName;
        File uFile = new File(fileName);
        try {
            createFileMkdirs(uFile);
            uploadFile.transferTo(uFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.dwDictService.uploadExcel(processCode,uFile,userCode,projectId,categoryId);

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
            log.info("DwDictController-->getImportProgress 获取导入文件进度");
        }
        if (StringUtils.isEmpty(request.getProcessCode())) {
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","操作进度的批次号不能为空");
            return Result.ok(mm);
        }

        return this.dwDictService.getImportProgress(request.getProcessCode());

    }

    //模型物理化操作
    @ApiOperation(value="数据字典物理化操作", notes="数据字典物理化操作接口")
    @PostMapping(value={"/doDictPhysics"})
    public R doDictPhysics(@RequestHeader String userCode,
                            @RequestBody DwDictPhysicsRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwDictController-->doDictPhysics 模型物理化操作");
        }
        //计算物理化耗时
        Stopwatch stopwatch = Stopwatch.createStarted();
        Long lotNumber = IdWorker.getId();
        //物理化操作
        //R r = this.dwMouldService.doMouldPhysics(lotNumber,userCode,request);
        //构建日志信息
        DwOperationLog gg = new DwOperationLog();
        gg.setLotNumber(lotNumber);
        gg.setPlatform("数仓");
        gg.setTitle("数据字典物理化");
        gg.setClassName("DwDictController");
        gg.setMethodName("doDictPhysics");
        gg.setType("INFO");
        gg.setMouldFlag(3);
        gg.setKeyId(request.getId()); // 模型ID
        gg.setProtocol("http");
        gg.setProtocolVersion("v1");
        gg.setRequestUrl("/v1/dw/dict/doDictPhysics");
        gg.setResultDesc("物理化操作成功");
        gg.setStatus(1);
        gg.setLogOrder(66);
        long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
        gg.setTimeConsuming(elapsed); //耗时
        gg.setOperationTime(new Date());
        gg.setCreateUser(userCode);
        //异步记录操作日志
        this.dwMouldService.writeOperationLog(gg);

        return Result.ok("数据字典物理化操作成功");
    }

}
