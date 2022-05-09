package com.dnt.data.standard.server.model.resource.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 模型资源发布--入参数据实体 <br>
 * @date: 2021/10/14 下午3:16 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型资源发布请求对象")
public class DwMouldResourceReleaseRequest extends PageEntity {
    @ApiModelProperty("资源ID")
    private Long id;
    /**分类ID**/
    @ApiModelProperty(value = "分类ID",required = true)
    private Long categoryId;
    /**类型**/
    @ApiModelProperty(value = "类型1 数据库 2 指标 ",required = true)
    private Integer type;
    /**资源名称**/
    @ApiModelProperty("资源名")
    private String name;
    /**资源目录路径**/
    @ApiModelProperty("资源目录全路径ID")
    private String categoryPath;
    /**资源目录路径名**/
    @ApiModelProperty("资源目录全路径名称")
    private String categoryPathName;
    /**数据源ID**/
    @ApiModelProperty("数据源类型ID")
    private Long dataSourceTypeId;
    /**数据源名称**/
    @ApiModelProperty("数据源类型名称")
    private String dataSourceTypeName;
    /**数据源ID**/
    @ApiModelProperty("数据源ID")
    private Long dataSourceId;
    /**数据源名称**/
    @ApiModelProperty("数据源名称")
    private String dataSourceName;
    /**数据库ID**/
    @ApiModelProperty("数据库ID")
    private Long dbId;
    /**数据库名称**/
    @ApiModelProperty("数据库名称")
    private String dbName;
    /**负责人**/
    @ApiModelProperty("负责人")
    private String ownerName;
    /**状态**/
    @ApiModelProperty("资产发布状态 0已下线,1已发布")
    private Integer status;
}
