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

/* *
 * @desc  通用业务属性Value值实体对象
 * @Return:
 * @author: ZZP
 * @date:  2022/5/18 15:08
 * @Version V1.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_currency_attribute_value")
@ApiModel("通用业务属性value值对象")
public class DwCurrencyAttributeValue extends BaseEntity {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**分类目录**/
    private Long categoryId;
    /*租户ID*/
    private Long tenantId;
    /*属性ID*/
    private Long attributeId;
    /*属性值*/
    private String attributeValue;
    /**通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本**/
    private Integer attributeType;

    /**path**/
    private String path;

    private Integer level;

    private Long parentId;

    private Integer isLeaf;

    @TableField(exist = false)
    private List<DwCurrencyAttributeValue> childs;

}
