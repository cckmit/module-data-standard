package com.dnt.data.standard.server.model.sys.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 数据源--入参数据实体 <br>
 * @date: 2021/8/18 下午6:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("数据源请求对象")
public class DwDataSourceRequest extends PageEntity {

    @ApiModelProperty("数据源名称")
    private String dataName;
}
