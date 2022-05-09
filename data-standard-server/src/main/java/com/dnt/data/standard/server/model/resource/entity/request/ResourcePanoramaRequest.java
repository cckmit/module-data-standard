package com.dnt.data.standard.server.model.resource.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 资产全景--入参数对象 <br>
 * @date: 2021/11/10 下午4:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("资产全景入参")
public class ResourcePanoramaRequest extends PageEntity {
    @ApiModelProperty("资产全景ID")
    private Long id;
    @ApiModelProperty("资产全景名称")
    private String name;
    @ApiModelProperty("状态 1 已上线  0 待上线 默认为0 待上线")
    private Integer status;
    /**智能数仓-工作组ID**/
    @ApiModelProperty("工作组ID")
    private Long groupId;
    /**智能数仓-工作组名称**/
    @ApiModelProperty("工作组名称")
    private String groupName;

    /**模型链路配置 顶级分类目录**/
    @ApiModelProperty("模型顶级分类目录ID集合")
    private List<Long> mouldTopCategoryIds;
    /**模型链路配置 顶级分类目录名称**/
    @ApiModelProperty("模型顶级分类目录名称集合")
    private List<String> mouldTopCategoryNames;
    /**业务数据来源ID**/
    @ApiModelProperty("业务数据来源ID集合")
    private List<Long> sourceIds;
    /**业务数据来源名称**/
    @ApiModelProperty("业务数据来源名称集合")
    private List<String> sourceNames;
    /**模型链路ID**/
    @ApiModelProperty("模型链路ID")
    private Long linkCategoryId;
    /**模型链路名称**/
    @ApiModelProperty("模型链路名称")
    private String linkCategoryName;
    /**应用ID**/
    @ApiModelProperty("应用ID集合")
    private List<Long> applicationIds;
    /**应用名称**/
    @ApiModelProperty("应用名称集合")
    private List<String> applicationNames;
}
