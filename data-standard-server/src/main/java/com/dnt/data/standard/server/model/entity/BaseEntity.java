package com.dnt.data.standard.server.model.entity;

import cn.hutool.core.annotation.Alias;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @description: 通用的基础字段 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
public class BaseEntity {
    /**
     * 项目ID
     */
    @Alias("项目ID")
    private Long projectId;
    /**
     * 创建人
     */
    @Alias("创建人")
    private String createUser;
    /**
     * 修改人
     */
    @Alias("更新人")
    private String updateUser;
    /**
     * 创建时间
     */
    @JSONField(format ="yyyy-MM-dd HH:mm:ss")
    @Alias("创建时间")
    private Date createTime;
    /**
     * 修改时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Alias("更新时间")
    private Date updateTime;
    /**
     * 是否删除 0:删除 1:正常
     */
    @Alias("删除标识")
    private Integer deleteModel;

}
