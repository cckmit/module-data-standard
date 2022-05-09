package com.dnt.data.standard.server.model.sys.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description: 用户--返回数据对象  <br>
 * @date: 2021/8/19 下午1:30 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwUserResponse implements Serializable {
    private static final long serialVersionUID = 2638026457678144593L;

    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 用户信息
     */
    private String userCode;
    /**
     * 中文名称
     */
    private String employName;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 用户状态
     */
    private Integer userStatus;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;

    private List<Map<String,Object>> roles;
}
