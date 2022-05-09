package com.dnt.data.standard.server.model.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.sys.dao.DwRoleMapper;
import com.dnt.data.standard.server.model.sys.dao.DwUserMapper;
import com.dnt.data.standard.server.model.sys.entity.DwRole;
import com.dnt.data.standard.server.model.sys.entity.DwUser;
import com.dnt.data.standard.server.model.sys.entity.DwUserRoleRel;
import com.dnt.data.standard.server.model.sys.entity.request.DwUserRequest;
import com.dnt.data.standard.server.model.sys.entity.response.DwUserResponse;
import com.dnt.data.standard.server.model.sys.service.DwUserService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @description: 用户-服务接口实现层 <br>
 * @date: 2021/8/17 下午5:41 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwUserServiceImpl extends BaseServiceImpl<DwUserMapper, DwUser> implements DwUserService {
    @Autowired
    private DwRoleMapper dwRoleMapper;
    /**获取用户分页列表**/
    @Override
    public IPage<DwUser> selectUserPage(DwUserRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwUserServiceImpl-->selectUserPage 获取用户分页列表");
        }


        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwUser> page = new Page<>(pn,ps);
        QueryWrapper<DwUser> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(StringUtils.isNotEmpty(request.getUserCode()),"a.user_code",request.getUserCode());
        IPage<DwUser> pageU = baseMapper.selectUserRolePage(page,q);


        //所有的业务角色
        QueryWrapper<DwRole> qr = Wrappers.query();
        qr.eq("delete_model",1);
        List<DwRole> roleList = this.dwRoleMapper.selectList(qr);
        for (DwUser u : pageU.getRecords()) {
            Long rid = u.getRoleId();
            String roleName = "";
            List<String> roleStrList = roleList.stream()
                    .filter(uu->rid.equals(uu.getId()))
                    .map(DwRole::getName)
                    .collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(roleStrList)){
                roleName= roleStrList.get(0);
            }
            u.setRoleName(roleName);
        }
        return pageU;
    }
    /**查看用户详情**/
    @Override
    public DwUserResponse detailUser(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwUserServiceImpl-->detailUser 查看用户详情");
        }
        //NO.1 查询基础的用户信息
        DwUser du = this.baseMapper.selectById(id);
        DwUserResponse ur = new DwUserResponse();
        BeanValueTrimUtil.beanValueTrim(du);
        BeanUtils.copyProperties(du,ur);
        //NO.2 查询用户下的角色
        List<Map<String,Object>> roles = this.baseMapper.selectRolesByUserId(id);
        if(CollectionUtils.isEmpty(roles)){
            roles = new ArrayList<>();
        }
        ur.setRoles(roles);
        return ur;
    }
    /**添加用户信息**/
    @Override
    public R saveUser(DwUserRequest user,String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwUserServiceImpl-->saveUser 添加用户信息 ");
        }
        //NO.1 数据校验
        if(StringUtils.isEmpty(user.getUserCode())){
            return Result.fail("添加用户时，用户账号不能为空");
        }
        //邮箱
        if(StringUtils.isEmpty(user.getEmail())){
            return Result.fail("添加用户时，邮箱不能为空");
        }
        //手机号
        if(StringUtils.isEmpty(user.getMobile())){
            return Result.fail("添加用户时，手机号不能为空");
        }
        //角色
        if(CollectionUtils.isEmpty(user.getRoles())) {
            return Result.fail("添加用户时，角色信息不能为空");
        }

        List<DwUser> lists = findUserByUserCode(user.getUserCode());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加用户时，用户账号已存在");
        }
        //NO.2构建数据
        DwUser u = new DwUser();
        BeanValueTrimUtil.beanValueTrim(user);
        BeanUtils.copyProperties(user,u);
        u.setCreateUser(userCode);
        u.setCreateTime(new Date());
        //NO.3 添加用户
        int ii = this.baseMapper.insert(u);
        List<DwRole> roles = user.getRoles();
        int ik = 0;
        Long uid = u.getId();
        log.info("用户添加成功，当前用户id为{}",uid);
        if(CollectionUtils.isNotEmpty(roles)){
            //NO.4 删除当前用户下的角色信息
            int dur = this.baseMapper.deleteRoleByUserId(uid,userCode);
            log.info("删除了当前用户ID为{} 的{}条角色信息",uid,dur);
            //NO.5 添加用户下的角色信息
            List<DwUserRoleRel> roleIds = new ArrayList<>();
            roles.forEach(r->{
                DwUserRoleRel urRel = new DwUserRoleRel();
                urRel.setId(IdWorker.getId());
                Long rid = r.getId();
                if(Optional.fromNullable(rid).isPresent()){
                    urRel.setRoleId(rid);
                    urRel.setUserId(uid);
                    urRel.setCreateUser(userCode);
                    urRel.setCreateTime(new Date());
                    roleIds.add(urRel);
                }
            });

            int iur = this.baseMapper.insertUserRoleBatch(roleIds);
            log.info("批量添加了{}条角色",iur);
        }
        return Result.ok(String.format("成功添加用户%s条数据\n 添加角色%s条数据",ii,ik));
    }

    /**
     * 根据 用户账号 查询当前用户编号是不是存在
     * @param userCode
     * @return
     */
    private List<DwUser> findUserByUserCode(String userCode) {
        QueryWrapper<DwUser> q = Wrappers.query();
        q.select("id,user_code").eq("delete_model",1)
                .eq("user_code",userCode);
        return this.baseMapper.selectList(q);
    }

    /**修改用户信息**/
    @Override
    public R updateUser(DwUserRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwUserServiceImpl-->updateUser 修改用户信息 ");
        }
        Long uid = request.getId();
        //NO.1 数据校验
        if(!Optional.fromNullable(uid).isPresent()){
            return Result.fail("编辑用户时，用户ID不能为空");
        }
        if(StringUtils.isEmpty(request.getUserCode())){
            return Result.fail("编辑用户时，用户账号不能为空");
        }

        List<DwUser> userList = findUserByUserCode(request.getUserCode());
        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(userList)){
            //判断名称是否存在
            userList.forEach(na->{
                if(na.getId().longValue()!=uid.longValue()){
                    haveNa.set(true);
                }

            });
        }

        if(haveNa.get()){
            return Result.fail("编辑用户时，用户编号已存在");
        }


        //NO.2构建数据
        DwUser u = new DwUser();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,u);
        u.setUpdateUser(userCode);
        u.setUpdateTime(new Date());
        //NO.3 添加用户
        int ii = this.baseMapper.updateById(u);
        int ik = 0;
        List<DwRole> roles = request.getRoles();
        //NO.4 删除当前用户下的角色信息
        int dur = this.baseMapper.deleteRoleByUserId(uid,userCode);
        log.info("删除了当前用户ID为{} 的{}条角色信息",uid,dur);

        if(CollectionUtils.isNotEmpty(roles)){
            //NO.5 添加用户下的角色信息
            List<DwUserRoleRel> roleIds = new ArrayList<>();
            roles.forEach(r->{
                DwUserRoleRel urRel = new DwUserRoleRel();
                urRel.setId(IdWorker.getId());
                Long rid = r.getId();
                if(Optional.fromNullable(rid).isPresent()){
                    urRel.setRoleId(rid);
                    urRel.setUserId(uid);
                    urRel.setCreateUser(userCode);
                    urRel.setCreateTime(new Date());
                    roleIds.add(urRel);
                }
            });

            int iur = this.baseMapper.insertUserRoleBatch(roleIds);
            log.info("批量添加了{}条角色",iur);
        }
        return Result.ok(String.format("成功编辑用户%s条数据\n 添加角色%s条数据",ii,ik));

    }
    /**删除用户信息**/
    @Override
    public int deleteUser(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwUserServiceImpl-->deleteUser 删除用户信息 ");
        }
        //NO.1构建数据
        DwUser u = new DwUser();
        u.setId(id);
        u.setDeleteModel(0);
        u.setUpdateUser(userCode);
        u.setUpdateTime(new Date());
        //NO.2删除用户与角色的关系 表数据
        this.baseMapper.deleteRoleByUserId(id,userCode);

        //删除操作
        return baseMapper.updateById(u);
    }
}
