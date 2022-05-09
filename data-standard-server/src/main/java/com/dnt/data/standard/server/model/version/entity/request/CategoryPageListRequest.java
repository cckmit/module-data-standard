package com.dnt.data.standard.server.model.version.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: CategoryPageListRequest <br>
 * @date: 2022/4/15 下午4:13 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("版本管理入参请求对象")
public class CategoryPageListRequest extends PageEntity {
    @ApiModelProperty("分类对应的数据目录类型")
    private String dwType;
    @ApiModelProperty("分类目录ID")
    private Long categoryId;
}
