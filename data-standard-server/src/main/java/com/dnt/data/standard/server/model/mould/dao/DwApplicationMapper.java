package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.entity.DwApplication;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description: 所属应用--数据库实现层 <br>
 * @date: 2021/7/28 下午1:01 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwApplicationMapper extends BaseDao<DwApplication> {
    /**获取所属应用分页列表**/
    @Select("<script>" +
            " select a.id,a.category_id,a.code,a.name,a.description,a.create_user,a.create_time " +
            " from dw_application a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwApplication> selectApplicationPage(Page<DwApplication> page,
                                               @Param(Constants.WRAPPER) QueryWrapper<DwApplication> q);

    /**根据指定的字段查询是否重复**/
    @Override
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_application  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);
}
