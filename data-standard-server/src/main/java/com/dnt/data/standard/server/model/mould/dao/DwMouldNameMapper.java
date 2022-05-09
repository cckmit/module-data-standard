package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.mould.entity.DwMouldName;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @description: 模型命名规则--数据库实现层 <br>
 * @date: 2021/8/4 下午3:33 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Mapper
public interface DwMouldNameMapper extends BaseDao<DwMouldName> {
    /**
     * 模型命名规则的目录下拉列表[一级目录]
     * @return
     */
    @Select("<script>" +
            "  select id,name,is_leaf " +
            "  from dw_mould_category " +
            "  where delete_model=1 and parent_id=0 " +
            "</script>")
    List<Map<String, Object>> selectCatalogueItem();

    /**
     * 模型命名规则的二级目录下拉列表
     * @param oneCatalogueId
     * @return
     */
    @Select("<script>" +
            "  select id,name,is_leaf " +
            "  from dw_mould_category " +
            "  where delete_model=1 and parent_id=#{oneCatalogueId} " +
            "</script>")
    List<Map<String, Object>> selectTwoCatalogueItem(@Param("oneCatalogueId") Long oneCatalogueId);

    /**
     * 获取模型命名规则分页列表
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.name,a.description,a.design_type_id,a.design_type_name,a.catalogue_id,a.catalogue_name,a.release_status,a.create_user,a.create_time " +
            " from dw_mould_name a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwMouldName> selectMouldNamePage(Page<DwMouldName> page,
                                           @Param(Constants.WRAPPER) QueryWrapper<DwMouldName> q);

    /**
     * 根据指定的字段查询是否重复
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_mould_name  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    @Override
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /**
     * 模型名称列表查询
     * @param page
     * @param wq
     * @return
     */
    @Select("<script>" +
            " select a.id,a.name,a.mould_name as mouldName,a.description,a.release_status as releaseStatus,'mould_name' as dwType " +
            " from dw_mould_name a" +
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
            " from dw_mould_name a  " +
            " where a.delete_model=1 " +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<DwMouldName> selectDataByProjectId(@Param("projectId") Long projectId);
}
