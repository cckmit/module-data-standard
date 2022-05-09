package com.dnt.data.standard.server.model.mould.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description:模型物理化--实体对象  <br>
 * @date: 2021/8/24 上午10:29 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_mould_physics")
@ApiModel("模型物理化请求对象")
public class DwMouldPhysics extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 228378628627564389L;

    /**模型ID**/
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**存储资源 dev 开发环境 test 测试环境 prod 生产环境**/
    private String envFlag;
    /**数据源类型ID**/
    private Long dataSourceId;
    /** 项目ID**/
    private Long projectId;
    /**项目名称**/
    private String projectName;
    /**库名**/
    private String dbName;
    /** 模型ID**/
    private Long mouldId;
    /**模型名称**/
    private String mouldName;
    /**hive配置json**/
    private String hiveJson;
    /**项目描述信息**/
    private String description;

}
