package com.dnt.data.standard.server.model.resource.controller;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.service.DwMouldService;
import com.dnt.data.standard.server.model.resource.entity.request.*;
import com.dnt.data.standard.server.model.resource.service.DwMouldResourceService;
import com.dnt.data.standard.server.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description: 模型资源--业务代码 <br>
 * @date: 2021/10/12 上午10:00 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/resource")
@Api(value = "dwMouldResource", tags = "模型资源接口")
@Slf4j
public class DwMouldResourceController extends BaseController {

    @Autowired
    private DwMouldResourceService dwMouldResourceService;
    @Autowired
    private DwMouldService dwMouldService;

    /**
     * 获取资源分页列表
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="获取资源分页列表", notes="获取资源分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwMouldResourceRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->pageList 获取资源分页列表");
        }
        request.setProjectId(projectId);
        return Result.ok(dwMouldResourceService.selectMouldResourcePage(request));
    }

    /**
     * 资源上线与下线操作
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="资源上线与下线操作", notes="资源上线与下线操作接口")
    @PostMapping(value={"/updateResourceStatus"})
    public R updateResourceStatus(@RequestHeader Long projectId,
                                  @RequestBody DwMouldResourceRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->updateResourceStatus 资源上线与下线操作");
        }
        request.setProjectId(projectId);
        return dwMouldResourceService.updateResourceStatus(request);
    }


    /**
     * 删除模型资源
     * @param userCode
     * @param request
     * @return
     */
    @ApiOperation(value="删除模型资源", notes="删除模型资源")
    @PostMapping(value={"/deleteMouldResource"})
    public R deleteDict(@RequestHeader String userCode,@RequestBody DwMouldResourceRequest request){
        if(log.isInfoEnabled()){
            log.info("DwMouldResourceController-->deleteMouldResource 删除模型资源");
        }

        if(CollectionUtils.isEmpty(request.getIds())){
            return Result.fail("删除模型资源时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwMouldResourceService.deleteMouldResource(request.getIds(),userCode)+" 条数据");
    }

    @ApiOperation(value="模型资源发布数据源类型下拉列表", notes="模型资源发布数据源类型下拉列表")
    @GetMapping(value={"/selectSourceTypeItem"})
    public R selectSourceTypeItem(){
        log.info("DwMouldResourceController-->selectSourceTypeItem 模型资源发布数据源类型下拉列表");

        JSONArray t = this.dwMouldResourceService.selectSourceTypeItem();
        return Result.ok(t);
    }

    @ApiOperation(value="模型资源发布数据源名称下拉列表", notes="模型资源发布数据源名称下拉列表")
    @GetMapping(value={"/selectSourceItem/{typeId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "数据源类型ID", required = true, dataType = "Long")
    })
    public R selectSourceItem(@PathVariable("typeId") Long typeId){
        log.info("DwMouldResourceController-->selectSourceItem 模型资源发布数据源名称下拉列表");

        JSONArray t = this.dwMouldResourceService.selectSourceItem(typeId);
        return Result.ok(t);
    }

    @ApiOperation(value="数据库下拉列表", notes="数据库下拉列表")
    @GetMapping(value={"/selectDbItem/{projectId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "Long")
    })
    public R selectDbItem(@PathVariable("projectId") Long projectId){
        log.info("DwMouldResourceController-->selectDbItem 数据库下拉列表");

        List<Map<String,Object>> t = this.dwMouldResourceService.selectDbItem(projectId);
        return Result.ok(t);
    }

    /**
     * 数据库表下拉列表
     * @param dbId
     * @return
     */
    @ApiOperation(value="数据库表下拉列表", notes="数据库表下拉列表")
    @GetMapping(value={"/selectTableItem/{dbId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dbId", value = "数据库ID", required = true, dataType = "Long")
    })
    public R selectTableItem(@PathVariable("dbId") Long dbId){
        log.info("DwMouldResourceController-->selectTableItem 数据库表下拉列表");

        List<Map<String,Object>> t = this.dwMouldResourceService.selectTableItem(dbId);
        return Result.ok(t);
    }

    /**
     * 数据库表下拉列表
     * @param tableId
     * @return
     */
    @ApiOperation(value="数据库下的字段下拉列表", notes="数据库下的字段下拉列表接口")
    @GetMapping(value={"/selectTableFieldItem/{tableId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableId", value = "表ID", required = true, dataType = "Long")
    })
    public R selectTableFieldItem(@PathVariable("tableId") Long tableId){
        log.info("DwMouldResourceController-->selectTableFieldItem 数据库下的字段下拉列表");

        List<Map<String,Object>> t = this.dwMouldResourceService.selectTableFieldItem(tableId);
        return Result.ok(t);
    }


    /**
     * 模型资源发布操作
     * @param userCode
     * @param request
     * @return
     */
    @ApiOperation(value="模型资源发布操作", notes="模型资源发布操作接口")
    @PostMapping(value={"/doResourceRelease"})
    public R doResourceRelease(@RequestHeader String userCode,
                               @RequestHeader Long projectId,
                               @RequestBody DwMouldResourceReleaseRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->doResourceRelease 模型资源发布操作 ");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.doResourceRelease(userCode, request);
    }

    /**
     * 模型资源发布操作
     * @return
     */
    @ApiOperation(value="模型资源统计汇总信息", notes="模型资源统计汇总信息接口")
    @PostMapping(value={"/getResourceStatistic"})
    public R getResourceStatistic(@RequestHeader Long projectId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getResourceStatistic 模型资源统计汇总信息接口 ");
        }

