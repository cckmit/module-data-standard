package com.dnt.data.standard.server.model.resource.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:资产盘点数据地图分布--入参对象  <br>
 * @date: 2021/10/18 下午2:13 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("资产盘点数据地图分布请求对象")
public class DataDistributionRequest extends PageEntity {
    /**数据源**/
    @ApiModelProperty("数据源类型ID")
    private Long dataSourceType;

    /**日期检索条件**/
    @ApiModelProperty("日期检索条件 1 近7天 2 近1月 3 近1年")
    private Integer interval;

    @ApiModelProperty("搜索类型 1 按查询次数 2 按登录人次")
    private Integer searchType;


}
