package com.dnt.data.standard.server.model.resource.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.resource.entity.request.CategoryLinkRequest;
import com.dnt.data.standard.server.model.resource.entity.request.DwResourcePanoramaRequest;
import com.dnt.data.standard.server.model.resource.entity.request.ResourcePanoramaRequest;
import com.dnt.data.standard.server.model.resource.service.DwResourcePanoramaService;
import com.dnt.data.standard.server.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @description: 资产全景--业务代码 <br>
 * @date: 2021/11/8 下午12:53 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@RestController
@RequestMapping("/v1/dw/panorama")
@Api(value = "dwResourcePanorama", tags = "资产全景接口")
@Slf4j
public class DwResourcePanoramaController {
    @Autowired
    private DwResourcePanoramaService dwResourcePanoramaService;

    /**
     * 获取资源列表
     * {@link #list(DwResourcePanoramaRequest)}
     */
    @ApiOperation(value="获取资产全景分页列表", notes="获取资产全景分页列表接口")
    @PostMapping(value={"/list"})
    public R list(@RequestHeader("projectId")Long projectId,@RequestBody DwResourcePanoramaRequest request){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->list 获取资产全景分页列表");
        }
        request.setProjectId(projectId);
        return Result.ok(this.dwResourcePanoramaService.selectPanoramaList(request));
    }

    /**
     * 获取资产全景下拉列表
     * @return
     */
    @ApiOperation(value="获取资产全景下拉列表", notes="获取资产全景下拉列表接口")
    @GetMapping(value={"/resourcePanoramaItem"})
    public R resourcePanoramaItem(){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->resourcePanoramaItem 获取资产全景下拉列表");
        }
        return Result.ok(this.dwResourcePanoramaService.getResourcePanoramaItem());
    }

    /**
     * 模型链路配置列表
     * @return
     */
    @ApiOperation(value="获取模型链路配置列表", notes="获取模型链路配置列表接口")
    @GetMapping(value={"/selectMouldCategory"})
    public R selectMouldCategory(){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->selectMouldCategory 获取模型链路配置列表");
        }
        return Result.ok(this.dwResourcePanoramaService.selectMouldCategory());
    }

    /**
     * 查询来源 与 所属应用接口 v1/dw/mould/selectMouldCurrencyAttribute
     * 选择模型链路中展示模型层级
     * @param request
     * @return
     */
    @ApiOperation(value="获取选择模型链路中展示模型层级列表", notes="获取选择模型链路中展示模型层级列表接口")
    @PostMapping(value={"/selectCategoryLink"})
    public R selectCategoryLink(@RequestBody CategoryLinkRequest request){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->selectCategoryLink 获取选择模型链路中展示模型层级列表");
        }
        //判断数据是否为空
        List<Long> ids = request.getCategoryIds();
        if(CollectionUtils.isEmpty(ids)){
            return Result.fail("查看模型链路的模型层级时，分类不能为空");
        }
        return Result.ok(this.dwResourcePanoramaService.selectCategoryLink(request));
    }


    /**
     * 查看详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看资产全景详情", notes="查看资产全景详情接口")
    @GetMapping(value={"/detailResourcePanorama/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "资产全景ID", required = true, dataType = "Long")
    })
    public R detailResourcePanorama(@PathVariable("id") Long id){
        if(log.isInfoEnabled()) {
            log.info("DwResourcePanoramaController-->detailResourcePanorama 查看资产全景详情接口 ID:{}",id);
        }
        if(!Optional.ofNullable(id).isPresent()){
            return Result.fail("查看资产全景时ID不能为空");
        }
        return Result.ok(this.dwResourcePanoramaService.detailResourcePanorama(id));
    }

    /**
     * 添加资产全景
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="添加资产全景", notes="添加资产全景接口")
    @PostMapping(value={"/saveResourcePanorama"})
    public R saveResourcePanorama(@RequestHeader String userCode,
                                  @RequestHeader Long projectId,
                        @RequestBody ResourcePanoramaRequest request){
        log.info("DwResourcePanoramaController-->saveResourcePanorama 添加资产全景 ");
        request.setProjectId(projectId);
        return  this.dwResourcePanoramaService.saveResourcePanorama(request,userCode);
    }

    /**
     * 修改资产全景
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="修改资产全景", notes="修改资产全景接口")
    @PutMapping(value={"/updateResourcePanorama"})
    public R updateResourcePanorama(@RequestHeader String userCode,
                                    @RequestHeader Long projectId,
                          @RequestBody ResourcePanoramaRequest request){
        log.info("DwResourcePanoramaController-->updateResourcePanorama 修改资产全景");
        if (!Optional.ofNullable(request.getId()).isPresent()){
            return Result.fail("修改资产全景时ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改资产全景时名称不能为空");
        }
        request.setProjectId(projectId);
        return  this.dwResourcePanoramaService.updateResourcePanorama(request,userCode);
    }

    /**
     * 渲染资产全景的层级关系
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="渲染资产全景的层级关系", notes="渲染资产全景的层级关系接口")
    @PostMapping(value={"/doRenderResourcePanorama"})
    public R doRenderResourcePanorama(@RequestHeader String userCode,
                                      @RequestHeader Long projectId,
                                      @RequestBody DwResourcePanoramaRequest request){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->doRenderResourcePanorama 渲染资产全景的层级关系");
        }

        Long id = request.getId();
        if(!Optional.ofNullable(id).isPresent()){
            return Result.fail("渲染资产全景时，资产全景ID不能为空");
        }
        request.setProjectId(projectId);
        return this.dwResourcePanoramaService.doRenderResourcePanorama(userCode,request);
    }

    /**
     * 下一级资产全景的数据与层级关系
     * @param userCode
     * @param request
     * @return
     */
    @ApiOperation(value="下一级资产全景的数据与层级关系", notes="下一级资产全景的数据与层级关系接口")
    @PostMapping(value={"/doRenderChildPanorama"})
    public R doRenderChildPanorama(@RequestHeader("userCode") String userCode, @RequestBody DwResourcePanoramaRequest request){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->doRenderResourcePanorama 下一级资产全景的数据与层级关系");
        }

        Long id = request.getId();
        if(!Optional.ofNullable(id).isPresent()){
            return Result.fail("渲染下一级资产全景时，ID不能为空");
        }

        return this.dwResourcePanoramaService.doRenderChildPanorama(userCode,request);
    }


    /**
     * 更新资产全景状态
     * @param userCode
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="更新资产全景状态", notes="更新资产全景状态接口")
    @PostMapping(value={"/updateStatus"})
    public R updateStatus(@RequestHeader String userCode,
                          @RequestHeader Long projectId,
                          @RequestBody DwResourcePanoramaRequest request){
        if(log.isInfoEnabled()) {
            log.info("DWResourcePanoramaController-->updateStatus 更新资产全景状态");
        }
        request.setProjectId(projectId);
        Long id = request.getId();
        if(!Optional.ofNullable(id).isPresent()){
            return Result.fail("更新状态时，资产全景ID不能为空");
        }
        Integer status = request.getStatus();
        if(!Optional.ofNullable(status).isPresent()){
            return Result.fail("更新状态时，状态值不能为空");
        }

        return this.dwResourcePanoramaService.updatePanoramaStatus(userCode,request);
    }

}
