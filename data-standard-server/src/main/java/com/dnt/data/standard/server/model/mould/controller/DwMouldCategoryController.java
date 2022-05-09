package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldCategoryRequest;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.mould.service.DwMouldCategoryService;
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
 * @description: 模型层级--业务代码 <br>
 * @date: 2021/8/2 下午5:29 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/mould/category")
@Api(value = "dwMouldCategory", tags = "模型层级分类接口")
@Slf4j
public class DwMouldCategoryController extends BaseController {

    @Autowired
    private DwMouldCategoryService dwMouldCategoryService;

    /**
     * 获取模型层级分类树型列表
     * @param projectId
     * @return
     */
    @ApiOperation(value="获取模型层级分类树型列表", notes="获取模型层级分类树型列表接口")
    @GetMapping(value={"/treeList"})
    public R treeList(@RequestHeader Long projectId){
        log.info("DwMouldCategoryController-->treeList获取模型层级分类树型列表");
        return Result.ok(dwMouldCategoryService.selectTreeList(projectId));
    }

    /**
     * 获取模型层级分类树型列表
     * @param projectId
     * @return
     */
    @ApiOperation(value="分类目录树没有子目录则不展示一级目录", notes="分类目录树没有子目录则不展示一级目录接口")
    @GetMapping(value={"/newTreeList"})
    public R newTreeList(@RequestHeader Long projectId){
        log.info("DwMouldCategoryController-->newTreeList获取模型层级分类树型列表");
        return Result.ok(dwMouldCategoryService.selectNewTreeList(projectId));
    }

    /**
     * 获取模型层级分类分页列表
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="获取模型层级分类分页列表", notes="获取模型层级分类分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwMouldCategoryRequest request){
        log.info("DwMouldCategoryController-->pageList 获取模型层级分类分页列表");
        request.setProjectId(projectId);
        return Result.ok(this.dwMouldCategoryService.selectDwMouldCategoryPage(request));
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看模型层级分类详情接口", notes="查看模型层级分类详情接口")
    @GetMapping(value={"/detailMouldCategory/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "模型层级分类ID", required = true, dataType = "Long")
    })
    public R detailMouldCategory(@PathVariable("id") Long id){
        log.info("DwMouldCategoryController-->detailMouldCategory查询id为{}的模型层级分类详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看模型层级分类信息时ID不能为空");
        }
        DwMouldCategory t = this.dwMouldCategoryService.detailMouldCategory(id);
        return Result.ok(t);
    }

    /**
     * 检验字段指定的字段与值是否重复
     * @param value
     * @param oldValue
     * @param property
     * @param mouldCategoryId
     * @return
     */
    @ApiOperation(value="检验字段值是否重复", notes="检验字段值是否重复接口")
    @GetMapping(value={"/remoteCheck"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String"),
            @ApiImplicitParam(name = "mouldCategoryId", value = "分类ID", required = false, dataType = "Long")
    })
    public R remoteCheck(@RequestParam("value") String value,
                         @RequestParam("oldValue")   String oldValue,
                         @RequestParam("property")   String property,
                         @RequestParam("mouldCategoryId") Long mouldCategoryId){
        if(StringUtils.isEmpty(property)){
            return Result.fail("检查字段信息不能为空");
        }
        if(StringUtils.isEmpty(value)){
            return Result.fail("检查字段的内容信息不能为空");
        }

        boolean t = remoteCheck(property,oldValue,value,mouldCategoryId,"模型层级分类 name ",dwMouldCategoryService);
        return Result.ok(t);
    }

    /**
     * 添加分类
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="添加模型层级分类接口", notes="添加模型层级分类接口")
    @PostMapping(value={"/saveMouldCategory"})
    public R saveMouldCategory(@RequestHeader String userCode,
                               @RequestHeader Long projectId,
                          @RequestBody DwMouldCategoryRequest request){
        log.info("DwMouldCategoryController-->saveMouldCategory添加模型层级分类操作");
        request.setProjectId(projectId);
        return  this.dwMouldCategoryService.saveMouldCategory(request,userCode);
    }

    /**
     * 修改分类
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="修改模型层级分类接口", notes="修改模型层级分类接口")
    @PutMapping(value={"/updateMouldCategory"})
    public R updateMouldCategory(@RequestHeader String userCode,
                                 @RequestHeader Long projectId,
                            @RequestBody DwMouldCategoryRequest request){
        log.info("DwMouldCategoryController-->updateMouldCategory编辑模型层级分类操作");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型层级分类时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwMouldCategoryService.updateMouldCategory(request,userCode);
    }

    /**
     * 删除分类
     * @param userCode
     * @param id
     * @return
     */
    @ApiOperation(value="删除模型层级分类接口", notes="删除模型层级分类接口")
    @DeleteMapping(value={"/deleteMouldCategory/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除模型层级分类ID", required = true, dataType = "Long")
    })
    public R deleteMouldCategory(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwMouldCategoryController-->deleteMouldCategory 删除模型层级分类时ID为{}信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看模型层级分类时ID不能为空");
        }
        return this.dwMouldCategoryService.deleteMouldCategory(id,userCode);
    }
}
