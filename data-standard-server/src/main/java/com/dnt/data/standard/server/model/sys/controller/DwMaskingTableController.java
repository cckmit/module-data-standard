package com.dnt.data.standard.server.model.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingTable;
import com.dnt.data.standard.server.model.sys.entity.request.DwMaskingTableRequest;
import com.dnt.data.standard.server.model.sys.service.DwMaskingTableService;
import com.dnt.data.standard.server.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @description: 数据脱敏规则表--业务代码 <br>
 * @date: 2021/11/1 下午1:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/ruleTable")
@Api(value = "ruleTable", tags = "数据脱敏规则表接口")
@Slf4j
public class DwMaskingTableController extends BaseController {
    @Autowired
    private DwMaskingTableService dwMaskingTableService;

    /**
     * 添加API数据脱敏表
     * @param userCode
     * @param entity
     * @return
     */
    @ApiOperation(value="添加数据脱敏表", notes="添加数据脱敏表")
    @PostMapping(value={"/addMaskingTable"})
    public R addMaskingTable(@RequestHeader String userCode, @RequestBody DwMaskingTable entity){
        log.info("ApiDataMaskingTableController-->addMaskingTable 添加数据脱敏规则入参 ApiDataMaskingTable："+entity);
        return  this.dwMaskingTableService.addMaskingTable(userCode,entity);
    }

    /**
     * 编辑脱敏表信息
     * @param userCode
     * @param entity
     * @return
     */
    @ApiOperation(value="编辑脱敏表信息", notes="编辑脱敏表信息")
    @PutMapping(value={"/editMaskingTable"})
    public R editMaskingTable(@RequestHeader String userCode, @RequestBody DwMaskingTable entity){
        log.info("ApiDataMaskingTableController-->editMaskingTable编辑脱敏表信息"+entity);
        return  this.dwMaskingTableService.editMaskingTable(entity,userCode);
    }

    @ApiOperation(value = "删除脱敏表信息", notes = "删除脱敏表信息")
    @DeleteMapping(value = {"/deleteMaskingTable/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", required = true,paramType="path", dataType = "Long")
    })
    public R deleteMaskingTable(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("ApiDataMaskingTableController-->deleteMaskingTable删除脱敏表信息id={}的信息",id);
        return  this.dwMaskingTableService.deleteMaskingTable(userCode,id);
    }

    /**
     * 分页获取脱敏信息列表
     * @param request
     * @return
     */
    @ApiOperation(value="分页获取脱敏信息列表", notes="分页获取脱敏信息列表")
    @PostMapping (value={"/maskingTable/list"})
    public R maskingTableList(@RequestHeader("projectId") Long projectId,
                              @RequestBody DwMaskingTableRequest request){
        request.setProjectId(projectId);
        Page<DwMaskingTable> page = new Page<>(request.getPageNum(),request.getPageSize());
        log.info("ApiDataMaskingTableController-->maskingTableList 参数为："+request);
        Long ruleId = request.getMaskingRuleId();
        if(!Optional.ofNullable(ruleId).isPresent()){
            return Result.fail("脱敏规则ID不能为空");
        }
        //分页数据
        IPage<DwMaskingTable> data=this.dwMaskingTableService.maskingTableList(request, page);
        return Result.ok(data);
    }


    /**
     * 批量开启/关闭血缘启用状态
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="批量开启/关闭血缘启用状态", notes="批量开启/关闭血缘启用状态接口")
    @PostMapping(value={"/updateBloodRuleStatus"})
    public R updateBloodRuleStatus(@RequestHeader String userCode,
                                   @RequestHeader Long projectId,
                                   @RequestBody DwMaskingTableRequest request){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->updateBloodRuleStatus 更新资产全景状态");
        }
        request.setProjectId(projectId);
        List<Long> ids = request.getIds();
        if(CollectionUtils.isEmpty(ids)){
            return Result.fail("批量开启/关闭血缘启用状态时，ID不能为空");
        }
        Integer status = request.getIsBloodRuleStatus();
        if(!Optional.ofNullable(status).isPresent()){
            return Result.fail("更新状态时，状态值不能为空");
        }

        return this.dwMaskingTableService.updateBloodRuleStatus(userCode,request);
    }
}
