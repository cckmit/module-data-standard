package com.dnt.data.standard.server.model.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 用户--实体对象 <br>
 * @date: 2021/8/17 上午11:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_user")
public class DwUser extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -4762936924959771632L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**租户ID**/
    private Long tenantId;
    /**用户信息**/
    private String userCode;
    /**中文名称**/
    private String employName;
    /**密码**/
    private String userPassword;
    /**用户状态**/
    private Integer userStatus;
    /**邮箱**/
    private String email;
    /**手机号**/
    private String mobile;

    /**角色名称**/
    @TableField(exist = false)
    private Long roleId;
    @TableField(exist = false)
    private String roleName;

}
