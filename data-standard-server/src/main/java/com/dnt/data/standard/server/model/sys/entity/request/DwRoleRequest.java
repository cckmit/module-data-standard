package com.dnt.data.standard.server.model.sys.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @description: 角色--入参数据实体 <br>
 * @date: 2021/8/17 下午6:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@ApiModel("角色请求对象")
public class DwRoleRequest extends PageEntity {

}
