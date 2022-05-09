package com.dnt.data.standard.server.model.client;

import com.alibaba.fastjson.JSONObject;
import com.dnt.data.standard.server.model.sys.entity.request.DwDataSourceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 通过openfeign 调用 dataSource --远程调用 <br>
 * @date: 2021/8/10 上午9:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Component(value = "dataSourceClient")
@FeignClient(name = "dnt-management-datasource", url = "${dnt.feign.data.source.client.url}")
public interface DataSourceClient {
    /**
     * 分页查询数据源信息
     * @param request
     * @return
     */
    @PostMapping(value = {"/data/source/page"})
    Object dataSourcePageList(DwDataSourceRequest request);
    /**
     * 删除数据源
     * @param id
     * @return
     */
    @DeleteMapping(value = {"/data/source/{id}"})
    Object deleteDataSource(@PathVariable("id") Long id);

    /**
     * 查询引入数据源对应的分类下拉列表
     * @param modelType
     * @return
     */
    @GetMapping(value = {"/data/source/selectSourceTypeItem"})
    JSONObject selectSourceTypeItem(@RequestParam("modelType") String modelType);

    /**
     * 查询引入数据源下拉列表
     * @param modelType
     * @param typeId
     * @return
     */
    @GetMapping(value = {"/data/source/selectSourceItem"})
    JSONObject selectSourceItem(@RequestParam("modelType") String modelType,
                                @RequestParam("typeId") Long typeId);

    /**
     *  根据ID 查询数据源信息
     * @param modelType
     * @param sourceId
     * @return
     */
    @GetMapping(value = {"/data/source/getDataSourceById"})
    JSONObject getDataSourceById(@RequestHeader("modelType")String modelType,@RequestHeader("sourceId")Long sourceId);

}
