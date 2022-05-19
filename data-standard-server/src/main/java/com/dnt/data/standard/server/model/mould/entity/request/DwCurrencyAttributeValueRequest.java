package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* *
 * @desc
 * @Return:
 * @author: ZZP
 * @date:  2022/5/18 15:34
 * @Version V1.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("通用业务属性value值请求对象")
public class DwCurrencyAttributeValueRequest extends PageEntity {
    private Long id;

    /**通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本**/
    @ApiModelProperty(value = "通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本")
    private Integer attributeType;

    @ApiModelProperty("父节点Id")
    private Long parentId;

    @ApiModelProperty("属性Id")
    private Long attributeId;

    /**属性值**/
    @ApiModelProperty("属性值")
    private String attributeValue;


}
