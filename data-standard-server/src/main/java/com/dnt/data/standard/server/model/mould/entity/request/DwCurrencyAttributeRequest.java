package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 来源系统--入参对象 <br>
 * @date: 2021/8/18 下午18:37 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("通用业务属性请求对象")
public class DwCurrencyAttributeRequest extends PageEntity {
    private Long id;
    /**分类目录**/
    @ApiModelProperty("分类目录ID")
    private Long categoryId;
    /**名称**/
    @ApiModelProperty("名称")
    private String name;
    /**编号**/
    @ApiModelProperty("标识")
    private String code;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**通用业务属性类型1 来源系统 2 所属应用 3 分区保留策略 4 表中文名称**/
    @ApiModelProperty(value = "通用业务属性类型1 来源系统 2 所属应用 3 分区保留策略 4 表中文名称",required = true)
    private Integer type;

}
