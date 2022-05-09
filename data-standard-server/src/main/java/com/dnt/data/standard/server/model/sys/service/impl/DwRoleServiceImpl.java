package com.dnt.data.standard.server.model.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.sys.entity.request.DwRoleRequest;
import com.dnt.data.standard.server.model.sys.entity.response.DwRoleResponse;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.sys.dao.DwRoleMapper;
import com.dnt.data.standard.server.model.sys.entity.DwRole;
import com.dnt.data.standard.server.model.sys.entity.DwUser;
import com.dnt.data.standard.server.model.sys.service.DwRoleService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 角色-服务接口实现层 <br>
 * @date: 2021/8/17 下午5:41 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwRoleServiceImpl extends BaseServiceImpl<DwRoleMapper, DwRole> implements DwRoleService {
    /**获取角色分页列表**/
    @Override
    public IPage<DwRole> selectRolePage(DwRoleRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwRoleServiceImpl-->selectRolePage 获取角色分页列表");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwRole> page = new Page<>(pn,ps);
        QueryWrapper<DwRole> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .groupBy("r.role_id");
        return baseMapper.selectRolePage(page,q);
    }
    /**查看详情**/
    @Override
    public DwRoleResponse detailRole(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwRoleServiceImpl-->detailRole 查看详情");
        }
        //NO.1 查询角色的基本信息
        DwRole ro = this.baseMapper.selectById(id);

        DwRoleResponse rr = new DwRoleResponse();
        BeanValueTrimUtil.beanValueTrim(ro);
        BeanUtils.copyProperties(ro,rr);
        //NO.2 查询当前角色下的用户信息
        List<DwUser> users = this.baseMapper.selectUsersByRoleId(id);
        if(CollectionUtils.isNotEmpty(users)) {
            rr.setUsers(users);
        }

        return rr;
    }
}
