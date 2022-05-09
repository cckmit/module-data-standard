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
 * @description: 模型命名属性--实体对象 <br>
 * @date: 2021/7/27 上午11:59 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_name_attribute")
@ApiModel("模型命令属性请求对象")
public class DwMouldNameAttribute extends BaseEntity {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**分类目录**/
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**名称**/
    private String name;
    /**编号**/
    private String code;
    /**描述**/
    private String description;
    /**类型 1 刷新频率  2 增量定义 3 统计周期  此字段不能为空**/
    private Integer type;
}
