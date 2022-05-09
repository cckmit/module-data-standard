package com.dnt.data.standard.server.model.standard.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 数据元--入参数据实体 <br>
 * @date: 2021/7/21 下午3:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("数据元请求对象")
public class DwDataElementRequest extends PageEntity {
    /**====================基础信息=========================**/
    @ApiModelProperty("数据元ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty("数据元分类目录ID")
    private Long categoryId;
    /**数据元名称**/
    @ApiModelProperty(value = "数据源名称",required = true)
    private String name;
    /**标识编码**/
    @ApiModelProperty(value = "数据源编码",required = true)
    private String code;
    /**数据元别名**/
    @ApiModelProperty("别名")
    private String alias;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**======================域值设置============================**/
    /**数据类型**/
    @ApiModelProperty("类型ID")
    private Integer typeId;
    /**长度**/
    @ApiModelProperty("长度")
    private Integer length;
    /**业务规则**/
    @ApiModelProperty("业务规则")
    private String businessRules;
    /**引用数据字典**/
    @ApiModelProperty("数据字典ID")
    private Long dictId;
    @ApiModelProperty("数据字典名称")
    private String dictName;
    /**自定义质量规则**/
    @ApiModelProperty("自定义质量规则")
    private String customerRules;


}
