package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMould;
import com.dnt.data.standard.server.model.mould.entity.request.DwPublicMouldRequest;
import com.dnt.data.standard.server.model.mould.service.DwPublicMouldService;
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
 * @description: 公共字段模型--业务代码 <br>
 * @date: 2021/7/29 下午4:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/public/mould/")
@Api(value = "dwPublicMould", tags = "公共字段模型接口")
@Slf4j
public class DwPublicMouldController extends BaseController {
    @Autowired
    public DwPublicMouldService dwPublicMouldService;


    /**查询公共字段模型下拉列表**/
    @ApiOperation(value="查询数据元列表的目录树", notes="查询数据元列表的目录树接口")
    @GetMapping(value={"/selectDataElementCategoryTree"})
    public R selectDataElementCategoryTree(){
        log.info("DwPublicMouldController--> selectDataElementCategoryTree 查询数据元列表的目录树");

        return Result.ok(this.dwPublicMouldService.selectDataElementCategoryTree());
    }



    /**查询公共字段模型下拉列表**/
    @ApiOperation(value="查询公共字段模型下拉列表", notes="查询公共字段模型下拉列表接口")
    @GetMapping(value={"/selectPublicMouldItem"})
    public R selectPublicMouldItem(Long dataElementCategoryId){
        log.info("DwPublicMouldController--> selectPublicMouldItem 查询公共字段模型下拉列表");

        Map<String, List<Map<String,Object>>> tmList = this.dwPublicMouldService.selectPublicMouldItem(dataElementCategoryId);
        return Result.ok(tmList);
    }

    /**l获取公共字段模型分页列表**/
    @ApiOperation(value="获取公共字段模型分页列表", notes="获取公共字段模型分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwPublicMouldRequest request){
        log.info("DwPublicMouldController-->pageList 获取公共字段模型分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwPublicMouldService.selectPublicMouldPage(request));
    }

    /**查看详情**/
    @ApiOperation(value="查看公共字段模型详情", notes="查看公共字段模型情接口")
    @GetMapping(value={"/detailPublicMould/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公共字段模型ID", required = true, dataType = "Long")
    })
    public R detailPublicMould(@PathVariable("id") Long id){
        log.info("DwPublicMouldController--> detailPublicMould 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看公共字段模型信息时ID不能为空");
        }
        DwPublicMould t = this.dwPublicMouldService.detailPubicMould(id);
        return Result.ok(t);
    }


    /**添加公共字段模型**/
    @ApiOperation(value="添加公共字段模型", notes="添加公共字段模型接口")
    @PostMapping(value={"/savePublicMould"})
    public R savePublicMould(@RequestHeader String userCode,
                             @RequestHeader Long projectId,
                             @RequestBody DwPublicMouldRequest request){
        log.info("DwPublicMouldController-->savePublicMould 添加公共字段模型 ");
        request.setProjectId(projectId);
        return  this.dwPublicMouldService.savePublicMould(request,userCode);
    }


    /**修改公共字段模型**/
    @ApiOperation(value="修改公共字段模型", notes="修改公共字段模型接口")
    @PutMapping(value={"/updatePublicMould"})
    public R updatePublicMould(@RequestHeader String userCode,
                               @RequestHeader Long projectId,
                               @RequestBody DwPublicMouldRequest request){
        log.info("DwPublicMouldController-->updatePublicMould 修改公共字段模型");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改公共字段模型时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwPublicMouldService.updatePublicMould(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"公共字段模型 name ",dwPublicMouldService);
        return Result.ok(t);
    }
    /**删除公共字段模型**/
    @ApiOperation(value="删除公共字段模型", notes="删除公共字段模型接口")
    @DeleteMapping(value={"/deletePublicMould/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除公共字段模型ID", required = true, dataType = "Long")
    })
    public R deletePublicMould(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwPublicMouldController-->PublicMould 删除公共字段模型时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除公共字段模型信息时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwPublicMouldService.deletePublicMould(id,userCode)+" 条数据");
    }

}
