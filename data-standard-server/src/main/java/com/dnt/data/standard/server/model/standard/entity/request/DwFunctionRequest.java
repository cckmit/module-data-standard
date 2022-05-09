package com.dnt.data.standard.server.model.standard.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 函数--入参数据实体 <br>
 * @date: 2021/7/19 下午3:50 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("函数请求对象")
public class DwFunctionRequest extends PageEntity {

    /**ID**/
    @ApiModelProperty("函数ID")
    private Long id;
    /**分类目录ID**/
    @ApiModelProperty(value = "分类目录ID",required = true)
    private Long categoryId;
    /**函数名称**/
    @ApiModelProperty(value = "函数名称",required = true)
    private String name;

    /**函数编号**/
    @ApiModelProperty(value = "函数编号",required = true)
    private String code;
    /**描述**/
    @ApiModelProperty(value = "函数描述信息")
    private String description;
    /**选择资源ID**/
    @ApiModelProperty(value = "资源ID")
    private Long resourceId;
    /**选择资源名称**/
    @ApiModelProperty(value = "资源名称")
    private String resourceName;
    /**类名**/
    @ApiModelProperty(value = "类名")
    private String className;

}
