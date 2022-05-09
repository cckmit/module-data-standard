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
 * @description: 数据字典--实体对象 <br>
 * @date: 2021/7/12 上午11:46 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_dict")
public class DwDict extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5311461778479169994L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类目录
     */
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**
     * 集群名称
     */
    private String name;
    /**
     * 编号
     */
    private String code;
    /**
     * 别名
     */
    private String alias;
    /**
     * 描述
     */
    private String description;
    /**
     * 数据发布状态 0 未发布1已发布2已更新
     */
    private Integer releaseStatus;
    @TableField(exist = false)
    private String releaseStatusStr;
}
