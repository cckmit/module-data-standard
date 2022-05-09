package com.dnt.data.standard.server.model.version.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.version.entity.request.DwVersionRequest;
import com.dnt.data.standard.server.model.version.entity.request.VersionReleaseRequest;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.version.service.DwVersionService;
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
 * @description: 版本管理-业务代码 <br>
 * @date: 2022/4/14 上午9:56 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
@RestController
@RequestMapping("/v1/dw/version")
@Api(value = "dwVersion", tags = "版本管理接口")
public class DwVersionController extends BaseController {

    @Autowired
    private DwVersionService dwVersionService;

    /**
     * 获取版本管理分页列表
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="获取版本管理分页列表", notes="获取版本管理分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,
                      @RequestBody DwVersionRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwVersionController-->pageList 获取版本管理分页列表");
        }
        request.setProjectId(projectId);
        return Result.ok(dwVersionService.selectVersionPage(request));
    }

    /**
     * 获取发布数据的目录树
     * @param projectId
     * @return
     */
    @ApiOperation(value="获取发布数据的目录树", notes="获取发布数据的目录树接口")
    @PostMapping(value={"/selectReleaseTree"})
    public R selectReleaseTree(@RequestHeader Long projectId){
        if(log.isInfoEnabled()) {
            log.info("DwVersionController-->selectReleaseTree 获取发布数据的目录树");
        }
        return Result.ok(dwVersionService.selectReleaseTree(projectId));
    }

    /**
     * 查询不同数据类型下的分页列表
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="查询不同数据类型下的分页列表", notes="查询不同数据类型下的分页列表接口")
    @PostMapping(value={"/selectCategoryPageList"})
    public R selectCategoryPageList(@RequestHeader Long projectId,
                      @RequestBody CategoryPageListRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwVersionController-->selectCategoryPageList 查询不同数据类型下的分页列表");
        }
        request.setProjectId(projectId);
        return Result.ok(dwVersionService.selectCategoryPageList(request));
    }

    /**
     * 查询不同数据类型下分页列表表头
     * @return
     */
    @ApiOperation(value="查询不同数据类型下分页列表表头", notes="查询不同数据类型下分页列表表头接口")
    @GetMapping(value={"/selectCategoryPageHeader"})
    public R selectCategoryPageHeader(){
        log.info("DwVersionController-->selectCategoryPageHeader 查询不同数据类型下分页列表表头");
        return Result.ok(this.dwVersionService.selectCategoryPageHeader());
    }

    @ApiOperation(value="版本的发布功能", notes="版本的发布功能接口")
    @PostMapping(value={"/doVersionRelease"})
    public R doVersionRelease(@RequestHeader Long projectId,
                                    @RequestBody VersionReleaseRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwVersionController-->doVersionRelease 版本的发布功能");
        }
        request.setProjectId(projectId);
        return dwVersionService.doVersionRelease(request);
    }
    /**
     * 查看详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看版本管理详情", notes="查看版本管理详情接口")
    @GetMapping(value={"/detailVersion/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "版本管理ID", required = true, dataType = "Long")
    })
    public R detailVersion(@PathVariable("id") Long id){
        log.info("DwVersionController-->detailVersion 查询id为{}的版本管理详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看版本管理信息时ID不能为空");
        }
        return Result.ok(this.dwVersionService.detailVersion(id));
    }

    /**
     * 删除版本管理
     * @param id
     * @return
     */
    @ApiOperation(value = "删除版本管理", notes = "删除版本管理接口")
    @DeleteMapping(value = {"/deleteVersion/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "版本管理ID", required = true, paramType = "path", dataType = "Long")
    })
    public R deleteVersion(@PathVariable("id") Long id){
        if(log.isInfoEnabled()) {
            log.info("DwVersionController-->deleteVersion 删除版本管理");
        }
        return this.dwVersionService.deleteVersion(id);

    }

}
