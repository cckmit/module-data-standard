package com.dnt.data.standard.server.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 分页实体 <br>
 * @date: 2022/2/10 上午11:23 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class PageEntity implements Serializable {
    /**======================通用分页============================**/
    @ApiModelProperty("当前页码")
    private Integer pageNum=1;
    /**每页显示的记录数**/
    @ApiModelProperty("每页显示的记录数")
    private Integer pageSize=10;
    @ApiModelProperty("项目ID")
    private Long projectId;
}
