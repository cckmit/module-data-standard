package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:  模型关联字段--实体对象<br>
 * @date: 2021/8/9 下午2:02 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_field")
@ApiModel("模型关联字段请求对象")
public class DwMouldField extends BaseEntity {
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    @ApiModelProperty(value = "前台key信息")
    private String key;
    /**
     * 公共字段模型ID
     */
    @ApiModelProperty(value = "模型ID")
    private Long mouldId;
    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称",required = true)
    private String name;
    /**
     * 字段类型
     */
    @ApiModelProperty("字段类型")
    private String fieldType;
    /**
     * 字段类型的名称
     */
    @TableField(exist = false)
    private String fieldTypeName;
    /**
     * 描述
     */
    @ApiModelProperty("字段描述")
    private String description;
    /**
     * 数据元分类目录ID
     */
    @ApiModelProperty("关联字段标准分类目录ID(数据元分类目录ID)")
    private Long standardCategoryId;

    @ApiModelProperty("关联字段标准分类目录名称")
    @TableField(exist = false)
    private String standardCategoryName;
    /**
     * 关联字段标准
     */
    @ApiModelProperty("关联字段标准")
    private String fieldStandard;

    /**
     * 关联字段标准名称
     */
    @ApiModelProperty("关联字段标准名称")
    @TableField(exist = false)
    private String fieldStandardName="";
    /**
     * 主键标识
     */
    @ApiModelProperty("主键标识")
    private Integer primaryFlag;
    /**
     * 非空标识
     */
    @ApiModelProperty("是否非常")
    private Integer emptyFlag;
    /**
     * 长度信息
     */
    @ApiModelProperty("长度")
    private Integer length;
}
