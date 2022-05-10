package com.dnt.data.standard.server.model.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 数据元--实体对象 <br>
 * @date: 2021/7/21 下午2:59 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_data_element")
public class DwDataElement extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8721043793788627809L;

    /**====================基础信息=========================**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类目录
     */
    private Long categoryId;
    /**
     * 数据元名称
     */
    private String name;
    /**
     * 标识编码
     */
    private String code;
    /**
     * 数据元别名
     */
    private String alias;
    /**
     * 描述
     */
    private String description;
    /**======================域值设置============================**/
    /**
     * 数据类型  1 整数型  2 浮点型
     */
    private Integer typeId;
    @TableField(exist = false)
    private String typeName="";
    /**
     * 长度
     */
    private Integer length;
    /**
     * 业务规则
     */
    private String businessRules;
    /**
     * 引用数据字典
     */
    private Long dictId;
    private String dictName;
    /**
     * 自定义质量规则
     */
    private String customerRules;
    /**
     * 数据发布状态 0 未发布1已发布2已更新
     */
    private Integer releaseStatus;
    @TableField(exist = false)
    private String releaseStatusStr;
}
