package com.dnt.data.standard.server.model.resource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 模型资源--实体对象 <br>
 * @date: 2021/10/11 下午5:46 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_resource")
public class DwMouldResource extends BaseEntity  implements Serializable {

    private Long id;
    /**
     * 分类ID
     */
    private Long categoryId;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源目录路径
     */
    private String categoryPath;
    /**
     * 资源目录路径名
     */
    private String categoryPathName;
    /**
     * 数据源ID
     */
    private Long dataSourceTypeId;
    /**
     * 数据源名称
     */
    private String dataSourceTypeName;
    /**
     * 数据源ID
     */
    private Long dataSourceId;
    /**
     * 数据源名称
     */
    private String dataSourceName;
    /**
     * 数据库ID
     */
    private Long dbId;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 负责人
     */
    private String ownerName;
    /**
     * 状态
     */
    private Integer status;

}
