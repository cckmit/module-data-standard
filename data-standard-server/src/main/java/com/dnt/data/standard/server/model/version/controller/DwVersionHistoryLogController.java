package com.dnt.data.standard.server.model.version.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.version.entity.request.DwVersionHistoryLogRequest;
import com.dnt.data.standard.server.model.version.service.DwVersionDataService;
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
 * @description: 发布历史版本日志--业务代码 <br>
 * @date: 2022/4/24 上午10:59 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
@RestController
@RequestMapping("/v1/dw/version/log")
@Api(value = "dwVersionData", tags = "发布历史版本日志接口")
public class DwVersionHistoryLogController {
    @Autowired
    private DwVersionDataService dwVersionDataService;
    /**
     * 获取发布历史版本日志分页列表
     * @param projectId
     * @param request
     * @return
     */
    @ApiOperation(value="获取发布历史版本日志分页列表", notes="获取发布历史版本日志分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,
                      @RequestBody DwVersionHistoryLogRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwVersionHistoryLogController-->pageList 获取发布历史版本日志分页列表");
        }
        request.setProjectId(projectId);
        return Result.ok(this.dwVersionDataService.selectVersionHistoryLogPage(request));
    }

    /**
     * 查看历史版本日志详情
     * @param id
     * @return
     */
    @ApiOperation(value="查看历史版本日志详情", notes="查看历史版本日志详情接口")
    @GetMapping(value={"/detailVersionHistoryLog/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "版本日志ID", required = true, dataType = "Long")
    })
    public R detailVersionHistoryLog(@PathVariable("id") Long id){
        log.info("DwVersionHistoryLogController-->detailVersionHistoryLog 查询id为{}的历史版本日志详情",id);
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("查看历史版本日志详情时ID不能为空");
        }
        return Result.ok(this.dwVersionDataService.detailVersionHistoryLog(id));
    }

    /**
     * 删除发布历史版本日志
     * @param id
     * @return
     */
    @ApiOperation(value = "删除发布历史版本日志", notes = "删除发布历史版本日志接口")
    @DeleteMapping(value = {"/deleteVersionHistoryLog/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "发布历史版本日志ID", required = true, paramType = "path", dataType = "Long")
    })
    public R deleteVersionHistoryLog(@PathVariable("id") Long id){
        if(log.isInfoEnabled()) {
            log.info("DwVersionHistoryLogController-->deleteVersionHistoryLog 删除发布历史版本日志");
        }
        return this.dwVersionDataService.deleteVersionHistoryLog(id);

    }
}
