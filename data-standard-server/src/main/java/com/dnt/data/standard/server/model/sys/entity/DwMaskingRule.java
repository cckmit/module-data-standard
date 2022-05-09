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
@TableName("dw_masking_rule")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DwMaskingRule extends BaseEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    @ApiModelProperty("主键")
    private Long id;

    /**
     * 脱敏规则名称
     */
    @ApiModelProperty("脱敏规则名称")
    @TableField("masking_rule_name")
    private String maskingRuleName;


    /**
     * 脱敏效果，0-部分脱敏，1-全部脱敏
     */
    @ApiModelProperty("脱敏效果，0-部分脱敏，1-全部脱敏")
    @TableField("masking_type")
    private Integer maskingType;

    /**
     * 替换字符
     */
    @ApiModelProperty("替换字符")
    @TableField("replace_char")
    private String replaceChar;

    /**
     * 字段替换开始位置
     */
    @ApiModelProperty("字段替换开始位置")
    @TableField("start_position")
    private Integer startPosition;

    /**
     * 字段替换结束位置
     */
    @ApiModelProperty("字段替换结束位置")
    @TableField("end_position")
    private Integer endPosition;

    /**
     * 样例数据
     */
    @ApiModelProperty("样例数据")
    @TableField("sample_data")
    private String sampleData;

    /**
     *脱敏数据集数量
     */
    @ApiModelProperty("脱敏数据集数量")
    @TableField("data_set_num")
    private Integer dataSetNum;

    //页码
    @ApiModelProperty("当前页码")
    @TableField(exist = false)
    private Integer pageNum;
    @ApiModelProperty("每页显示的记录数")
    @TableField(exist = false)
    private Integer pageSize;

}
