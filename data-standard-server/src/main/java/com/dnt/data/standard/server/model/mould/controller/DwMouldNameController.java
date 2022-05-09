package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldNameRequest;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.mould.entity.DwMouldName;
import com.dnt.data.standard.server.model.mould.service.DwMouldNameService;
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
 * @description: 模型命名规则--业务代码 <br>
 * @date: 2021/8/4 下午3:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/mould/name")
@Api(value = "dwMouldName", tags = "模型命名规则接口")
@Slf4j
public class DwMouldNameController extends BaseController {
    @Autowired
    private DwMouldNameService dwMouldNameService;

    /**模型命名规则的目录下拉列表**/
    @ApiOperation(value="模型命名规则的目录下拉列表", notes="模型命名规则的目录下拉列表接口")
    @GetMapping(value={"/selectCatalogueItem"})
    public R selectCatalogueItem(){
        log.info("DwMouldNameController-->selectCatalogueItem 模型命名规则的目录下拉列表");

        List<Map<String,Object>> t = this.dwMouldNameService.selectCatalogueItem();
        return Result.ok(t);
    }
    /**模型命名规则的二级目录下拉列表**/
    @ApiOperation(value="模型命名规则的二级目录下拉列表", notes="模型命名规则的二级目录下拉列表接口")
    @GetMapping(value={"/selectTwoCatalogueItem/{oneCatalogueId}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oneCatalogueId", value = "一级目录ID", required = true, dataType = "Long")
    })
    public R selectTwoCatalogueItem(@PathVariable("oneCatalogueId") Long oneCatalogueId){
        log.info("DwMouldNameController-->selectTwoCatalogueItem 模型命名规则的二级目录下拉列表");

        List<Map<String,Object>> t = this.dwMouldNameService.selectTwoCatalogueItem(oneCatalogueId);
        return Result.ok(t);
    }

    /**模型规则页自定义下拉列表**/
    @ApiOperation(value="模型规则页自定义下拉列表", notes="模型规则页自定义下拉列表接口")
    @GetMapping(value={"/selectCustomMouldNameItem"})
    public R selectCustomMouldNameItem(){
        log.info("DwMouldNameController-->selectCustomMouldNameItem 模型规则页自定义下拉列表");

        List<DwMouldCategory> t = this.dwMouldNameService.selectCustomMouldNameItem();
        return Result.ok(t);
    }

    /**获取模型命名规则分页列表**/
    @ApiOperation(value="获取模型命名规则分页列表", notes="获取模型命名规则分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwMouldNameRequest request){
        log.info("DwMouldNameController-->pageList 获取模型命名规则分页列表");

        if(!Optional.fromNullable(request.getPageNum()).isPresent() || !Optional.fromNullable(request.getPageSize()).isPresent() ){
            return Result.fail("页码[pageNum]与每页展示的记录[pageSize]都不能为空");
        }
        request.setProjectId(projectId);
        return Result.ok(dwMouldNameService.selectMouldNamePage(request));
    }

    /**查看详情**/
    @ApiOperation(value="查看模型命名规则详情", notes="查看模型命名规则接口")
    @GetMapping(value={"/detailMouldName/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型命名规则ID", required = true, dataType = "Long")
    })
    public R detailMouldName(@PathVariable("id") Long id){
        log.info("DwMouldNameController--> detailMouldName 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看模型命名规则信息时ID不能为空");
        }
        DwMouldName t = this.dwMouldNameService.detailMouldName(id);
        return Result.ok(t);
    }


    /**添加模型命名规则**/
    @ApiOperation(value="添加模型命名规则", notes="添加模型命名规则接口")
    @PostMapping(value={"/saveMouldName"})
    public R saveMouldName(@RequestHeader String userCode,
                           @RequestHeader Long projectId,
                                    @RequestBody DwMouldNameRequest request){
        log.info("DwMouldNameController-->saveMouldName 添加模型命名规则 ");
        request.setProjectId(projectId);
        return  this.dwMouldNameService.saveMouldName(request,userCode);
    }


    /**修改模型命名规则**/
    @ApiOperation(value="修改模型命名规则", notes="修改模型命名规则接口")
    @PutMapping(value={"/updateMouldName"})
    public R updateMouldName(@RequestHeader String userCode,
                             @RequestHeader Long projectId,
                                      @RequestBody DwMouldNameRequest request){
        log.info("DwMouldNameController-->updateMouldName 修改模型命名规则");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型命名规则时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwMouldNameService.updateMouldName(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"模型命名规则 name ",dwMouldNameService);
        return Result.ok(t);
    }

    /**删除模型命名规则**/
    @ApiOperation(value="删除模型命名规则", notes="删除模型命名规则接口")
    @DeleteMapping(value={"/deleteMouldName/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除模型命名规则ID", required = true, dataType = "Long")
    })
    public R deleteMouldName(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwMouldNameController-->MouldName 删除模型命名规则时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除模型命名规则信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwMouldNameService.deleteMouldName(id,userCode)+" 条数据");
    }




}
