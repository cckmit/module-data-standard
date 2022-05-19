package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description: 通用业务属性--数据库实现层 <br>
 * @date: 2021/8/18 下午18:01 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwCurrencyAttributeMapper extends BaseDao<DwCurrencyAttribute> {
    /**获取数来源系统分页列表**/
    @Select("<script>" +
            " SELECT " +
            "   da.*, " +
            "   db.attribute_value as attributeValue " +
            "FROM " +
            "   dw_currency_attribute da " +
            "   LEFT JOIN ( SELECT GROUP_CONCAT( attribute_value ) AS attribute_value, attribute_id FROM dw_currency_attribute_value dv WHERE dv.attribute_type = 1 AND delete_model = 1 GROUP BY dv.attribute_id ) db  " +
            "   ON da.id = db.attribute_id" +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwCurrencyAttribute> selectCurrencyAttributePage(Page<DwCurrencyAttribute> page,
                                     @Param(Constants.WRAPPER) QueryWrapper<DwCurrencyAttribute> q);

    /**根据指定的字段查询是否重复**/
    @Override
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_currency_attribute  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /**根据指定的字段查询是否重复**/
    @Override
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_currency_attribute  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    boolean isExistInProject(@Param("property") String property,
                             @Param("value") String value,
                             @Param("projectId") Long projectId);

}
