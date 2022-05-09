package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description: 数据基础库关联字段--实体对象 <br>
 * @date: 2021/9/23 上午11:51 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_db_base_field")
@ApiModel("数据基础库关联对象请求对象")
public class DwDbBaseField extends BaseEntity {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**基础库的ID**/
    private Long dbBaseId;
    private String tableName;
    /**存储的动态数据信息**/
    private String contentData;

}
