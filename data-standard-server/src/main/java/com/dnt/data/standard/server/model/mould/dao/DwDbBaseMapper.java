package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.mould.entity.DwDbBase;
import com.dnt.data.standard.server.model.mould.entity.DwDbBaseField;
import com.dnt.data.standard.server.model.mould.entity.excel.DwDbBaseExcel;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 数据基础库--数据库实现层 <br>
 * @date: 2021/7/29 下午1:33 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwDbBaseMapper extends BaseDao<DwDbBase> {
    /**
     * 获取数据基础库分页列表
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.code,a.name,a.description,a.release_status,a.create_user,a.create_time " +
            " from dw_db_base a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwDbBase> selectDbBasePage(Page<DwDbBase> page,
                                     @Param(Constants.WRAPPER) QueryWrapper<DwDbBase> q);
    @Select("<script>" +
            " select b.name as categoryName,a.code,a.name,a.description,a.create_user,a.create_time " +
            " from dw_db_base a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    List<DwDbBaseExcel> selectDbBaseList(@Param(Constants.WRAPPER) QueryWrapper<DwDbBase> q);

    /**
     * 根据指定的字段查询是否重复
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_db_base  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    @Override
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /**
     * 根据 字典ID 与字段名称  验证数据是否重复
     * @param dbBaseId
     * @param tableName
     * @return
     */
    @Select("<script>" +
            " select id,table_name from dw_db_base_field " +
            " where delete_model=1 and db_base_id=#{dbBaseId} and table_name=#{tableName}" +
            "</script>")
    List<DwDbBaseField> selectDbBaseFieldByIdName(@Param("dbBaseId") Long dbBaseId,
                                                  @Param("tableName") String tableName);

    /**
     * 插入数据基础库关联字段
     * @param insertFields
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_db_base_field` " +
            "   (`id`, `db_base_id`, `table_name`, `content_data`, " +
            "    `create_user`, `create_time`) " +
            "  VALUES" +
            "  <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\"> " +
            "   (#{item.id}, #{item.dbBaseId}, #{item.tableName}, #{item.contentData}, " +
            "    #{item.createUser}, #{item.createTime})" +
            "  </foreach> " +
            " </script>")
    int insertDbBaseFieldBatch(@Param("list") List<DwDbBaseField> insertFields);

    /**
     * 查询当前数据字典下  关联的字段集合
     * @param updateFields
     * @return
     */
    @Update("<script>" +
            " <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\"> " +
            "   update  dw_db_base_field SET " +
            "    <if test=\"item.tableName!=null and item.tableName!=''\">  table_name = #{item.tableName}  </if>" +
            "    <if test=\"item.contentData!=null and item.contentData!=''\">  ,content_data = #{item.contentData}  </if>" +
            "   where delete_model=1 and id = #{item.id} " +
            " </foreach> " +
            "</script>")
    int updateDbBaseFieldBatch(@Param("list") List<DwDbBaseField> updateFields);

    /**
     * 数据基础库下的字段信息
     * @param id
     * @return
     */
    @Select("<script>" +
            " select id,db_base_id,table_name,content_data" +
            " from dw_db_base_field " +
            " where  delete_model=1 and db_base_id=#{dbBaseId}" +
            "</script>")
    List<DwDbBaseField> selectDwDbBaseFieldByDbId(@Param("dbBaseId") Long id);

    /**
     * 查询选择分类下的数据
     * @param page
     * @param wq
     * @return
     */
    @Select("<script>" +
            " select a.id,a.name,a.code,a.description,IFNULL(a.release_status,0) as releaseStatus,'db_base' as dwType " +
            " from dw_db_base a" +
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
            " from dw_db_base a  " +
            " where a.delete_model=1 " +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<DwDbBase> selectDataByProjectId(@Param("projectId") Long projectId);

}
