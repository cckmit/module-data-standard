package com.dnt.data.standard.server.model.standard.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.standard.entity.DwDataElement;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDataElementExcel;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @description: 数据元--数据库实现 <br>
 * @date: 2021/7/21 下午3:15 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwDataElementMapper extends BaseDao<DwDataElement> {


    /**
     * 查看数据源分类下拉列表
     * @return
     */
    @Select("<script> " +
            " select id,name from dw_data_element_type where delete_model=1 " +
            "</script>")
    List<Map<String, Object>> selectDataElementTypeItem();

    /**
     * 查看数据字典下拉列表
     * @return
     */
    @Select("<script> " +
            " select id,name,alias,category_id as categoryId from dw_dict where delete_model=1 " +
            "</script>")
    List<Map<String, Object>>  selectDictItem();

    /**
     * 分页查询数据元信息
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id, a.category_id,a.code,a.name,a.alias,a.description," +
            "  a.type_id,a.length,a.business_rules,a.dict_id,a.dict_name,a.customer_rules,a.release_status,a.create_user,a.create_time " +
            " from dw_data_element a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwDataElement> selectDataElementPage( IPage<DwDataElement> page,
                                                @Param(Constants.WRAPPER) QueryWrapper<DwDataElement> q);

    /**
     * 根据查询条件过滤 查询数据元信息
     * @param q
     * @return
     */
    @Select("<script>" +
            " select b.name as categoryName,a.code,a.name ,a.alias,a.description," +
            "    a.type_id,a.length,a.business_rules,a.dict_id,a.dict_name,a.customer_rules,a.create_user,a.create_time " +
            " from dw_data_element a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    List<DwDataElementExcel> selectDataElementList(@Param(Constants.WRAPPER) QueryWrapper<DwDataElementExcel> q);

    /**
     * 根据指定的字段查询是否重复
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_data_element  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    @Override
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /**
     * 查询分类
     * @param dwType
     * @return
     */
    @Select("<script>" +
            " select id,name,path from dw_category where delete_model =1 and  dw_type=#{dwType}" +
            "</script>")
    List<DwCategory> selectDictCategoryList(@Param("dwType") String dwType);

    /**
     * 根据数据元的ID查询那些模型关联了数据元
     * @param dataElementId
     * @return
     */
    @Select(" select m.id as mouldId,m.name as mouldName ,a.name as mouldFieldName ,IFNULL(p.`name`,'') as partitionName ,IFNULL(p.description,'') as partitionDescription  \n" +
            " from dw_mould_field a \n" +
            " left join dw_data_element e on a.field_standard = e.id and e.delete_model=1 \n" +
            " left join dw_mould m on a.mould_id=m.id and m.delete_model=1 \n" +
            " left join dw_mould_field_partition p on a.mould_id = p.mould_id and p.delete_model=1 " +
            " where a.delete_model=1 and a.field_standard=#{dataElementId}")
    List<Map<String, Object>> selectMouldById(@Param("dataElementId") Long dataElementId);

    /**
     * 查询选择分类下的数据
     * @param page
     * @param wq
     * @return
     */
    @Select("<script> select a.id,a.code,a.name,a.description,a.create_user as createUser,a.release_status as releaseStatus,'data_element' as dwType " +
            " from dw_data_element a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
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
            " from dw_data_element a  " +
            " where a.delete_model=1 " +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<DwDataElement> selectDataByProjectId(@Param("projectId") Long projectId);
}
