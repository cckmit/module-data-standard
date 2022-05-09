package com.dnt.data.standard.server.model.standard.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 指标--入参数据实体 <br>
 * @date: 2021/7/19 下午3:50 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("指标请求对象")
public class DwTargetRequest extends PageEntity {
    /**====================基础信息=========================**/
    /**ID**/
    @ApiModelProperty("指标ID")
    private Long id;
    /**分类目录ID**/
    @ApiModelProperty(value = "分类目录ID",required = true)
    private Long categoryId;
    /**指标名称**/
    @ApiModelProperty(value = "指标名称")
    private String name;
    /**时间周期修饰**/
    @ApiModelProperty(value = "时间周期修饰ID")
    private Long timeTargetAttributeId;
    /**时间修饰名称**/
    @ApiModelProperty(value = "时间周期修饰名称")
    private String timeTargetAttributeName;
    /**业务修饰**/
    @ApiModelProperty(value = "业务修饰ID")
    private Long serviceTargetAttributeId;
    /**业务修饰名称**/
    @ApiModelProperty(value = "业务修饰名称")
    private String serviceTargetAttributeName;
    /**原子词修饰**/
    @ApiModelProperty(value = "原子词修饰ID")
    private Long atomTargetAttributeId;
    /**原子词名称**/
    @ApiModelProperty(value = "原子词修饰名称")
    private String atomTargetAttributeName;

    /**指标编号**/
    @ApiModelProperty(value = "指标编号")
    private String code;
    /**别名**/
    @ApiModelProperty(value = "别名")
    private String alias;
    /**来源系统**/
    @ApiModelProperty(value = "来源")
    private String source;
    /**======================域值设置============================**/
    /**数据类型**/
    @ApiModelProperty(value = "类型")
    private Integer type;
    /**长度**/
    @ApiModelProperty(value = "长度")
    private Integer length;
    /** 质量校验函数**/
    @ApiModelProperty(value = "质量校验函数")
    private String checkFunction;
    /**指标业务口径**/
    @ApiModelProperty(value = "指标业务口径")
    private String serviceCaliber;
    /**指标技术口径**/
    @ApiModelProperty(value = "指标技术口径")
    private String technicalCaliber;
}
