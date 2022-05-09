package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldNameAttributeRequest;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwMouldNameAttribute;
import com.dnt.data.standard.server.model.mould.service.DwMouldNameAttributeService;
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
 * @description: 模型命名属性--业务代码 <br>
 * @date: 2021/7/27 下午12:11 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/mould/name/attribute")
@Api(value = "dwMouldNameAttribute", tags = "模型命名属性接口")
@Slf4j
public class DwMouldNameAttributeController extends BaseController {

    @Autowired
    private DwMouldNameAttributeService dwMouldNameAttributeService;


    //获取数模型命名属性分页列表
    @ApiOperation(value="获取模型命名属性分页列表", notes="获取模型命名属性分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwMouldNameAttributeRequest request){
        log.info("DwMouldNameAttributeController-->pageList 获取模型命名属性分页列表");
        if(!Optional.fromNullable(request.getType()).isPresent()){
            return Result.fail("查询数据时类型不能为空");
        }
        request.setProjectId(projectId);
        return Result.ok(dwMouldNameAttributeService.selectMouldNameAttributePage(request));
    }


    /**查看详情**/
    @ApiOperation(value="查看模型命名属性详情", notes="查看模型命名属性情接口")
    @GetMapping(value={"/detailMouldNameAttribute/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型命名属性ID", required = true, dataType = "Long")
    })
    public R detailMouldNameAttribute(@PathVariable("id") Long id){
        log.info("DwMouldNameAttributeController--> detailMouldNameAttribute 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看模型命名属性信息时ID不能为空");
        }
        DwMouldNameAttribute t = this.dwMouldNameAttributeService.detailMouldNameAttribute(id);
        return Result.ok(t);
    }


    /**添加模型命名属性**/
    @ApiOperation(value="添加模型命名属性", notes="添加模型命名属性接口")
    @PostMapping(value={"/saveMouldNameAttribute"})
    public R saveMouldNameAttribute(@RequestHeader String userCode,
                        @RequestHeader Long projectId,
                        @RequestBody DwMouldNameAttributeRequest request){
        log.info("DwMouldNameAttributeController-->saveMouldNameAttribute 添加模型命名属性 ");
        request.setProjectId(projectId);
        return  this.dwMouldNameAttributeService.saveMouldNameAttribute(request,userCode);
    }


    /**修改模型命名属性**/
    @ApiOperation(value="修改模型命名属性", notes="修改模型命名属性接口")
    @PutMapping(value={"/updateMouldNameAttribute"})
    public R updateMouldNameAttribute(@RequestHeader String userCode,
                                      @RequestHeader Long projectId,
                          @RequestBody DwMouldNameAttributeRequest request){
        log.info("DwMouldNameAttributeController-->updateMouldNameAttribute 修改模型命名属性");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型命名属性时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwMouldNameAttributeService.updateMouldNameAttribute(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"模型命名属性 name ",dwMouldNameAttributeService);
        return Result.ok(t);
    }

    /**删除模型命名属性**/
    @ApiOperation(value="删除模型命名属性", notes="删除模型命名属性接口")
    @DeleteMapping(value={"/deleteMouldNameAttribute/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除模型命名属性ID", required = true, dataType = "Long")
    })
    public R deleteMouldNameAttribute(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwMouldNameAttributeController-->MouldNameAttribute 删除模型命名属性时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除模型命名属性信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwMouldNameAttributeService.deleteMouldNameAttribute(id,userCode)+" 条数据");
    }


}
