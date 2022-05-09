package com.dnt.data.standard.server.model.sys.entity.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 脱敏规则表--请求对象 <br>
 * @date: 2021/11/1 下午1:19 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("脱敏规则表请求对象")
public class DwMaskingTableRequest extends PageEntity {


    /**主键**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    @ApiModelProperty("主键")
    private Long id;
    /**脱敏规则ID**/
    @ApiModelProperty("脱敏规则ID")
    @TableField("masking_rule_id")
    private Long maskingRuleId;

    @ApiModelProperty("批量开启/关闭血缘启用状态ID")
    private List<Long> ids;

    /**脱敏表名**/
    @ApiModelProperty("脱敏表名")
    @TableField("table_name")
    private String tableName;

    /**脱敏字段名称**/
    @ApiModelProperty("脱敏字段名称")
    @TableField("field_name")
    private String fieldName;
    /**血缘启用状态**/
    @ApiModelProperty("血缘启用状态 1 开启 0 关闭 ")
    @TableField("is_blood_rule_status")
    private Integer isBloodRuleStatus;
}
