package com.dnt.data.standard.server.model.resource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 模型资源操作记录--实体对象 <br>
 * @date: 2021/11/5 上午8:58 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_resource_operator_log")
public class DwMouldResourceOperatorLog  extends BaseEntity implements Serializable {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 资源ID
     */
    private Long resourceId;
    /**
     * 库ID
     */
    private Long dbId;
    /**
     * 库名
     */
    private String dbName;
    /**
     * 表ID
     */
    private Long tableId;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 操作人
     */
    private String operatorUser;
    /**
     * 操作标识
     */
    private String operatorFlag;
    /**
     * 搜索内容
     */
    private String searchContent;

}
