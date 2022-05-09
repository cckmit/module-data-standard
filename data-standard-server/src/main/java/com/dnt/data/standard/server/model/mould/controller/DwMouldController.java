package com.dnt.data.standard.server.model.mould.controller;

import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldPhysicsRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwMouldResponse;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.entity.ExcelProgressRequest;
import com.dnt.data.standard.server.model.mould.entity.DwOperationLog;
import com.dnt.data.standard.server.model.mould.service.DwMouldService;
import com.dnt.data.standard.server.utils.FileUtils;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 模型管理--业务代码 <br>
 * @date: 2021/8/4 下午4:56 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/mould")
@Api(value = "dwMould", tags = "模型管理接口")
@Slf4j
public class DwMouldController extends BaseController {
    @Autowired
    private DwMouldService dwMouldService;

    /**
     * 获取模型分页列表
     * @param projectId 项目ID
     * @param request 入参数对象
     * @return
     */
    @ApiOperation(value="获取模型分页列表", notes="获取模型分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwMouldRequest request){
        log.info("DwMouldController-->pageList 获取模型分页列表");
        request.setProjectId(projectId);
        return Result.ok(this.dwMouldService.selectDwMouldPage(request));
    }

    /**
     * 检验字段指定的字段与值是否重复
     * @param value
     * @param oldValue
     * @param property
     * @param mouldCategoryId
     * @return
     */
    @ApiOperation(value="检验字段值是否重复", notes="检验字段值是否重复接口")
    @GetMapping(value={"/remoteCheck"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String"),
            @ApiImplicitParam(name = "mouldCategoryId", value = "分类ID", required = false, dataType = "Long")
    })
    public R remoteCheck(@RequestParam("value") String value,
                         @RequestParam("oldValue")   String oldValue,
                         @RequestParam("property")   String property,
                         @RequestParam("mouldCategoryId") Long mouldCategoryId){
        if(log.isInfoEnabled()){
            log.info("DwMouldController-->remoteCheck 检验字段值是否重复");
        }
        if(StringUtils.isEmpty(property)){
            return Result.fail("检查字段信息不能为空");
        }
        if(StringUtils.isEmpty(value)){
            return Result.fail("检查字段的内容信息不能为空");
        }

        boolean t = remoteCheck(property,oldValue,value,mouldCategoryId,"模型 name ",dwMouldService);
        return Result.ok(t);
    }

    /**
     * 选择公共字段集的字段信息接口
     * @param request
     * @return
     */
    @ApiOperation(value="选择公共字段集的字段信息", notes="选择公共字段集的字段信息接口")
    @PostMapping(value={"/selectPublicMouldField"})
    public R selectPublicMouldField(@RequestBody DwMouldRequest request){
        log.info("DwMouldController-->selectPublicMouldField 选择公共字段集的字段信息");

        if(CollectionUtils.isEmpty(request.getPublicMouldIds())){
            return Result.fail("请选择公共字段集信息");
        }
        return Result.ok(this.dwMouldService.selectPublicMouldField(request.getPublicMouldIds()));
    }


