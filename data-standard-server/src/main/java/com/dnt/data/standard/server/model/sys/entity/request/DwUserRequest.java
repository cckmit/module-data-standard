package com.dnt.data.standard.server.model.sys.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import com.dnt.data.standard.server.model.sys.entity.DwRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 用户--入参数据实体 <br>
 * @date: 2021/8/17 下午6:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户请求对象")
public class DwUserRequest extends PageEntity {

    @ApiModelProperty("用户ID")
    private Long id;

    /**租户ID**/
    @ApiModelProperty("租户ID")
    private Long tenantId;
    /**用户信息**/
    @ApiModelProperty(value = "用户信息",required = true)
    private String userCode;
    /**中文名称**/
    @ApiModelProperty("中文名称")
    private String employName;
    /**密码**/
    @ApiModelProperty("密码")
    private String userPassword;
    /**用户状态**/
    @ApiModelProperty("用户状态")
    private Integer userStatus;
    /**邮箱**/
    @ApiModelProperty("邮箱")
    private String email;
    /**手机号**/
    @ApiModelProperty("手机号")
    private String mobile;

    private List<DwRole> roles;
}
