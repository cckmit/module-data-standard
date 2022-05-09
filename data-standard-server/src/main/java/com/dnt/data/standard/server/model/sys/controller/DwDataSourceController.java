package com.dnt.data.standard.server.model.sys.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.client.DataSourceClient;
import com.dnt.data.standard.server.model.sys.entity.request.DwDataSourceRequest;
import com.dnt.data.standard.server.web.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description:  数据源-业务代码 <br>
 * @date: 2021/8/18 上午6:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@RestController
@RequestMapping("/v1/dw/dataSource")
@Api(value = "dwDataSource", tags = "数据源管理接口")
@Slf4j
public class DwDataSourceController {
    @Autowired
    private DataSourceClient dataSourceClient;

    /**获取数据源分页列表**/
    @ApiOperation(value="获取数据源分页列表", notes="获取数据源分页列表接口")
    @PostMapping(value={"/page/list"})
    public R pageList(@RequestHeader Long projectId,@RequestBody DwDataSourceRequest request){
        log.info("DwDatasourceController-->pageList 获取数据源分页列表");
        request.setProjectId(projectId);
        return Result.ok(dataSourceClient.dataSourcePageList(request));
    }


    @ApiOperation(value = "删除数据源", notes = "删除数据源")
    @DeleteMapping(value = {"/delete/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "数据源ID", required = true, paramType = "path", dataType = "Long")
    })
    public R deleteDataSource(@PathVariable("id") Long id){
        if(log.isInfoEnabled()) {
            log.info("DwDataSourceController-->deleteDataSource 删除数据源");
        }
        return Result.ok(dataSourceClient.deleteDataSource(id));

    }

}
