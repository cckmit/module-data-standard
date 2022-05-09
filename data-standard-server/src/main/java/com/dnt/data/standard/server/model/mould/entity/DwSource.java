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
 * @description: 来源系统--实体对象 <br>
 * @date: 2021/7/28 上午11:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_source")
@ApiModel("来源系统请求对象")
public class DwSource extends BaseEntity {

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
}
