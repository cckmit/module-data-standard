package com.dnt.data.standard.server.model.standard.dao;

import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description: 业务分类-数据库实现层 <br>
 * @date: 2021/7/8 下午5:39 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwCategoryMapper extends BaseDao<DwCategory> {
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
            "  dw_category  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and parent_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);
}
