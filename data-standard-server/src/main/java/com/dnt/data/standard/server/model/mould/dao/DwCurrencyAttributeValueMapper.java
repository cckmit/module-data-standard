package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/* *
 * @desc    通用业务属性Value值数据库实现层
 * @Return:
 * @author: ZZP
 * @date:  2022/5/18 15:40
 * @Version V1.1.0
 */
@Mapper
public interface DwCurrencyAttributeValueMapper extends BaseDao<DwCurrencyAttributeValue> {

    /**根据指定的字段查询是否重复**/
    @Override
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_currency_attribute_value  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    boolean isExistInProject(@Param("property") String property,
                             @Param("value") String value,
                             @Param("projectId") Long projectId);

}
