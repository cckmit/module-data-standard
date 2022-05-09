package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwApplication;
import com.dnt.data.standard.server.model.mould.entity.request.DwApplicationRequest;
import com.dnt.data.standard.server.model.mould.service.DwApplicationService;
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
 * @description: 所属应用--业务代码 <br>
 * @date: 2021/7/28 下午1:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/application")
@Api(value = "dwApplication", tags = "所属应用接口")
@Slf4j
public class DwApplicationController extends BaseController {
    @Autowired
    private DwApplicationService dwApplicationService;

    /**获取所属应用分页列表**/
    @ApiOperation(value="获取所属应用分页列表", notes="获取所属应用分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwApplicationRequest request){
        log.info("DwApplicationController-->pageList 获取所属应用分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwApplicationService.selectApplicationPage(request));
    }

    /**查看详情**/
    @ApiOperation(value="查看所属应用详情", notes="查看所属应用情接口")
    @GetMapping(value={"/detailApplication/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "所属应用ID", required = true, dataType = "Long")
    })
    public R detailApplication(@PathVariable("id") Long id){
        log.info("DwApplicationController--> detailApplication 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看所属应用信息时ID不能为空");
        }
        DwApplication t = this.dwApplicationService.detailApplication(id);
        return Result.ok(t);
    }


    /**添加所属应用**/
    @ApiOperation(value="添加所属应用", notes="添加所属应用接口")
    @PostMapping(value={"/saveApplication"})
    public R saveApplication(@RequestHeader String userCode,
                             @RequestHeader Long projectId,
                                    @RequestBody DwApplicationRequest request){
        log.info("DwApplicationController-->saveApplication 添加所属应用 ");
        request.setProjectId(projectId);
        return  this.dwApplicationService.saveApplication(request,userCode);
    }


    /**修改所属应用**/
    @ApiOperation(value="修改所属应用", notes="修改所属应用接口")
    @PutMapping(value={"/updateApplication"})
    public R updateApplication(@RequestHeader String userCode,
                               @RequestHeader Long projectId,
                                      @RequestBody DwApplicationRequest request){
        log.info("DwApplicationController-->updateApplication 修改所属应用");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改所属应用时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwApplicationService.updateApplication(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"所属应用 name ",dwApplicationService);
        return Result.ok(t);
    }

    /**删除所属应用**/
    @ApiOperation(value="删除所属应用", notes="删除所属应用接口")
    @DeleteMapping(value={"/deleteApplication/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除所属应用ID", required = true, dataType = "Long")
    })
    public R deleteApplication(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwApplicationController-->Application 删除所属应用时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除所属应用信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwApplicationService.deleteApplication(id,userCode)+" 条数据");
    }


}
