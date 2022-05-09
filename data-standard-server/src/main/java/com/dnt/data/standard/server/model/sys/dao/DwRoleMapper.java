package com.dnt.data.standard.server.model.sys.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.sys.entity.DwRole;
import com.dnt.data.standard.server.model.sys.entity.DwUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @description: 角色-数据库实现层 <br>
 * @date: 2021/8/17 下午5:39 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwRoleMapper extends BaseDao<DwRole> {

    /**
     * 根据roleId获取对应的用户信息
     * @param roleId
     * @return
     */
    @Select("<script>" +
            "  select b.id,b.user_code,b.employ_name,b.email,b.mobile,b.user_status" +
            "  from dw_user_role_rel a " +
            "  left join dw_user b on a.user_id = b.id and b.delete_model=1" +
            "  where a.delete_model=1 and a.role_id=#{roleId}" +
            "</script>")
    List<DwUser> selectUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 角色分页管理
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select  a.id,a.name,a.description,a.create_time as createTime,a.create_user as createUser,count(r.role_id) as recordsCount,a.delete_model as deleteModel " +
            " from dw_role a " +
            " left join dw_user_role_rel r on a.id=r.role_id and r.delete_model=1 " +
            " ${ew.customSqlSegment} " +
            "</script>")
    IPage<DwRole> selectRolePage(Page<DwRole> page,
                                 @Param(Constants.WRAPPER) QueryWrapper<DwRole> q);
}