        return this.dwMouldResourceService.getResourceStatistic(projectId);
    }


    /**
     * 资产盘点查询已有资源的数据类型
     * @return
     */
    @ApiOperation(value="资产盘点查询已有资源的数据类型", notes="资产盘点查询已有资源的数据类型接口")
    @PostMapping(value={"/getHaveDataDataSourceType"})
    public R getHaveDataDataSourceType(@RequestHeader Long projectId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getHaveDataDataSourceType 资产盘点查询已有资源的数据类型接口");
        }

        return this.dwMouldResourceService.getHaveDataDataSourceType(projectId);
    }

    /**
     * 资产盘点已接入数据源数据预览
     * @return
     */
    @ApiOperation(value="资产盘点已接入数据源数据预览", notes="资产盘点已接入数据源数据预览接口")
    @PostMapping(value={"/getDataPreview"})
    public R getDataPreview(@RequestHeader Long projectId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getDataPreview 资产盘点已接入数据源数据预览 ");
        }

        return this.dwMouldResourceService.getDataPreview(projectId);
    }


    /**
     * 资产盘点数据地图分布
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点数据地图分布", notes="资产盘点数据地图分布接口")
    @PostMapping(value={"/getDataDistribution"})
    public R getDataDistribution(@RequestHeader Long projectId,
                                 @RequestBody DataDistributionRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getDataPreview 资产盘点数据地图分布 ");
        }
        Long stId = request.getDataSourceType();
        if(!Optional.ofNullable(stId).isPresent()){
            return Result.fail("数据源类型不能为空");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.getDataDistribution(request);
    }

    /**
     * 资产盘点数据源分布
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点数据资源分布", notes="资产盘点数据资源分布接口")
    @PostMapping(value={"/getCategoryStatisticInfo"})
    public R getCategoryStatisticInfo(@RequestHeader Long projectId,
                                      @RequestBody DataDistributionRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getCategoryStatisticInfo 资产盘点数据资源分布");
        }

        request.setProjectId(projectId);
        return this.dwMouldResourceService.getCategoryStatisticInfo(request);
    }

    /**
     * 资产盘点数据价值排行
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点数据价值排行", notes="资产盘点数据价值排行接口")
    @PostMapping(value={"/getDataValueRank"})
    public R getDataValueRank(@RequestHeader Long projectId,
                              @RequestBody DataValueRankRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getDataValueRank 资产盘点数据价值排行  ");
        }
        if(!Optional.ofNullable(request.getDataSourceType()).isPresent() ){
            return Result.fail("查询数据价值排行时数据源类型不能为空");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.getDataValueRank(request);
    }

    /**
     * 资产盘点数据库Top10
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点数据库Top10", notes="资产盘点数据库Top10接口")
        @PostMapping(value={"/getTop10Databases"})
    public R getTop10Databases(@RequestHeader Long projectId,
                               @RequestBody DataValueRankRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getTop10Databases 资产盘点数据库Top10");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.getTop10Databases(request);
    }

    /**
     * 资产盘点数据表Top10
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点数据表Top10", notes="资产盘点数据表Top10接口")
    @PostMapping(value={"/getTop10Tables"})
    public R getTop10Tables(@RequestHeader Long projectId,
                            @RequestBody DataValueRankRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getTop10Tables 资产盘点数据表Top10  ");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.getTop10Tables(request);
    }

    /**
     * 资产盘点元数据变化趋势
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点元数据变化趋势", notes="资产盘点元数据变化趋势接口")
    @PostMapping(value={"/getDataIncrementTrend"})
    public R getDataIncrementTrend(@RequestHeader Long projectId,
                                   @RequestBody DataDistributionRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getDataIncrementTrend 资产盘点元数据变化趋势  ");
        }
        Long dataSourceType = request.getDataSourceType();
        if(!Optional.ofNullable(dataSourceType).isPresent()){

            return Result.fail("数据源类型不能为空");
        }
        Integer interval  =request.getInterval();
        if(!Optional.ofNullable(interval).isPresent()){
            request.setInterval(1);
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.getDataIncrementTrend(request);
    }

    /**
     * 资产盘点资产查询趋势
     * @param request
     * @return
     */
    @ApiOperation(value="资产盘点元资产查询趋势", notes="资产盘点元资产查询趋势接口")
    @PostMapping(value={"/getSearchTrend"})
    public R getSearchTrend(@RequestHeader Long projectId,
                            @RequestBody DataDistributionRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->getSearchTrend 资产盘点元资产查询趋势 ");
        }
        if(!Optional.ofNullable(request.getSearchType()).isPresent()){
            return Result.fail("资产查询趋势的搜索类型不能为空");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.getSearchTrend(request);
    }

    @ApiOperation(value="联想输入", notes="根据输入的内容联想出相关的内容接口")
    @PostMapping(value={"/inputTips"})
    public R inputTips(@RequestHeader Long projectId,@RequestBody OperatorRequest request){
        if(log.isInfoEnabled()) {
            log.info("DataMapController-->searchDB 搜索库列表");
        }
        String searchContent = request.getSearchContent();
        if(StringUtils.isEmpty(searchContent)){
            return Result.fail("检索内容不能为空");
        }
        return Result.ok(dwMouldResourceService.inputTips(projectId,searchContent));
    }

    /**
     * 查看搜索操作记录
     * @param userCode
     * @param request
     * @return
     */
    @ApiOperation(value="查看/搜索操作记录", notes="查看/搜索操作记录接口")
    @PostMapping(value={"/doOperator"})
    public R doOperator(@RequestHeader String userCode,
                        @RequestHeader Long projectId,@RequestBody OperatorRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceController-->doOperator 查看/搜索操作记录 ");
        }
        Long rid = request.getResourceId();
        if(!Optional.ofNullable(rid).isPresent()){
            return Result.fail("资源ID不能为空");
        }
        request.setProjectId(projectId);
        return this.dwMouldResourceService.doOperator(userCode,request);
    }
}
