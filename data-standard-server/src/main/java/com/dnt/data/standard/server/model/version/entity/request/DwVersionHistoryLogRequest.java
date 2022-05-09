package com.dnt.data.standard.server.model.version.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 发布历史版本日志--入参数据实体 <br>
 * @date: 2022/4/24 上午11:04 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("发布历史版本日志入参请求对象")
public class DwVersionHistoryLogRequest extends PageEntity {
    @ApiModelProperty(value = "模块类型 data_element:数据源 dict:数据字典 target:数据指标 mould_name:模型命名 db_base:数据基础库",required = true)
    private String mouldType;
    @ApiModelProperty(value = "操作标识 update更新 insert 插入 release 发布",required = true)
    private String operationFlag;
    @ApiModelProperty(value = "数据信息ID",required = true)
    private Long dataId;

}
