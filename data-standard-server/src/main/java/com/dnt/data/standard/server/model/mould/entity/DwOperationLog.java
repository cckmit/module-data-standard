package com.dnt.data.standard.server.model.mould.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description: 业务操作日志--实体对象 <br>
 * @date: 2021/8/12 上午11:50 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_operation_log")
public class DwOperationLog extends BaseEntity {


    /**API接口ID**/
    @JSONField(serializeUsing= ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**批次号**/
    private Long lotNumber;
    /**平台信息**/
    private String platform;
    /**日志的标题某功能模块记录的日志**/
    private String title;
    /**日志类型 dubuge info warn error**/
    private String type;
    private Integer mouldFlag;
    /**指定业务ID**/
    private Long keyId;
    /**请求ip**/
    private String ip;
    /**请求的URL**/
    private String requestUrl;
    /**协议**/
    private String protocol;
    /**协议版本**/
    private String protocolVersion;
    /**class 类名**/
    private String className;
    /**方法名**/
    private String methodName;
    /**调用方标识**/
    private String callFlag;
    /**运行耗时**/
    private Long timeConsuming;
    /**操作时间**/
    @JSONField(format ="yyyy-MM-dd HH:mm:ss")
    private Date operationTime;
    /**入参数信息**/
    private String requestParam;
    /**调用接口结果描述**/
    private String resultDesc;
    /**接口操作状态**/
    private Integer status;
    /**日志操作顺序**/
    private Integer logOrder;
}
