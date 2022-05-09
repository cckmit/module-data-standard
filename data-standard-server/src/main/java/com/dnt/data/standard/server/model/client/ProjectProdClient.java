package com.dnt.data.standard.server.model.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description: 通过openfeign 调用 project --远程调用 <br>
 * @date: 2021/8/11 上午9:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Component(value = "projectProdClient")
@FeignClient(name = "dnt-management-project-prod", url = "${dnt.feign.prod.project.client.url}")
public interface ProjectProdClient {

    @GetMapping(value = {"/sys/project/selectProjectItem"})
    JSONObject selectProjectItem(@RequestParam("modelType") String modelType ,@RequestParam("sourceTypeId") Long sourceTypeId);

}
