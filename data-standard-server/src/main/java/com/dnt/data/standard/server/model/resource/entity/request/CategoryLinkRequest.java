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
 * @description: 分类层级链路--入参对象 <br>
 * @date: 2021/11/10 上午10:33 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("分类层级链路请求对象")
public class CategoryLinkRequest extends PageEntity {

    /**
     * 指定分类层级
     */
    @ApiModelProperty("指定分类层级ID")
    private List<Long> categoryIds;
}
