package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 公共字段模型关联字段--实体类 <br>
 * @date: 2021/7/29 下午3:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_public_mould_field")
@ApiModel("公共字段模型关联字段请求对象")
public class DwPublicMouldField extends BaseEntity {
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**公共字段模型ID**/
    @ApiModelProperty(value = "公共字段模型ID",required = true)
    private Long publicMouldId;
    /**字段名称**/
    @ApiModelProperty(value = "字段名称",required = true)
    private String name;
    /**字段类型**/
    @ApiModelProperty("字段类型")
    private String fieldType;
    /**描述**/
    @ApiModelProperty("字段描述")
    private String description;
    /**数据元分类目录ID**/
    @ApiModelProperty("关联字段标准分类目录ID(数据元分类目录ID)")
    private Long standardCategoryId;
    /**关联字段标准**/
    @ApiModelProperty("关联字段标准")
    private String fieldStandard;
    /**主键标识**/
    @ApiModelProperty("主键标识")
    private Integer primaryFlag;
    /**非空标识**/
    @ApiModelProperty("是否非常")
    private Integer emptyFlag;
    /**长度信息**/
    @ApiModelProperty("长度")
    private Integer length;


}
