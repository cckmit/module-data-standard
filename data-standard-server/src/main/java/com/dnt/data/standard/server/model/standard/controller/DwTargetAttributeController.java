package com.dnt.data.standard.server.model.standard.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.standard.entity.DwTargetAttribute;
import com.dnt.data.standard.server.model.standard.entity.request.DwTargetAttributeRequest;
import com.dnt.data.standard.server.model.standard.service.DwTargetAttributeService;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 指标属性--业务代码 <br>
 * @date: 2021/7/15 上午11:21 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/target/attribute")
@Api(value = "dwTargetAttribute", tags = "指标属性接口")
@Slf4j
public class DwTargetAttributeController extends BaseController {

    @Autowired
    private DwTargetAttributeService dwTargetAttributeService;

    /**获数指标属性分页列表**/
    @ApiOperation(value="获取指标属性分页列表", notes="获取指标属性分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwTargetAttributeRequest request){
        log.info("DwTargetAttributeController-->pageList 获取指标属性分页列表");
        if(!Optional.fromNullable(request.getType()).isPresent()){
            request.setType(1);
        }
        request.setProjectId(projectId);
        return Result.ok(dwTargetAttributeService.selectTargetAttributePage(request));
    }


    /**查看详情**/
    @ApiOperation(value="查看指标属性详情", notes="查看指标属性情接口")
    @GetMapping(value={"/detailTargetAttribute/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "指标属性ID", required = true, dataType = "Long")
    })
    public R detailTargetAttribute(@PathVariable("id") Long id){
        log.info("DwTargetAttributeController--> detailTargetAttribute 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看指标属性信息时ID不能为空");
        }
        DwTargetAttribute t = this.dwTargetAttributeService.detailTargetAttribute(id);
        return Result.ok(t);
    }

    /**添加指标属性**/
    @ApiOperation(value="添加指标属性", notes="添加指标属性接口")
    @PostMapping(value={"/saveTargetAttribute"})
    public R saveTargetAttribute(@RequestHeader String userCode,
                                 @RequestHeader Long projectId,
                      @RequestBody DwTargetAttributeRequest request){
        log.info("DwTargetAttributeController-->saveTargetAttribute 添加指标属性 ");
        request.setProjectId(projectId);
        return  this.dwTargetAttributeService.saveTargetAttribute(request,userCode);
    }


    /**修改指标属性**/
    @ApiOperation(value="修改指标属性", notes="修改指标属性接口")
    @PutMapping(value={"/updateTargetAttribute"})
    public R updateTargetAttribute(@RequestHeader String userCode,
                                   @RequestHeader Long projectId,
                        @RequestBody DwTargetAttributeRequest request){
        log.info("DwTargetAttributeController-->updateTargetAttribute 修改指标属性");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改指标属性时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwTargetAttributeService.updateTargetAttribute(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"指标属性 name ",dwTargetAttributeService);
        return Result.ok(t);
    }

    /**删除指标属性**/
    @ApiOperation(value="删除指标属性", notes="删除指标属性接口")
    @DeleteMapping(value={"/deleteTargetAttribute/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除指标属性ID", required = true, dataType = "Long")
    })
    public R deleteTargetAttribute(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwTargetAttributeController-->TargetAttribute 删除指标属性时ID为{}信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除指标属性信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwTargetAttributeService.deleteTargetAttribute(id,userCode)+" 条数据");
    }





}
