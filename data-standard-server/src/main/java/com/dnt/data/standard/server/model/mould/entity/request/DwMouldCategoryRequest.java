package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型业务分类请求对象")
public class DwMouldCategoryRequest extends PageEntity {

    private static final long serialVersionUID = 8073928903041680419L;
    @ApiModelProperty("分类ID")
    private Long id;
    /**分类父级ID**/
    @ApiModelProperty("父分类ID")
    private Long parentId;
    /**租户ID**/
    @ApiModelProperty("租户ID")
    private Long tenantId;
    /**分类名称**/
    @ApiModelProperty(value = "分类名称",required = true)
    private String name;
    @ApiModelProperty(value = "编号")
    private String code;
    @ApiModelProperty(value = "描述")
    private String description;

}