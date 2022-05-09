package com.dnt.data.standard.server.model.mould.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description: 模型--实体对象 <br>
 * @date: 2021/8/4 下午4:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould")
@ApiModel("模型管理请求对象")
public class DwMould extends BaseEntity {
    /**=========================基础数据==================================**/
    @ApiModelProperty("模型ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty("模型分类目录ID")
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**名称**/
    @ApiModelProperty("模型名称")
    private String name;
    /**存储生命周期**/
    @ApiModelProperty("存储生命周期")
    @JSONField(format ="yyyy-MM-dd")
    private Date storageLifecycle;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**模型状态**/
    @ApiModelProperty("模型状态 1 已发布  0 未发布")
    private Integer mouldStatus;
    /**模型类型**/
    @ApiModelProperty("模型类型 1 手工建模  2 ddl建模 ")
    private Integer mouldType=1;
    /**模型物理化状态**/
    @ApiModelProperty("模型物理化状态 0 未物理化  1 已物理化 ")
    private Integer physicsStatus;
    /**发布模型时生成的sql   decimals**/
    private String releaseSql;

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



}
