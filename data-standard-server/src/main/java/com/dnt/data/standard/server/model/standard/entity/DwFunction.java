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
 * @description:  函数--实体对象<br>
 * @date: 2021/7/19 上午11:16 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_function")
@ApiModel("函数实体对象")
public class DwFunction extends BaseEntity implements Serializable {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类目录
     */
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**
     * 函数名称
     */
    private String name;
    /**
     * 函数编号
     */
    private String code;
    /**
     * 描述
     */
    private String description;
    /**
     * 选择资源ID
     */
    private Long resourceId=0L;
    /**
     * 选择资源名称
     */
    private String resourceName="默认";
    /**
     * 类名
     */
    private String className;
    /**
     * 物理化状态
     */
    private Integer physicalState;

}
