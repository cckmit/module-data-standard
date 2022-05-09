package com.dnt.data.standard.server.model.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.sys.entity.DwUser;
import com.dnt.data.standard.server.model.sys.entity.request.DwUserRequest;
import com.dnt.data.standard.server.model.sys.entity.response.DwUserResponse;

/**
 * @description: 用户-服务接口层 <br>
 * @date: 2021/8/17 下午5:41 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

public interface DwUserService extends BaseService<DwUser> {
    /**
     * 获取用户分页列表
     * @param request
     * @return
     */
    IPage<DwUser> selectUserPage(DwUserRequest request);

    /**
     * 查看用户详情
     * @param id
     * @return
     */
    DwUserResponse detailUser(Long id);

    /**
     * 添加用户信息
     * @param user
     * @param userCode
     * @return
     */
    R saveUser(DwUserRequest user, String userCode);

    /**
     * 修改用户信息
     * @param request
     * @param userCode
     * @return
     */
    R updateUser(DwUserRequest request, String userCode);

    /**
     * 删除用户信息
     * @param id
     * @param userCode
     * @return
     */
    int deleteUser(Long id, String userCode);
}
