package com.dnt.data.standard.server.model.sys.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.sys.entity.request.DwUserRequest;
import com.dnt.data.standard.server.model.sys.service.DwUserService;
import com.dnt.data.standard.server.model.controller.BaseController;
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
 * @description:  用户-业务代码 <br>
 * @date: 2021/7/8 下午6:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/user")
@Api(value = "dwUser", tags = "用户管理接口")
@Slf4j
public class DwUserController extends BaseController {

    @Autowired
    private DwUserService dwUserService;

    /**获取用户分页列表**/
    @ApiOperation(value="获取用户分页列表", notes="获取用户分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwUserRequest request){
        log.info("DwUserController-->pageList 获取用户分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwUserService.selectUserPage(request));
    }

    /**查看详情**/
    @ApiOperation(value="查看用户详情", notes="查看用户详情接口")
    @GetMapping(value={"/detailUser/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long")
    })
    public R detailUser(@PathVariable("id") Long id){
        log.info("DwUserController--> detailUser 查询id为{}的用户详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看用户信息时ID不能为空");
        }
        return Result.ok(this.dwUserService.detailUser(id));
    }
    
    /**添加用户**/
    @ApiOperation(value="添加用户", notes="添加用户接口")
    @PostMapping(value={"/saveUser"})
    public R saveUser(@RequestHeader String userCode,
                      @RequestHeader Long projectId,
                      @RequestBody DwUserRequest user){
        log.info("ApiUserController-->saveUser 添加用户");
        user.setProjectId(projectId);
        return  this.dwUserService.saveUser(user,userCode);
    }

    /**修改用户**/
    @ApiOperation(value="修改用户", notes="修改用户接口")
    @PutMapping(value={"/updateUser"})
    public R updateUser(@RequestHeader String userCode,
                        @RequestHeader Long projectId,
                        @RequestBody DwUserRequest request){
        log.info("DwUserController-->updateUser 修改用户");
        if (!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改用户时ID不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwUserService.updateUser(request,userCode);
    }

    /**检验字段指定的字段与值是否重复**/
    @ApiOperation(value="检验字段值是否重复", notes="检验字段值是否重复接口")
    @GetMapping(value={"/remoteCheck"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String")
    })
    public R remoteCheck(@RequestParam("value") String value,
                         @RequestParam("oldValue")   String oldValue,
                         @RequestParam("property")   String property){
        if(StringUtils.isEmpty(property)){
            return Result.fail("检查字段信息不能为空");
        }
        if(StringUtils.isEmpty(value)){
            return Result.fail("检查字段的内容信息不能为空");
        }

        boolean t = remoteCheck(property,oldValue,value,null,"用户 编号 ",dwUserService);
        return Result.ok(t);
    }
    /**删除分类**/
    @ApiOperation(value="删除用户", notes="删除用户接口")
    @DeleteMapping(value={"/deleteUser/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除用户ID", required = true, dataType = "Long")
    })
    public R deleteUser(@RequestHeader String userCode,@PathVariable("id") Long id){
        log.info("DwUserController-->deleteUser 删除用户时ID为{}信息",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看用户时ID不能为空");
        }
        return Result.ok("删除成功了："+ this.dwUserService.deleteUser(id,userCode)+" 条数据");
    }
}
