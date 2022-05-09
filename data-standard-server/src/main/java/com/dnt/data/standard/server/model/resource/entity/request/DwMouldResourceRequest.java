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
 * @description:模型资源--入参数据实体  <br>
 * @date: 2021/10/12 上午10:27 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型资源请求对象")
public class DwMouldResourceRequest extends PageEntity {

    @ApiModelProperty(value = "模型资源ID" )
    private Long id;
    @ApiModelProperty(value = "批量操作模型资源的ID" )
    private List<Long> ids;
    /**分类目录ID**/
    @ApiModelProperty(value = "分类目录ID" )
    private Long categoryId;

    /**分类目录ID**/
    @ApiModelProperty(value = "数据源ID" )
    private Long projectId;

    /**分类目录ID**/
    @ApiModelProperty(value = "数据库ID" )
    private Long dbId;

    /**类型**/
    @ApiModelProperty(value = "类型 1 数据表 2 指标")
    private Integer type;
    /**状态**/
    @ApiModelProperty(value = "状态  0已下线 1已发布 ")
    private Integer status;

    @ApiModelProperty(value = "搜索内容")
    private String searchContent;
}
