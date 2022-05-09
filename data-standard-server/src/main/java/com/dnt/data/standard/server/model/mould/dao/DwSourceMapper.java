package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.entity.DwSource;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description: 来源系统--数据库实现层 <br>
 * @date: 2021/7/28 下午1:01 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwSourceMapper extends BaseDao<DwSource> {
    /**
     * 获取数来源系统分页列表
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.code,a.name,a.description,a.create_user,a.create_time " +
            " from dw_source a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwSource> selectSourcePage(Page<DwSource> page,
                                     @Param(Constants.WRAPPER) QueryWrapper<DwSource> q);

    /**
     * 根据指定的字段查询是否重复
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Override
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_source  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

}