    /**
     * 删除分类
     * @param userCode
     * @param id
     * @return
     */
    @ApiOperation(value="删除模型接口", notes="删除模型接口")
    @DeleteMapping(value={"/deleteMould/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除模型ID", required = true, dataType = "Long")
    })
    public R deleteMould(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwMouldController-->deleteMould 删除模型时ID为{}信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看模型时ID不能为空");
        }
        return this.dwMouldService.deleteMould(id,userCode);
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看模型详情接口", notes="查看模型详情接口")
    @GetMapping(value={"/detailMould/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型ID", required = true, dataType = "Long")
    })
    public R detailMould(@PathVariable("id") Long id){
        log.info("DwMouldController-->detailMould 查询id为{}的模型详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看模型信息时ID不能为空");
        }
        DwMouldResponse t = this.dwMouldService.detailMould(id);
        return Result.ok(t);
    }

    /**
     * 模型发布操作
     * @param userCode
     * @param id
     * @param mouldStatus
     * @return
     */
    @ApiOperation(value="模型发布操作", notes="模型发布操作接口")
    @GetMapping(value={"/doMouldRelease/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型ID", required = true, dataType = "Long")
    })
    public R doMouldRelease(@RequestHeader String userCode,
                            @PathVariable("id") Long id,
                            Integer mouldStatus){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->doMouldRelease 模型发布操作");
        }
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("发布模型信息时ID不能为空");
        }
        return this.dwMouldService.doMouldRelease(id,mouldStatus,userCode);
    }


    /**
     *  下载导入模型模板
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value="下载导入模型模板", notes="下载导入模型模板接口")
    @GetMapping(value = "/download/import/template")
    public R downloadImportTemplate(HttpServletRequest request, HttpServletResponse response){
        if(log.isDebugEnabled()){
            log.debug("DwMouldController-->downloadImportTemplate 下载导入模型模板");
        }
        String fileName = request.getParameter("fileName");
        fileName= StringUtils.isEmpty(fileName)?"模型模板":fileName;
        // 文件下载
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource resource = resolver.getResource("classpath:template/data/dw_mould_template.xlsx");
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
            log.error("下载导入模型模板失败");
        }

        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        return Result.ok("下载导入模型模板操作成功");
    }

    @PostMapping(value = "/upload/big/file")
    public R uploadBigFile(@RequestParam MultipartFile uploadFile,
                         @RequestHeader String userCode){

        dwMouldService.uploadBigFile(uploadFile,userCode);
        return Result.ok("操作成功了！");

    }

    /**
     * 文件批量导入操作
     * @param uploadFile
     * @param categoryId
     * @return
     */
    @ApiOperation(value="excel模板导入数据元数据", notes="excel模板导入数据元数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uploadFile", value = "模型文件", required = true, dataType = "__File",allowMultiple = true),
            @ApiImplicitParam(name = "userCode", value = "用户编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryId", value = "模型分类ID", required = true, dataType = "Long")
    })
    @PostMapping(value = "/upload/excel")
    public R uploadExcel(@RequestParam MultipartFile uploadFile,
                         @RequestHeader String userCode,
                         @RequestHeader Long projectId,
                         Long categoryId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->uploadExcel excel模板导入数据元数据");
        }


        if(!Optional.fromNullable(categoryId).isPresent()){
            return Result.fail("上传模型数据时分类不能为空");
        }
        String processCode = IdWorker.getId()+"";
        String newFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + ".xlsx" ;
        String fileName = "/tmp" + File.separatorChar+"dnt" +File.separatorChar +"uploadFile"+ File.separatorChar  +DateFormatUtils.format(new Date(), "yyyyMMdd") +File.separatorChar+"mould"+File.separatorChar+ newFileName;
        File uFile = new File(fileName);
        try {
            createFileMkdirs(uFile);
            uploadFile.transferTo(uFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.dwMouldService.uploadExcel(processCode,uFile,userCode,projectId,categoryId);

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
            log.info("DwMouldController-->getImportProgress 获取导入文件进度");
        }
        if (StringUtils.isEmpty(request.getProcessCode())) {
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","操作进度的批次号不能为空");
            return Result.ok(mm);
        }

        return this.dwMouldService.getImportProgress(request.getProcessCode());

    }

    /**
     * 添加手工新建模型
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="添加手工新建模型", notes="添加手工新建模型接口")
    @PostMapping(value={"/saveMould"})
    public R saveMould(@RequestHeader String userCode,
                       @RequestHeader Long projectId,
                          @RequestBody DwMouldRequest request){
        log.info("DwMouldController-->saveMould 添加手工新建模型 ");
        request.setProjectId(projectId);
        return  this.dwMouldService.saveMould(request,userCode);
    }

    /**
     * 修改手工新建模型
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="修改手工新建模型", notes="修改手工新建模型接口")
    @PutMapping(value={"/updateMould"})
    public R updateMould(@RequestHeader String userCode,
                         @RequestHeader Long projectId,
                            @RequestBody DwMouldRequest request){
        log.info("DwMouldController-->updateMould 修改手工新建模型");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwMouldService.updateMould(request,userCode);
    }


    /**==========================================ddl操作接口=======================================**/

    /**
     * ddl建模数据源类型下拉列表
     * @param envFlag
     * @return
     */
    @ApiOperation(value="ddl建模数据源类型下拉列表", notes="ddl建模数据源类型下拉列表接口")
    @GetMapping(value={"/selectDDLSourceTypeItem/{envFlag}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "envFlag", value = "存储资源", required = true, dataType = "String"),
    })
    public R selectDDLSourceTypeItem(@PathVariable("envFlag") String envFlag){
        log.info("DwMouldController-->selectDDLSourceTypeItem ddl建模数据源类型下拉列表");

        JSONArray t = this.dwMouldService.selectDDLSourceTypeItem(envFlag);
        return Result.ok(t);
    }

    /**
     * 添加ddl建模
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="添加ddl建模", notes="添加ddl建模接口")
    @PostMapping(value={"/saveDDLMould"})
    public R saveDDLMould(@RequestHeader String userCode,
                          @RequestHeader Long projectId,
                                    @RequestBody DwMouldRequest request){
        log.info("DwMouldController-->saveDDLMould 添加模型命名属性 ");
        request.setProjectId(projectId);
        return  this.dwMouldService.saveDDLMould(request,userCode);
    }

    /**
     * 修改ddl建模
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="修改ddl建模", notes="修改ddl建模口")
    @PutMapping(value={"/updateDDLMould"})
    public R updateDDLMould(@RequestHeader String userCode,
                            @RequestHeader Long projectId,
                                      @RequestBody DwMouldRequest request){
        log.info("DwMouldController-->updateDDLMould 修改模型命名属性");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwMouldService.updateDDLMould(request,userCode);
    }

    /**==========================================物理化操作=======================================**/

    /**
     * 查看默认数据源接口
     * @param projectId
     * @return
     */
    @ApiOperation(value="查看默认数据源接口", notes="查看默认数据源接口")
    @GetMapping(value={"/detailDefaultDatasource/{projectId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "Long")
    })
    public R detailDefaultDatasource(@PathVariable("projectId") Long projectId){

        log.info("DwMouldController-->detailDefaultDatasource 查看默认数据源接口");
        if(!Optional.fromNullable(projectId).isPresent()){
            return Result.fail("查看默认数据源时项目ID不能为空");
        }

        return this.dwMouldService.detailDefaultDatasource(projectId);
    }

    /**
     * 查看指定存储资源下的 项目下拉列表
     * @param envFlag
     * @param sourceTypeId
     * @return
     */
    @ApiOperation(value="查看指定存储资源下的 项目下拉列表", notes="查看指定存储资源下的 项目下拉列表接口")
    @GetMapping(value={"/selectProjectItem/{envFlag}/{sourceTypeId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "envFlag", value = "存储资源", required = true, dataType = "String"),
            @ApiImplicitParam(name = "sourceTypeId", value = "数据源类型ID", required = true, dataType = "Long")
    })
    public R selectProjectItem(@PathVariable("envFlag") String envFlag,
                               @PathVariable("sourceTypeId") Long sourceTypeId){

        log.info("DwMouldController-->selectProjectItem 查看指定存储资源下的 项目下拉列表");
        if(StringUtils.isEmpty(envFlag)){
            return Result.fail("请选择存储资源");
        }

        return this.dwMouldService.selectProjectItem(envFlag,sourceTypeId);
    }


    /**
     * 模型物理化操作
     * @param userCode
     * @param request
     * @return
     */
    @ApiOperation(value="模型物理化操作", notes="模型物理化操作接口")
    @PostMapping(value={"/doMouldPhysics"})
    public R doMouldPhysics(@RequestHeader String userCode,
                          @RequestBody DwMouldPhysicsRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->doMouldPhysics 模型物理化操作");
        }
        //计算物理化耗时
        Stopwatch stopwatch = Stopwatch.createStarted();
        Long lotNumber = IdWorker.getId();
        //物理化操作
        R r = this.dwMouldService.doMouldPhysics(lotNumber,userCode,request);
        //构建日志信息
        DwOperationLog gg = new DwOperationLog();
        gg.setLotNumber(lotNumber);
        gg.setPlatform("数仓");
        gg.setTitle("模型物理化");
        gg.setClassName("DwMouldController");
        gg.setMethodName("doMouldPhysics");
        gg.setType("INFO");
        gg.setMouldFlag(3);
        gg.setKeyId(request.getId()); // 模型ID
        gg.setProtocol("http");
        gg.setProtocolVersion("v1");
        gg.setRequestUrl("/v1/dw/mould/doMouldPhysics");
        gg.setResultDesc("物理化操作成功");
        gg.setStatus(1);
        gg.setLogOrder(66);
        long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
        gg.setTimeConsuming(elapsed); //耗时
        gg.setOperationTime(new Date());
        gg.setCreateUser(userCode);
        //异步记录操作日志
        this.dwMouldService.writeOperationLog(gg);

        return r;
    }

    /**
     * 查看模型物理化的DDL语句
     * @param mouldId
     * @return
     */
    @ApiOperation(value="查看模型物理化的DDL语句", notes="查看模型物理化的DDL语句接口")
    @GetMapping(value={"/selectMouldPhysicsDDL/{mouldId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mouldId", value = "模型ID", required = true, dataType = "Long")
    })
    public R selectMouldPhysicsDDL(@PathVariable("mouldId") Long mouldId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldPhysicsDDL 查看模型物理化的DDL语句");
        }

        //查看ddl语句
        return this.dwMouldService.selectMouldPhysicsDDL(mouldId);
    }

    /**
     * 查看模型物理化表列表
     * @param mouldId
     * @return
     */
    @ApiOperation(value="查看模型物理化表列表", notes="查看模型物理化表列表接口")
    @GetMapping(value={"/selectMouldTableStructure/{mouldId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mouldId", value = "模型ID", required = true, dataType = "Long")
    })
    public R selectMouldTableStructure(@PathVariable("mouldId") Long mouldId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldTableStructure 查看模型物理化表数据信息 ");
        }
        if(!Optional.fromNullable(mouldId).isPresent()){
            return Result.fail("查看模型物理化表结构信息时模型ID不能为空");
        }
        //查看模型物理化表数据信息
        return Result.ok(this.dwMouldService.selectMouldTableStructure(mouldId));
    }


    /**
     * 查看模型物理化表字段结构
     * @param mouldId
     * @return
     */
    @ApiOperation(value="查看模型物理化表字段结构", notes="查看模型物理化表字段结构接口")
    @GetMapping(value={"/selectMouldPhysicsStructure/{mouldId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mouldId", value = "模型ID", required = true, dataType = "Long")
    })
    public R selectMouldPhysicsStructure(@PathVariable("mouldId") Long mouldId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldPhysicsStructure  查看模型物理化表结构 ");
        }

        //查看模型物理化表结构
        return this.dwMouldService.selectMouldPhysicsStructure(mouldId);
    }

    /**
     * 查看模型物理化表数据
     * @param physicsId
     * @return
     */
    @ApiOperation(value="查看模型物理化表数据", notes="查看模型物理化表数据接口")
    @GetMapping(value={"/selectMouldPhysicsTable/{physicsId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "physicsId", value = "物理化记录ID", required = true, dataType = "Long")
    })
    public R selectMouldPhysicsTable(@PathVariable("physicsId") Long physicsId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldPhysicsTable 查看模型物理化表数据 ");
        }

        //查看模型物理化表结构
        return this.dwMouldService.selectMouldPhysicsTable(physicsId);
    }

    /**
     * 根据模型查询物理化的批次日志信息
     * @param mouldId
     * @param mouldFlag
     * @return
     */
    @ApiOperation(value="根据模型查询物理化的批次日志信息", notes="根据模型查询物理化的批次日志信息接口")
    @GetMapping(value={"/selectMouldPhysicsLog/{mouldId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mouldId", value = "模型ID", required = true, dataType = "Long")
    })
    public R selectMouldPhysicsLog(@PathVariable("mouldId") Long mouldId,Integer mouldFlag){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldPhysicsLog 根据模型查询物理化的批次日志信息");
        }
        if(!Optional.fromNullable(mouldFlag).isPresent()){
            mouldFlag=1;
        }
        //查询物理化日志信息
        List<DwOperationLog> lists = this.dwMouldService.selectMouldPhysicsLog(mouldId,mouldFlag);
        return Result.ok(lists);
    }

    /**
     * 查询物理化模型每个批次下的日志详情
     * @param lotNumber
     * @param mouldFlag
     * @return
     */
    @ApiOperation(value="查询物理化模型每个批次下的日志详情", notes="查询物理化模型每个批次下的日志详情接口")
    @GetMapping(value={"/selectMouldPhysicsChildLog/{lotNumber}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "lotNumber", value = "物理化日志批次ID", required = true, dataType = "Long")
    })
    public R selectMouldPhysicsChildLog(@PathVariable("lotNumber") Long lotNumber,Integer mouldFlag){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldPhysicsChildLog 查询物理化模型每个批次下的日志详情");
        }
        if(!Optional.fromNullable(mouldFlag).isPresent()){
            mouldFlag=1;
        }
        //查询物理化日志信息
        List<DwOperationLog> lists = this.dwMouldService.selectMouldPhysicsChildLog(lotNumber,mouldFlag);
        return Result.ok(lists);
    }


    /**====================================通用业务属性======================================================**/

    /**
     * 通用业务属性通用下拉列表
     * @return
     */
    @ApiOperation(value="通用业务属性通用下拉列表", notes="通用业务属性通用下拉列表接口")
    @GetMapping(value={"/selectMouldCurrencyAttribute"})
    public R selectMouldCurrencyAttribute(){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldCurrencyAttribute 通用业务属性通用下拉列表");
        }

        //查询物理化日志信息
        Map<String,Object> mapLists = this.dwMouldService.selectMouldCurrencyAttributeItem();
        return Result.ok(mapLists);
    }

    /**
     * 通用业务属性负责人下拉列表
     * @return
     */
    @ApiOperation(value="通用业务属性负责人下拉列表", notes="通用业务属性负责人下拉列表接口")
    @GetMapping(value={"/selectMouldBossheadItem"})
    public R selectMouldBossheadItem(){
        if(log.isInfoEnabled()) {
            log.info("DwMouldController-->selectMouldBossheadItem 通用业务属性负责人下拉列表");
        }

        //查询物理化日志信息
        return Result.ok(this.dwMouldService.selectMouldBossheadItem());
    }


}
