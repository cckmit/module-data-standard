package com.dnt.data.standard.server.model.standard.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.standard.entity.request.DwCategoryRequest;
import com.dnt.data.standard.server.model.standard.service.DwCategoryService;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
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
 * @description:  业务分类-业务代码 <br>
 * @date: 2021/7/8 下午6:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/category")
@Api(value = "dwCategory", tags = "业务分类接口")
@Slf4j
public class DwCategoryController extends BaseController {
    @Autowired
    private DwCategoryService dwCategoryService;

    @ApiOperation(value="获取业务分类树型列表", notes="获取业务分类树型列表接口")
    @GetMapping(value={"/treeList/{dwType}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dwType", value = "业务分类类型", required = true, dataType = "String")
    })
    public R treeList(@RequestHeader Long projectId, @PathVariable("dwType") String dwType){
        log.info("DwCategoryController-->treeList获业务分类树型列表");
        if (!Optional.fromNullable(dwType).isPresent()){
            return Result.fail("获取业务分类数据时，类型不能为空");
        }

        return Result.ok(dwCategoryService.selectTreeList(projectId,dwType));
    }

    /**查看详情**/
    @ApiOperation(value="查看业务分类详情接口", notes="查看业务分类详情接口")
    @GetMapping(value={"/detailCategory/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "业务分类ID", required = true, dataType = "Long")
    })
    public R detailCategory(@PathVariable("id") Long id){
        log.info("DwCategoryController-->detailCategory查询id为{}的业务分类详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看业务分类信息时ID不能为空");
        }
        DwCategory t = this.dwCategoryService.detailCategory(id);
        return Result.ok(t);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"业务分类 name ",dwCategoryService);
        return Result.ok(t);
    }

    /**添加分类**/
    @ApiOperation(value="添加业务分类接口", notes="添加业务分类接口")
    @PostMapping(value={"/saveCategory"})
    public R saveCategory(@RequestHeader String userCode,
                          @RequestHeader Long projectId,
                          @RequestBody DwCategoryRequest request){
        log.info("DwCategoryController-->saveCategory添加业务分类操作");
        request.setProjectId(projectId);
        return  this.dwCategoryService.saveCategory(request,userCode);
    }

    /**修改分类**/
    @ApiOperation(value="修改业务分类接口", notes="修改业务分类接口")
    @PutMapping(value={"/updateCategory"})
    public R updateCategory(@RequestHeader String userCode,
                            @RequestHeader Long projectId,
                            @RequestBody DwCategoryRequest request){
        log.info("DwCategoryController-->updateCategory编辑业务分类操作");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改业务分类时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwCategoryService.updateCategory(request,userCode);
    }

    /**删除分类**/
    @ApiOperation(value="删除业务分类接口", notes="删除业务分类接口")
    @DeleteMapping(value={"/deleteCategory/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除业务分类ID", required = true, dataType = "Long")
    })
    public R deleteCategory(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("APICategoryController-->deleteCategory 删除业务分类时ID为{}信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看业务分类时ID不能为空");
        }
        return this.dwCategoryService.deleteCategory(id,userCode);
    }




}
