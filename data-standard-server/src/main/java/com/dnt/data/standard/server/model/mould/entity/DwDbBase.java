package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 数据基础库--实体对象 <br>
 * @date: 2021/7/29 上午11:33 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_db_base")
@ApiModel("数据基础库请求对象")
public class DwDbBase extends BaseEntity {
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**分类目录**/
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**基础库名称**/
    private String name;
    /**基础库标识**/
    private String code;
    /**描述**/
    private String description;
    /**表头信息**/
    private String contentHeader;
    /**
     * 数据发布状态 0 未发布1已发布2已更新
     */
    private Integer releaseStatus;
    @TableField(exist = false)
    private String releaseStatusStr;
}
