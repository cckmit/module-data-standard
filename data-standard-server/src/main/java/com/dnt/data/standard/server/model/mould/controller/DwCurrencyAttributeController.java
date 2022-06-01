package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeService;
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
import java.util.regex.Pattern;

/**
 * @description: 通用业务属性--业务代码 <br>
 * @date: 2021/7/28 下午1:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/CurrencyAttribute")
@Api(value = "dwCurrencyAttribute", tags = "通用业务属性接口")
@Slf4j
public class DwCurrencyAttributeController extends BaseController {
    @Autowired
    private DwCurrencyAttributeService dwCurrencyAttributeService;

    /*支持中英文字母数字下划线*/
    public static  final  String REGEX_NAME= "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";


    //获取数通用业务属性分页列表
    @ApiOperation(value="获取通用业务属性分页列表", notes="获取通用业务属性分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwCurrencyAttributeRequest request){
        log.info("DwCurrencyAttributeController-->pageList 获取通用业务属性分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwCurrencyAttributeService.selectCurrencyAttributePage(request));
    }

    //获取数通用业务属性树支持根据属性名和属性值模糊查询
    @ApiOperation(value="获取通用业务属性树", notes="获取通用业务属性树接口")
    @GetMapping(value={"/tree/list"})
    public R treeList(@RequestHeader Long projectId,@RequestParam(value = "name",required = false) String name){
        log.info("DwCurrencyAttributeController-->treeList 获取通用业务属性树");
        DwCurrencyAttributeRequest request=new DwCurrencyAttributeRequest();
        request.setProjectId(projectId);
        if(Optional.fromNullable(name).isPresent()){
            request.setAttributeName(name);
        }

        List list= dwCurrencyAttributeService.selectCurrencyAttributeTree(request);
        return Result.ok(list);
    }


    /**查看详情**/
    @ApiOperation(value="查看通用业务属性详情", notes="查看通用业务属性情接口")
    @GetMapping(value={"/detailCurrencyAttribute/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "通用业务属性ID", required = true, dataType = "Long")
    })
    public R detailCurrencyAttribute(@PathVariable("id") Long id){
        log.info("DwCurrencyAttributeController--> detailCurrencyAttribute 查看id为{}的通用业务属性详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看通用业务属性信息时ID不能为空");
        }
        DwCurrencyAttribute t = this.dwCurrencyAttributeService.detailCurrencyAttribute(id);
        return Result.ok(t);
    }


    /**添加通用业务属性**/
    @ApiOperation(value="添加通用业务属性", notes="添加通用业务属性接口")
    @PostMapping(value={"/saveCurrencyAttribute"})
    public R saveCurrencyAttribute(@RequestHeader String userCode,
                                    @RequestHeader Long projectId,
                                    @RequestBody DwCurrencyAttributeRequest request){
        log.info("DwCurrencyAttributeController-->saveCurrencyAttribute 添加通用业务属性 ");
        request.setProjectId(projectId);
        if (!Optional.fromNullable(request.getAttributeName()).isPresent()){
            return Result.fail("添加通用业务属性时属性名不能为空");
        }
        if (!Optional.fromNullable(request.getAttributeType()).isPresent()){
            return Result.fail("添加通用业务属性时属性类型不能为空");
        }


        /*属性名和属性类型校验*/
        if(request.getAttributeName()==null || request.getAttributeName().isEmpty()){
            return Result.fail("属性名不能为空！");
        }else{
            if(request.getAttributeName()!=null && request.getAttributeName().length()>20){
                return Result.fail("属性名长度不能超过20！");
            }
            if(!Pattern.matches(REGEX_NAME, request.getAttributeName())){
                return Result.fail("属性名仅支持中英文字母数字下划线！");
            }
        }

        if(!(request.getAttributeType()>0 && request.getAttributeType()<6)){
            return Result.fail("属性类型设置错误！");
        }

        return  this.dwCurrencyAttributeService.saveCurrencyAttribute(request,userCode);
    }


    /**修改通用业务属性**/
    @ApiOperation(value="修改通用业务属性", notes="修改通用业务属性接口")
    @PutMapping(value={"/updateCurrencyAttribute"})
    public R updateCurrencyAttribute(@RequestHeader String userCode,
                                     @RequestHeader Long projectId,
                                      @RequestBody DwCurrencyAttributeRequest request){
        log.info("DwCurrencyAttributeController-->updateCurrencyAttribute 修改通用业务属性");
        request.setProjectId(projectId);
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改通用业务属性时ID不能为空");
        }
        /*属性名和属性类型校验*/
        if (!Optional.fromNullable(request.getAttributeName()).isPresent()){
            if(request.getAttributeName()!=null && request.getAttributeName().length()>20){
                return Result.fail("属性名长度不能超过20！");
            }
            if(!Pattern.matches(REGEX_NAME, request.getAttributeName())){
                return Result.fail("属性名仅支持中英文字母数字下划线！");
            }
        }
        if (Optional.fromNullable(request.getAttributeType()).isPresent()){
            if(!(request.getAttributeType()>0 && request.getAttributeType()<6)){
                return Result.fail("属性类型设置错误！");
            }
        }

        return  this.dwCurrencyAttributeService.updateCurrencyAttribute(request,userCode);
    }

    /**检验字段指定的字段与值是否重复**/
    /*@ApiOperation(value="检验字段值是否重复", notes="检验字段值是否重复接口")
    @GetMapping(value={"/remoteCheck"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String"),
            @ApiImplicitParam(name = "categoryId", value = "项目ID", required = false, dataType = "Long")
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

        boolean t = remoteCheck(property,oldValue,value,categoryId,"通用业务属性 name ",dwCurrencyAttributeService);
        log.info("检验字段值是否重复接口:"+t+"");
        return Result.ok(t);
    }*/


    /**检验字段指定的字段与值是否重复**/
    @ApiOperation(value="检验字段值在当前项目是否重复", notes="检验字段值在当前项目是否重复")
    @GetMapping(value={"/remoteCheckInProject"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String")
    })
    public R remoteCheckInProject(@RequestParam("value") String value,
                         @RequestParam("oldValue")   String oldValue,
                         @RequestParam("property")   String property,
                         @RequestHeader("projectId") Long projectId){
        if(StringUtils.isEmpty(property)){
            return Result.fail("检查字段信息不能为空");
        }
        if(StringUtils.isEmpty(value)){
            return Result.fail("检查字段的内容信息不能为空");
        }

        boolean t = remoteCheckInProject(property,oldValue,value,projectId,"通用业务属性 name ",dwCurrencyAttributeService);
        log.info("检验字段值是否重复接口:"+t+"");
        return Result.ok(t);
    }

    /**删除通用业务属性**/
    @ApiOperation(value="删除通用业务属性", notes="删除通用业务属性接口")
    @DeleteMapping(value={"/deleteCurrencyAttribute/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除通用业务属性ID", required = true, dataType = "Long")
    })
    public R deleteCurrencyAttribute(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwCurrencyAttributeController-->CurrencyAttribute 删除通用业务属性时ID为{}信息",id);

        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除通用业务属性信息时ID不能为空");
        }
        DwCurrencyAttribute dwCurrencyAttribute = this.dwCurrencyAttributeService.getById(id);
        if(dwCurrencyAttribute.getCreateUser().equals("内置")){
            return Result.fail("内置属性不允许删除！");
        }
        return Result.ok("删除成功了："+ this.dwCurrencyAttributeService.deleteCurrencyAttribute(id,userCode)+" 条数据");
    }

}
