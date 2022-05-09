package com.dnt.data.standard.server.model.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 角色--实体对象 <br>
 * @date: 2021/8/17 上午11:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_role")
public class DwRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4013932922229141663L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 成员人数
     */
    private Integer recordsCount;
}
