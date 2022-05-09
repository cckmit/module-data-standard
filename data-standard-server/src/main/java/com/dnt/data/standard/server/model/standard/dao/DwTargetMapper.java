package com.dnt.data.standard.server.model.standard.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.standard.entity.DwTarget;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @description: 指标--数据库实现层 <br>
 * @date: 2021/7/19 下午2:56 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwTargetMapper extends BaseDao<DwTarget> {
    /**
     * 查询指标属性信息
     * @param type
     * @param projectId
     * @return
     */
    @Select("<script>" +
            " select id,name " +
            " from dw_target_attribute " +
            " where delete_model=1 and type=#{type}" +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<Map<String, Object>> selectAttributeByType(@Param("type") Integer type,
                                                    @Param("projectId") Long projectId);

    /**
     * 查询质量校验函数下拉列表
     * @param projectId
     * @return
     */
    @Select("<script>" +
            " select id,name " +
            " from dw_function " +
            " where delete_model=1" +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<Map<String, Object>> selectFunctionItem(@Param("projectId") Long projectId);

    /**
     * 分页查询 指标数据信息
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.time_target_attribute_id,a.time_target_attribute_name," +
            "     a.service_target_attribute_id,a.service_target_attribute_name," +
            "     a.atom_target_attribute_id,a.atom_target_attribute_name,a.code,a.name," +
            "     a.alias,a.source,a.type,a.class_name,a.length,a.check_function,a.service_caliber,a.technical_caliber," +
            "     a.release_status,a.create_user,a.create_time " +
            " from dw_target a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwTarget> selectTargetPage(Page<DwTarget> page,
                                     @Param(Constants.WRAPPER) QueryWrapper<DwTarget> q);

    /**
     * 根据指定的字段查询是否重复
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_target  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    @Override
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /**
     * 数据指标列表查询
     * @param page
     * @param wq
     * @return
     */
    @Select("<script>" +
            " select a.id,a.name,a.code,a.alias,a.source,a.release_status as releaseStatus,'target' as dwType " +
            " from dw_target a" +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  "  +
            "</script>")
    IPage<Map<String, Object>> selectCategoryDataPageList(Page<CategoryPageListRequest> page,
                                                          @Param(Constants.WRAPPER) QueryWrapper<CategoryPageListRequest> wq);

    /**
     * 项目下的数据 满足发布数数据返回的字段名
     * @param projectId
     * @return
     */
    @Select("<script>" +
            " select a.*" +
            " from dw_target a  " +
            " where a.delete_model=1 " +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<DwTarget> selectDataByProjectId(@Param("projectId") Long projectId);
}
