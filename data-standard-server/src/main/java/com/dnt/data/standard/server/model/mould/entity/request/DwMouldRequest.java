package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import com.dnt.data.standard.server.model.mould.entity.DwMouldField;
import com.dnt.data.standard.server.model.mould.entity.DwMouldFieldPartition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @description: 模型--入参对象 <br>
 * @date: 2021/8/6 上午11:03 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型入参对象")
public class DwMouldRequest extends PageEntity {

    private static final long serialVersionUID = 5320564565858845666L;
    /**=========================基础数据==================================**/
    @ApiModelProperty("模型ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty("模型分类目录ID")
    private Long categoryId;
    /**名称**/
    @ApiModelProperty("模型名称")
    private String name;
    /**存储生命周期**/
    @ApiModelProperty("存储生命周期")
    private Date storageLifecycle;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;

    /**模型状态**/
    @ApiModelProperty("模型状态 1 已发布  0 未发布")
    private Integer mouldStatus;

    /**模型类型**/
    @ApiModelProperty("模型类型 1 手工建模  2 ddl建模 默认为1")
    private Integer mouldType=1;

    /**=========================手工新建==================================**/
    /**模型关联的字段集合**/
    @ApiModelProperty("模型关联的字段集合")
    List<DwMouldField> fields;
    /**模型关联的分区**/
    @ApiModelProperty("模型关联的分区")
    List<DwMouldFieldPartition> fieldPartitions;

    /**=========================ddl==================================**/
    /**数据源类型**/
    @ApiModelProperty("数据源类型")
    private String typeId;
    /**数据源名称**/
    @ApiModelProperty("数据源类型名")
    private String typeName;
    /**DDL语句**/
    @ApiModelProperty("DDL语句")
    private String ddlStatement;
    /**=========================通用业务属性==================================**/
    @ApiModelProperty("来源系统ID")
    private Long sourceId;
    @ApiModelProperty("来源系统名称")
    private String sourceName;
    @ApiModelProperty("所属应用ID")
    private Long applicationId;
    @ApiModelProperty("所属应用名称")
    private String applicationName;
    @ApiModelProperty("分区保留策略ID")
    private Long partitionId;
    @ApiModelProperty("分区保留策略名称")
    private String partitionName;
    @ApiModelProperty("表中文名的ID")
    private Long tableId;
    @ApiModelProperty("表中文名的名称")
    private String tableName;
    @ApiModelProperty("负责人ID")
    private Long bossheadId;
    @ApiModelProperty("负责人名称")
    private String bossheadName;

    @ApiModelProperty("选择公共字段集ID")
    public List<Long> publicMouldIds;
}
