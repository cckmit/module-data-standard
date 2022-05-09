package com.dnt.data.standard.server.model.mould.entity;

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
 * @description: 公共字段模型--实体类 <br>
 * @date: 2021/7/29 下午3:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_public_mould")
@ApiModel("公共字段模型请求对象")
public class DwPublicMould extends BaseEntity {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**分类目录**/
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    /**名称**/
    private String name;
    /**描述信息**/
    private String description;
    /**字段数量**/
    private Integer recordCount;

    @TableField(exist = false)
    List<DwPublicMouldField> fields;

}
