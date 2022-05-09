package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 所属应用--入参对象 <br>
 * @date: 2021/7/28 下午1:37 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("所属应用请求对象")
public class DwApplicationRequest extends PageEntity {
    private Long id;
    /**分类目录**/
    @ApiModelProperty("分类目录ID")
    private Long categoryId;
    /**名称**/
    @ApiModelProperty("应用名称")
    private String name;
    /**编号**/
    @ApiModelProperty("应用标识")
    private String code;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
}
