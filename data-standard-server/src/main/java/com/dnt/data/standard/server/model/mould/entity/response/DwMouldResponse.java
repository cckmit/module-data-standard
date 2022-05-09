package com.dnt.data.standard.server.model.mould.entity.response;

import com.dnt.data.standard.server.model.mould.entity.DwMouldField;
import com.dnt.data.standard.server.model.mould.entity.DwMouldFieldPartition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @description: 数据模型--返回数据对象  <br>
 * @date: 2021/9/22 上午10:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwMouldResponse implements Serializable {

    private static final long serialVersionUID = -6726859325224262355L;
    /**=========================基础数据==================================**/
    @ApiModelProperty("模型ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty("模型分类目录ID")
    private Long categoryId;
    /**分类全路径名称**/
    @ApiModelProperty("模型分类目录全路径名称")
    private String categoryName;
    /**名称**/
    @ApiModelProperty("模型名称")
    private String name;
    /**存储生命周期**/
    @ApiModelProperty("存储生命周期 yyyy-MM-dd")
    private String storageLifecycle;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**模型状态**/
    @ApiModelProperty("模型状态 1 已发布  0 未发布")
    private Integer mouldStatus;
    @ApiModelProperty("模型状态名 1 已发布  0 未发布")
    private String mouldStatusName;
    /**模型类型**/
    @ApiModelProperty("模型类型 1 手工建模  2 ddl建模 ")
    private Integer mouldType;
    @ApiModelProperty("模型类型名 1 手工建模  2 ddl建模 ")
    private String mouldTypeName;

    /**数据源类型**/
    @ApiModelProperty("数据源类型")
    private String typeId;
    /**数据源名称**/
    @ApiModelProperty("数据源类型名")
    private String typeName;
    /**=========================手工新建==================================**/
    /**模型关联的字段集合**/
    @ApiModelProperty("模型关联的字段集合")
    List<DwMouldField> fields;
    /**模型关联的分区**/
    @ApiModelProperty("模型关联的分区")
    List<DwMouldFieldPartition> fieldPartitions;
    @ApiModelProperty("ddl建模语句")
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

}
