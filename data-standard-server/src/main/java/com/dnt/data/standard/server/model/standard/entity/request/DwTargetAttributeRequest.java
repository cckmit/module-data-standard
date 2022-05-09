package com.dnt.data.standard.server.model.standard.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 指标属性--入参数据实体 <br>
 * @date: 2021/7/15 下午5:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("指标属性请求对象")
public class DwTargetAttributeRequest extends PageEntity {
    @ApiModelProperty("指标属性ID")
    private Long id;
    /**分类目录ID**/
    @ApiModelProperty(value = "分类目录ID",required = true)
    private Long categoryId;
    /**名称**/
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    /**编号**/
    @ApiModelProperty(value = "指标属性编码",required = true)
    private String code;
    /**类型**/
    @ApiModelProperty(value = "指标属性类型 1 业务类型2原子类型3时间类型",required = true)
    private Integer type;
    /**描述**/
    @ApiModelProperty("描述信息")
    private String description;

}
