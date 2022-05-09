package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @TableField(exist = false)
    private String categoryName;
    /**名称**/
    private String name;
    /**编号**/
    private String code;
    /**描述**/
    private String description;
    /**通用业务属性类型1 来源系统 2 所属应用 3 分区保留策略 4 表中文名称**/
    private Integer type;
}
