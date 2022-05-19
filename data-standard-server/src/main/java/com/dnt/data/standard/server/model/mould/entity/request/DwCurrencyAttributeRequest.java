package com.dnt.data.standard.server.model.mould.entity.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dnt.data.standard.server.model.entity.PageEntity;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    /**名称**/
    @ApiModelProperty("名称")
    private String attributeName;
    /**通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本**/
    @ApiModelProperty(value = "通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本")
    private Integer attributeType;
    /**长度**/
    @ApiModelProperty("长度")
    private Integer attributeLength;

    /*属性值list*/
    @TableField(exist = false)
    private List<DwCurrencyAttributeValue> attributeValue;


}
