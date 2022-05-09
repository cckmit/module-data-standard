package com.dnt.data.standard.server.model.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description:  <br>
 * @date: 2021/8/19 下午2:57 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_user_role_rel")
public class DwUserRoleRel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7311617775764445524L;
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**用户ID**/
    private Long userId;
    /**角色ID**/
    private Long roleId;
}
