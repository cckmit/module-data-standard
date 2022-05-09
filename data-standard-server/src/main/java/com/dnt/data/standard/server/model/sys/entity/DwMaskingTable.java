package com.dnt.data.standard.server.model.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据脱敏规则--实体对象
 */
@Data
@TableName("dw_masking_table")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwMaskingTable extends BaseEntity {

    /**主键**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    @ApiModelProperty("主键")
    private Long id;
    /**脱敏规则ID**/
    @ApiModelProperty("脱敏规则ID")
    @TableField("masking_rule_id")
    private Long maskingRuleId;

    /**数据源类型ID**/
    @ApiModelProperty("数据源类型ID")
    @TableField("data_source_type_id")
    private Long dataSourceTypeId;
    /**数据源类型名称**/
    @ApiModelProperty("数据源类型名称")
    @TableField("data_source_type_name")
    private String dataSourceTypeName;

    /**数据源ID**/
    @ApiModelProperty("数据源ID")
    @TableField("data_source_id")
    private Long dataSourceId;
    /**数据源名称**/
    @ApiModelProperty("数据源名称")
    @TableField("data_source_name")
    private String dataSourceName;
    /**库ID**/
    @ApiModelProperty("库ID")
    @TableField("db_id")
    private Long dbId;
    /**库名称**/
    @ApiModelProperty("库名称")
    @TableField("db_name")
    private String dbName;
    /**脱敏表的ID**/
    @ApiModelProperty("脱敏表的ID")
    @TableField("table_id")
    private Long tableId;
    /**脱敏表名**/
    @ApiModelProperty("脱敏表名")
    @TableField("table_name")
    private String tableName;

    /**脱敏字段ID**/
    @ApiModelProperty("脱敏字段ID")
    @TableField("field_id")
    private Long fieldId;

    /**脱敏字段名称**/
    @ApiModelProperty("脱敏字段名称")
    @TableField("field_name")
    private String fieldName;
    /**血缘脱敏表数量**/
    @ApiModelProperty("血缘脱敏表数量")
    @TableField("blood_table_count")
    private Integer bloodTableCount;
    /**血缘启用状态**/
    @ApiModelProperty("血缘启用状态 1 开启 0 关闭 ")
    @TableField("is_blood_rule_status")
    private Integer isBloodRuleStatus;



}
