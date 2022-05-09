package com.dnt.data.standard.server.model.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 指标--实体对象 <br>
 * @date: 2021/7/19 下午2:53 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_target")
@ApiModel("指标实体对象")
public class DwTarget extends BaseEntity implements Serializable {
    /**====================基础信息=========================**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类目录
     */
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**
     * 时间周期修饰
     */
    private Long timeTargetAttributeId;
    /**
     * 时间修饰名称
     */
    private String timeTargetAttributeName;
    /**
     * 业务修饰
     */
    private Long serviceTargetAttributeId;
    /**
     * 业务修饰名称
     */
    private String serviceTargetAttributeName;
    /**
     * 原子词修饰
     */
    private Long atomTargetAttributeId;
    /**
     * 原子词名称
     */
    private String atomTargetAttributeName;
    /**
     * 指标名称 来源
     */
    private String name;
    /**
     * 指标编号
     */
    private String code;
    /**
     * 别名
     */
    private String alias;
    /**
     * 来源系统
     */
    private String source;
    /**=====================域值设置============================**/
    /**
     * 数据类型
     */
    private Integer type;
    /**
     * 长度
     */
    private Integer length;
    /**
     * 质量校验函数
     */
    private String checkFunction;
    /**
     * 指标业务口径
     */
    private String serviceCaliber;
    /**
     * 指标技术口径
     */
    private String technicalCaliber;
    /**
     * 数据发布状态 0 未发布1已发布2已更新
     */
    private Integer releaseStatus;
    @TableField(exist = false)
    private String releaseStatusStr;
}
