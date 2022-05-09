package com.dnt.data.standard.server.model.resource.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 操作记录--入参数对象 <br>
 * @date: 2021/11/5 上午8:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("操作记录请求对象")
public class OperatorRequest extends PageEntity {
    @ApiModelProperty("发布资源的ID")
    private Long resourceId;
    @ApiModelProperty("搜索框中输入的内容")
    private String searchContent;
    @ApiModelProperty("操作标识：search 搜索 show 查看")
    private String operatorFlag;
}
