package com.dnt.data.standard.server.model.version.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 版本发布选择数据--入参数据实体 <br>
 * @date: 2022/4/19 下午2:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("版本发布选择数据入参请求对象")
public class VersionReleaseSelectDataRequest {
    @ApiModelProperty("选择数据ID集合")
    private List<Long> id;
    @ApiModelProperty("数据元 数据字典 数据基础库 指标 模型命名规则下数据标识")
    private String tableName;
}
