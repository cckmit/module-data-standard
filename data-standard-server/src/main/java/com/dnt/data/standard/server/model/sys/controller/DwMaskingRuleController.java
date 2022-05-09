package com.dnt.data.standard.server.model.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingRule;
import com.dnt.data.standard.server.model.sys.service.DwMaskingRuleService;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 数据脱敏规则--业务代码  <br>
 * @date: 2021/11/1 下午1:21 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/rule")
@Api(value = "dwRule", tags = "数据脱敏规则接口")
@Slf4j
public class DwMaskingRuleController extends BaseController {
    @Autowired
    private DwMaskingRuleService dwMaskingRuleService;

    /**
     * 添加API数据脱敏规则
     * @param userCode
     * @param entity
     * @return
     */
    @ApiOperation(value="添加数据脱敏规则", notes="添加数据脱敏规则")
    @PostMapping(value={"/addRule"})
    public R addRule(@RequestHeader String userCode, @RequestBody DwMaskingRule entity){
        log.info("ApiMaskingRuleController-->addRule 添加数据脱敏规则入参 ApiMaskingRule："+entity);
        return this.dwMaskingRuleService.addRule(userCode,entity);
    }

    /**
     * 编辑API数据脱敏规则
     * @param userCode
     * @param entity
     * @return
     */
    @ApiOperation(value="编辑数据脱敏规则", notes="编辑数据脱敏规则")
    @PutMapping (value={"/editRule"})
    public R editRule(@RequestHeader String userCode, @RequestBody DwMaskingRule entity){
        log.info("ApiMaskingRuleController-->editRule 编辑数据脱敏规则 ApiMaskingRule："+entity);
        return  this.dwMaskingRuleService.editRule(userCode,entity);
    }

    @ApiOperation(value = "删除数据脱敏规则接口", notes = "删除数据脱敏规则接口")
    @DeleteMapping(value = {"/deleteRule/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "规则ID", required = true,paramType="path", dataType = "Long")
    })
    public R deleteRule(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("ApiMaskingRuleController-->deleteRule 删除数据脱敏规则接口ID为{}的信息",id);
        return  this.dwMaskingRuleService.deleteRule(userCode,id);
    }

    /**
     * 分页获取脱敏规则列表
     * @param entity
     * @return
     */
    @ApiOperation(value="分页获取脱敏规则列表", notes="分页获取脱敏规则列表")
    @PostMapping (value={"/dissentRecord/list"})
    public R maskingRuleList(@RequestHeader("projectId") Long projectId,
                             @RequestBody DwMaskingRule entity){
        Map map =new HashMap();
        if (Optional.fromNullable(entity.getMaskingRuleName()).isPresent()){
            map.put("maskingRuleName",entity.getMaskingRuleName());
        }
        map.put("projectId",projectId);
        if (!Optional.fromNullable(entity.getPageNum()).isPresent() ||!Optional.fromNullable(entity.getPageSize()).isPresent()){
            return  Result.fail("分页所需字段必传！");
        }
        Page<DwMaskingRule> page = new Page<>(entity.getPageNum(),entity.getPageSize());

        log.info("ApiMaskingRuleController-->ApiDissentRecordController.dissentRecordList 参数为："+map);
        //分页数据
        IPage<DwMaskingRule> data=this.dwMaskingRuleService.maskingRuleList(map, page);
        return Result.ok(data);
    }

}
