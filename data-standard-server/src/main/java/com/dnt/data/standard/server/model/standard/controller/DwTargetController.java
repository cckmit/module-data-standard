package com.dnt.data.standard.server.model.standard.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.standard.entity.DwTarget;
import com.dnt.data.standard.server.model.standard.entity.request.DwTargetRequest;
import com.dnt.data.standard.server.model.standard.service.DwTargetService;
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

import java.util.List;
import java.util.Map;

/**
 * @description: 指标--业务代码 <br>
 * @date: 2021/7/19 下午3:03 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/target")
@Api(value = "dwTarget", tags = "指标接口")
@Slf4j
public class DwTargetController extends BaseController {
    @Autowired
    private DwTargetService dwTargetService;

    /**
     * 获数指标分页列表
     */

    @ApiOperation(value="获取指标分页列表", notes="获取指标分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwTargetRequest request){
        log.info("DwTargetController-->pageList 获取指标分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwTargetService.selectTargetPage(request));
    }


    /**
     * 查看详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看指标详情", notes="查看指标情接口")
    @GetMapping(value={"/detailTarget/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "指标ID", required = true, dataType = "Long")
    })
    public R detailTarget(@PathVariable("id") Long id){
        log.info("DwTargetController--> detailTarget 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看指标信息时ID不能为空");
        }
        DwTarget t = this.dwTargetService.detailTarget(id);
        return Result.ok(t);
    }

    /**
     * 查询指标周期修饰
     * @return
     */
    @ApiOperation(value="查看指标周期修饰列表", notes="查看指标周期修饰列表接口")
    @GetMapping(value={"/selectAttributeItem"})
    public R selectAttributeItem(@RequestHeader Long projectId){
        log.info("DwDataElementController--> selectAttributeItem 查看时间周期修饰列表");

        Map<String,List<Map<String,Object>>> t = this.dwTargetService.selectAttributeItem(projectId);
        return Result.ok(t);
    }

    /**
     * 查询质量校验函数下拉列表
     * @return
     */
    @ApiOperation(value="查询质量校验函数下拉列表", notes="查询质量校验函数下拉列表接口")
    @GetMapping(value={"/selectFunctionItem"})
    public R selectFunctionItem(@RequestHeader Long projectId){
        log.info("DwDataElementController--> selectFunctionItem 查询质量校验函数下拉列表");

        List<Map<String,Object>> t = this.dwTargetService.selectFunctionItem(projectId);
        return Result.ok(t);
    }

    /**
     * 添加指标
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="添加指标", notes="添加指标接口")
    @PostMapping(value={"/saveTarget"})
    public R saveTarget(@RequestHeader String userCode,
                        @RequestHeader Long projectId,
                                 @RequestBody DwTargetRequest request){
        log.info("DwTargetController-->saveTarget 添加指标 ");
        request.setProjectId(projectId);
        return  this.dwTargetService.saveTarget(request,userCode);
    }


    /**
     * 修改指标
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="修改指标", notes="修改指标接口")
    @PutMapping(value={"/updateTarget"})
    public R updateTarget(@RequestHeader String userCode,
                          @RequestHeader Long projectId,
                                   @RequestBody DwTargetRequest request){
        log.info("DwTargetController-->updateTarget 修改指标");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改指标时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwTargetService.updateTarget(request,userCode);
    }

    /**
     * 检验字段指定的字段与值是否重复
     * @param value
     * @param oldValue
     * @param property
     * @param categoryId
     * @return
     */
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"指标 name ",dwTargetService);
        return Result.ok(t);
    }

    /**
     * 删除指标
     * @param userCode
     * @param id
     * @return
     */
    @ApiOperation(value="删除指标", notes="删除指标接口")
    @DeleteMapping(value={"/deleteTarget/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除指标ID", required = true, dataType = "Long")
    })
    public R deleteTarget(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwTargetController-->Target 删除指标时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除指标信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwTargetService.deleteTarget(id,userCode)+" 条数据");
    }
}
