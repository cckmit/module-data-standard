package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 模型命名属性--入参对象 <br>
 * @date: 2021/7/27 下午1:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型命名属性请求对象")
public class DwMouldNameAttributeRequest extends PageEntity {

    private static final long serialVersionUID = 2865390445885489820L;

    @ApiModelProperty("模型命名属性ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty(value = "模型命名属性分类目录ID",required = true)
    private Long categoryId;
    /**名称**/
    @ApiModelProperty("模型命名属性名称")
    private String name;
    /**编号**/
    @ApiModelProperty("编号")
    private String code;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**类型 1 刷新频率  2 增量定义 3 统计周期  此字段不能为空**/
    @ApiModelProperty(value = "类型 1 刷新频率  2 增量定义 3 统计周期" ,required = true)
    private Integer type;

}
