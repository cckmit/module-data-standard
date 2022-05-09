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
 * @description: 指标属性--实体对象 <br>
 * @date: 2021/7/15 上午11:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_target_attribute")
public class DwTargetAttribute extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8575707257531410474L;
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类目录
     */
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**
     * 名称
     */
    private String name;
    /**
     * 编号
     */
    private String code;
    /**
     * 描述
     */
    private String description="";
    /**
     * 指标类型 1时间类型2业务类型3原子类型
     */
    private Integer type;
}
