package com.dnt.data.standard.server.model.sys.entity.response;

import com.dnt.data.standard.server.model.sys.entity.DwUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 角色--返回数据对象 <br>
 * @date: 2021/8/17 下午3:25 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwRoleResponse implements Serializable {

    private static final long serialVersionUID = -746918238641779180L;
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 成员人数
     */
    private Integer recordsCount;

    List<DwUser> users;
}
