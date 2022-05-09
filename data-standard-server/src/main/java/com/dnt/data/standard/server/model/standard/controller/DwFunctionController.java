package com.dnt.data.standard.server.model.standard.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.standard.entity.request.DwFunctionRequest;
import com.dnt.data.standard.server.model.standard.service.DwFunctionService;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.standard.entity.DwFunction;
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
 * @description: 函数--业务代码 <br>
 * @date: 2021/7/19 下午2:40 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/function")
@Api(value = "dwFunction", tags = "函数接口")
@Slf4j
public class DwFunctionController extends BaseController {

    @Autowired
    private DwFunctionService dwFunctionService;
    //获数函数分页列表
    @ApiOperation(value="获取函数分页列表", notes="获取函数分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwFunctionRequest request){
        log.info("DwFunctionController-->pageList 获取函数分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwFunctionService.selectFunctionPage(request));
    }


    /**查看详情**/
    @ApiOperation(value="查看函数详情", notes="查看函数情接口")
    @GetMapping(value={"/detailFunction/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "函数ID", required = true, dataType = "Long")
    })
    public R detailFunction(@PathVariable("id") Long id){
        log.info("DwFunctionController--> detailFunction 查询id为{}的数据字典详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看函数信息时ID不能为空");
        }
        DwFunction t = this.dwFunctionService.detailFunction(id);
        return Result.ok(t);
    }

    /**添加函数**/
    @ApiOperation(value="添加函数", notes="添加函数接口")
    @PostMapping(value={"/saveFunction"})
    public R saveFunction(@RequestHeader String userCode,
                          @RequestHeader Long projectId,
                                 @RequestBody DwFunctionRequest request){
        log.info("DwFunctionController-->saveFunction 添加函数 ");
        request.setProjectId(projectId);
        return  this.dwFunctionService.saveFunction(request,userCode);
    }


    /**修改函数**/
    @ApiOperation(value="修改函数", notes="修改函数接口")
    @PutMapping(value={"/updateFunction"})
    public R updateFunction(@RequestHeader String userCode,
                            @RequestHeader Long projectId,
                                   @RequestBody DwFunctionRequest request){
        log.info("DwFunctionController-->updateFunction 修改函数");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改函数时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwFunctionService.updateFunction(request,userCode);
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"质量校验函数 name ",dwFunctionService);
        return Result.ok(t);
    }

    /**删除函数**/
    @ApiOperation(value="删除函数", notes="删除函数接口")
    @DeleteMapping(value={"/deleteFunction/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除函数ID", required = true, dataType = "Long")
    })
    public R deleteFunction(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwFunctionController-->Function 删除函数时ID为{}信息",id);

        return Result.ok("删除成功了："+ this.dwFunctionService.deleteFunction(id,userCode)+" 条数据");
    }

}
