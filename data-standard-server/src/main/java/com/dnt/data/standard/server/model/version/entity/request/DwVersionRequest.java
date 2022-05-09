package com.dnt.data.standard.server.model.version.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 版本管理--入参数据实体 <br>
 * @date: 2022/4/14 上午10:00 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("版本管理入参请求对象")
public class DwVersionRequest extends PageEntity {
    /**
     * 版本名称
     */
    @ApiModelProperty("版本管理名称")
    private String versionName;
}
