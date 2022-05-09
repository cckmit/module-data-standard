package com.dnt.data.standard.server.model.standard.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.standard.entity.DwTargetAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description: 指标属性--数据库实现层 <br>
 * @date: 2021/7/15 上午11:28 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwTargetAttributeMapper extends BaseDao<DwTargetAttribute> {
    /**
     * 分页查询指标属性 数据
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.code,a.name,a.description,a.type,a.create_user,a.create_time " +
            " from dw_target_attribute a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwTargetAttribute> selectTargetAttributePage(Page<DwTargetAttribute> page,
                                                       @Param(Constants.WRAPPER) QueryWrapper<DwTargetAttribute> q);

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
            "  dw_target_attribute " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);
}
