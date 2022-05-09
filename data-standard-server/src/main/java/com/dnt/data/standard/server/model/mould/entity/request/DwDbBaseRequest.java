package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 数据基础库--入参对象 <br>
 * @date: 2021/7/29 上午11:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("数据基础库请求对象")
public class DwDbBaseRequest extends PageEntity {
    private Long id;
    /**基础库分类ID**/
    @ApiModelProperty("数据基础库分类ID")
    private Long categoryId;
    /**基础库名称**/
    @ApiModelProperty("数据基础库名称")
    private String name;
    /**基础标识**/
    @ApiModelProperty("数据基础标识")
    private String code;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**表头信息**/
    @ApiModelProperty("表头信息")
    private String contentHeader;

}
