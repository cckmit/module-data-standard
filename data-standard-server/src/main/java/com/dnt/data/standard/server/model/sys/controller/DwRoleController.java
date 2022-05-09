package com.dnt.data.standard.server.model.sys.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.sys.entity.request.DwRoleRequest;
import com.dnt.data.standard.server.model.sys.service.DwRoleService;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:  角色-业务代码 <br>
 * @date: 2021/7/8 下午6:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/role")
@Api(value = "dwRole", tags = "角色管理接口")
@Slf4j
public class DwRoleController {

    @Autowired
    private DwRoleService dwRoleService;

    /**
     * 获取角色分页列表
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="获取角色分页列表", notes="获取角色分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwRoleRequest request){
        log.info("DwRoleController-->pageList 获取角色分页列表");
        request.setProjectId(projectId);
        return Result.ok(dwRoleService.selectRolePage(request));
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看角色详情", notes="查看角色详情接口")
    @GetMapping(value={"/detailRole/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "Long")
    })
    public R detailRole(@PathVariable("id") Long id){
        log.info("DwRoleController-->detailRole 查询id为{}的角色详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看角色信息时ID不能为空");
        }
        return Result.ok(this.dwRoleService.detailRole(id));
    }
}
