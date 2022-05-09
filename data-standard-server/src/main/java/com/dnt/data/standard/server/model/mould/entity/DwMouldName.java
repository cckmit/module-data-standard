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
 * @description: 模型命名规则--实体对象 <br>
 * @date: 2021/8/2 下午2:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_name")
@ApiModel("模型命名规则请求对象")
public class DwMouldName extends BaseEntity {
    /**=========================第一页==================================**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类目录
     */
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**
     * 基础库名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**=========================第二页==================================**/
    /**
     * 数据模型命名规范
     */
    private Long designTypeId;
    private String designTypeName;
    private Long catalogueId;
    private String catalogueName;
    /**=========================第三页==================================**/

    private String selectItem;
    private String selectItemName;
    /**
     * 生成预览(模型命名)
     */
    private String mouldName;
    /**
     * 数据发布状态 0 未发布1已发布2已更新
     */
    private Integer releaseStatus;
    @TableField(exist = false)
    private String releaseStatusStr;
}
