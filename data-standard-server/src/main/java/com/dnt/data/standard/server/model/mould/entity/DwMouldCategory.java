package com.dnt.data.standard.server.model.mould.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * @description: 模型层级--实体对象 <br>
 * @date: 2021/8/2 下午5:29 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_category")
@ApiModel("模型层级请求对象")
public class DwMouldCategory extends BaseEntity {
    /**分类ID**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long id;
    /**分类父级ID**/
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long parentId;
    /**租户ID**/
    @JSONField(serializeUsing= ToStringSerializer.class)
    private Long tenantId;

    /**分类名称**/
    private String name;
    /**编号**/
    private String code;
    /**描述**/
    private String description;
    /**分类层级的全路径**/
    private String path;
    /**是否为叶子节点  1 为是  0 为否**/
    private Integer isLeaf;
    /**层级 节点所在的层级**/
    private Integer level;

    @TableField(exist = false)
    private List<DwMouldCategory> childs;
}
