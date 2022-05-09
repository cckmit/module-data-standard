package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 模型关联字段分区--实体对象 <br>
 * @date: 2021/8/9 下午2:02 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_field_partition")
@ApiModel("模型关联字段分区请求对象")
public class DwMouldFieldPartition extends BaseEntity {
    private Long id;
    @ApiModelProperty(value = "模型ID")
    private Long mouldId;
    /**字段名称**/
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    /**字段类型**/
    @ApiModelProperty("类型")
    private String fieldType;
    @TableField(exist = false)
    @ApiModelProperty("类型名称")
    private String fieldTypeName;

    /**描述**/
    @ApiModelProperty("描述")
    private String description;
}
