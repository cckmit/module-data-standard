package com.dnt.data.standard.server.model.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description: 通过openfeign 调用 dataSource --远程调用 <br>
 * @date: 2021/8/10 上午9:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Component(value = "dataSourceProdClient")
@FeignClient(name = "dnt-management-datasource-prod", url = "${dnt.feign.prod.data.source.client.url}")
public interface DataSourceProdClient {

    @GetMapping(value = {"/data/source/getDefaultDataSource"})
    JSONObject getDefaultDataSource(@RequestHeader("modelType")String modelType,@RequestHeader("projectId")Long projectId);

    /**数据源类型下拉列表**/
    @GetMapping(value = {"/data/source/selectSourceTypeItem"})
    JSONObject selectSourceTypeItem(@RequestParam("modelType") String modelType);
}
