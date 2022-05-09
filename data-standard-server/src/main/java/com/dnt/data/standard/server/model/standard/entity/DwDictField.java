package com.dnt.data.standard.server.model.standard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 数据字典关联字段--实体对象 <br>
 * @date: 2021/7/12 下午3:00 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_dict_field")
@ApiModel("数据字典关联字段请求对象")
public class DwDictField extends BaseEntity implements Serializable {
    /**
     * 数据字典关联字段 ID
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 数据字典ID
     */
    @ApiModelProperty("数据字典ID")
    private Long dictId;
    /**
     * 值编码
     */
    @ApiModelProperty("值编码")
    private String keyCode;
    /**
     * 值名称
     */
    @ApiModelProperty("值名称")
    private String keyName;
    /**
     * 值描述
     */
    @ApiModelProperty("描述")
    private String description="";
}
