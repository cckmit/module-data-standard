package com.dnt.data.standard.server.model.resource.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 资产全景--入参数据实体 <br>
 * @date: 2021/11/8 下午1:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("资产全景请求对象")
public class DwResourcePanoramaRequest extends PageEntity {

    @ApiModelProperty("资产全景ID")
    private Long id;
    @ApiModelProperty("资产全景名称")
    private String name;
    @ApiModelProperty("资产全景状态")
    private Integer status;


}
