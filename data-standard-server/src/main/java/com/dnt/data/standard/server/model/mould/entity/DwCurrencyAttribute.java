package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @description: 通用业务属性--实体对象  <br>
 * @date: 2021/8/18 下午6:51 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_currency_attribute")
@ApiModel("通用业务属性请求对象")
public class DwCurrencyAttribute extends BaseEntity {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**分类目录**/
    private Long categoryId;
    /*租户ID*/
    private Long tenantId;
    /*属性值*/
    @TableField(exist = false)
    private List<DwCurrencyAttributeValue> attributeValues;
    /**名称**/
    private String attributeName;
    /**编号**/
    private Integer attributeLength;
    /**通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本**/
    private Integer attributeType;

}
