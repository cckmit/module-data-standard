package com.dnt.data.standard.server.model.standard.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 业务分类--入参数据实体 <br>
 * @date: 2021/7/8 下午5:43 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("业务分类请求对象")
public class DwCategoryRequest extends PageEntity {

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
    /**分类类型**/
    @ApiModelProperty(value = "分类类型",required = true)
    private String dwType;

}
