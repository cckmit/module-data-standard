package com.dnt.data.standard.server.model.sys.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.sys.entity.DwUser;
import com.dnt.data.standard.server.model.sys.entity.DwUserRoleRel;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 用户-数据库实现层 <br>
 * @date: 2021/8/17 下午5:39 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwUserMapper extends BaseDao<DwUser> {
    /**
     * 查看当前用户下的 用户角色信息
     *
     * @param userId
     * @return
     */
    @Select("<script>" +
            "  select b.id,b.name" +
            "  from dw_user_role_rel a " +
            "  left join dw_role b on a.role_id = b.id and b.delete_model=1" +
            "  where a.delete_model=1 and a.user_id=#{userId}" +
            "</script>")
    List<Map<String, Object>> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 批量删除当前用户下的角色
     *
     * @param userId
     * @param userCode
     * @return
     */
    @Update("<script>" +
            " update dw_user_role_rel set delete_model=0,update_user=#{userCode},update_time=now() " +
            " where delete_model=1 and user_id=#{userId} " +
            "</script>")
    int deleteRoleByUserId(@Param("userId") Long userId,
                           @Param("userCode") String userCode);

    /**
     * 批量添加用户的角色
     *
     * @param roleIds
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_user_role_rel` " +
            "   (`id`, `user_id`, `role_id`, `create_user`, `create_time`) " +
            "  VALUES" +
            "  <foreach collection=\"roleIds\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\"> " +
            "   (#{item.id}, #{item.userId}, #{item.roleId},#{item.createUser}, #{item.createTime})" +
            "  </foreach> " +
            " </script>")
    int insertUserRoleBatch(@Param("roleIds") List<DwUserRoleRel> roleIds);

    /**
     * 根据指定的字段查询是否重复
     *
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Override
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_user  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /**
     * 分页查询用户角色
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.user_code,a.employ_name,a.email,a.mobile,r.id as roleId,r.name as roleName,a.create_user,a.create_time,a.delete_model" +
            " from dw_user a " +
            " left join dw_user_role_rel ur on a.id = ur.user_id and ur.delete_model=1 " +
            " left join dw_role r on ur.role_id = r.id and r.delete_model=1 " +
            " ${ew.customSqlSegment} " +
            "</script>")
    IPage<DwUser> selectUserRolePage(Page<DwUser> page,
                                     @Param(Constants.WRAPPER) QueryWrapper<DwUser> q);
}
