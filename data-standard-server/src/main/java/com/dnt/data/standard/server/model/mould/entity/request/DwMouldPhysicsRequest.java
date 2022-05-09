package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 模型物理化--入参对象 <br>
 * @date: 2021/8/12 上午11:33 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型物理化入参对象")
public class DwMouldPhysicsRequest extends PageEntity {

    private static final long serialVersionUID = 3150553932791711141L;

    @ApiModelProperty("模型ID")
    private Long id;
    @ApiModelProperty("存储资源 dev 开发环境 test 测试环境 prod 生产环境")
    private String envFlag;
    @ApiModelProperty("数据源类型ID")
    private Long dataSourceId;
}
