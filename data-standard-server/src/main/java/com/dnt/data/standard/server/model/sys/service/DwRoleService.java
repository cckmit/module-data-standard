package com.dnt.data.standard.server.model.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.sys.entity.DwRole;
import com.dnt.data.standard.server.model.sys.entity.request.DwRoleRequest;
import com.dnt.data.standard.server.model.sys.entity.response.DwRoleResponse;

/**
 * @description: 角色-服务接口层 <br>
 * @date: 2021/8/17 下午5:41 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

public interface DwRoleService extends BaseService<DwRole> {
    /**
     * 获取角色分页列表
     * @param request
     * @return
     */
    IPage<DwRole> selectRolePage(DwRoleRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwRoleResponse detailRole(Long id);
}
