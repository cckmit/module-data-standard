package com.dnt.data.standard.server.model.resource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 资产全景--实体对象 <br>
 * @date: 2021/11/8 下午1:25 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_resource_panorama")
public class DwResourcePanorama extends BaseEntity implements Serializable {
    private Long id;
    private String name;
    private Integer status;
    /**
     * 智能数仓-工作组ID
     */
    private Long groupId;
    /**
     * 智能数仓-工作组名称
     */
    private String groupName;

    /**
     * 模型链路配置 顶级分类目录
     */
    private String mouldTopCategoryIds;
    /**
     * 模型链路配置 顶级分类目录名称
     */
    private String mouldTopCategoryNames;
    /**
     * 业务数据来源ID
     */
    private String sourceIds;
    /**
     * 业务数据来源名称
     */
    private String sourceNames;
    /**
     * 模型链路ID
     */
    private Long linkCategoryId;
    /**
     * 模型链路名称
     */
    private String linkCategoryName;
    /**
     * 应用ID
     */
    private String applicationIds;
    /**
     * 应用名称
     */
    private String applicationNames;
}
