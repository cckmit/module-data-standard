package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwSource;
import com.dnt.data.standard.server.model.mould.entity.request.DwSourceRequest;
import com.dnt.data.standard.server.model.mould.service.DwSourceService;
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
 * @description: 来源系统--业务代码 <br>
 * @date: 2021/7/28 下午1:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/source")
@Api(value = "dwSource", tags = "来源系统接口")
@Slf4j
public class DwSourceController extends BaseController {
    @Autowired
    private DwSourceService dwSourceService;

    //获取数来源系统分页列表
    @ApiOperation(value="获取来源系统分页列表", notes="获取来源系统分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwSourceRequest request){
        log.info("DwSourceController-->pageList 获取来源系统分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwSourceService.selectSourcePage(request));
    }


    /**查看详情**/
    @ApiOperation(value="查看来源系统详情", notes="查看来源系统情接口")
    @GetMapping(value={"/detailSource/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "来源系统ID", required = true, dataType = "Long")
    })
    public R detailSource(@PathVariable("id") Long id){
        log.info("DwSourceController--> detailSource 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看来源系统信息时ID不能为空");
        }
        DwSource t = this.dwSourceService.detailSource(id);
        return Result.ok(t);
    }


    /**添加来源系统**/
    @ApiOperation(value="添加来源系统", notes="添加来源系统接口")
    @PostMapping(value={"/saveSource"})
    public R saveSource(@RequestHeader String userCode,
                        @RequestHeader Long projectId,
                                    @RequestBody DwSourceRequest request){
        log.info("DwSourceController-->saveSource 添加来源系统 ");
        request.setProjectId(projectId);
        return  this.dwSourceService.saveSource(request,userCode);
    }


    /**修改来源系统**/
    @ApiOperation(value="修改来源系统", notes="修改来源系统接口")
    @PutMapping(value={"/updateSource"})
    public R updateSource(@RequestHeader String userCode,
                          @RequestHeader Long projectId,
                                      @RequestBody DwSourceRequest request){
        log.info("DwSourceController-->updateSource 修改来源系统");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改来源系统时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwSourceService.updateSource(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"来源系统 name ",dwSourceService);
        return Result.ok(t);
    }

    /**删除来源系统**/
    @ApiOperation(value="删除来源系统", notes="删除来源系统接口")
    @DeleteMapping(value={"/deleteSource/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除来源系统ID", required = true, dataType = "Long")
    })
    public R deleteSource(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwSourceController-->Source 删除来源系统时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除来源系统信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwSourceService.deleteSource(id,userCode)+" 条数据");
    }

}
