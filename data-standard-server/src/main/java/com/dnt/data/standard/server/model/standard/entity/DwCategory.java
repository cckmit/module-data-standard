package com.dnt.data.standard.server.model.standard.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;


/**
 * @description: 数仓业务分类 实体层 <br>
 * @date: 2021/7/8 下午4:43 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_category")
public class DwCategory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3499111789924601071L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long id;
    /**
     * 分类父级ID
     */
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long parentId;
    /**
     * 租户ID
     */
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long tenantId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 分类层级的全路径
     */
    private String path;
    /**
     * 是否为叶子节点
     */
    private Integer isLeaf;
    /**
     * 层级 节点所在的层级
     */
    private Integer level;
    /**
     * 数据类型分类 有多少个模块的目录 可以累加
     */
    private String dwType;
    /**
     * 目录下的数据条数
     */
    private Integer recordsCount;

    @TableField(exist = false)
    private List<DwCategory> childs;

}

