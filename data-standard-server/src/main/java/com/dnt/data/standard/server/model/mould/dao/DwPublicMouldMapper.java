package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMould;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMouldField;
import com.dnt.data.standard.server.model.mould.entity.response.DwDataElementTreeResponse;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 公共字段模型--数据库实现层 <br>
 * @date: 2021/7/29 下午3:40 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwPublicMouldMapper extends BaseDao<DwPublicMould> {
    /**
     * 查询数据字段类型
     * @return
     */
    @Select("<script> select id,code,name " +
            " from dw_mould_field_type where delete_model=1 " +
            "</script>")
    List<Map<String, Object>> selectMouldFieldTypes();
    @Select("<script> select a.id,a.code,a.name " +
            " from dw_data_element a " +
            " left join dw_category c on a.category_id = c.id and c.delete_model=1  " +
            " where a.delete_model=1 " +
            "  <if test='dataElementCategoryId!=null'> and c.path like CONCAT( '%',#{dataElementCategoryId},'%')  </if>" +
            "</script>")
    List<Map<String, Object>> selectDataElements(@Param("dataElementCategoryId") Long dataElementCategoryId);

    /**
     * 获取公共字段模型分页列表
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.name,a.description,a.record_count,a.create_user,a.create_time " +
            " from dw_public_mould a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " left join dw_public_mould_field mf on a.id = mf.public_mould_id and mf.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwPublicMould> selectPublicMouldPage(Page<DwPublicMould> page,
                                               @Param(Constants.WRAPPER) QueryWrapper<DwPublicMould> q);

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
            "  dw_public_mould  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);

    /***
     * 批量增加公共字段模型关联字段
     * @param pmfList
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_public_mould_field` " +
            "   (`id`, `public_mould_id`, `field_type`, `name`, " +
            "    `description`, `standard_category_id`,`field_standard`,`primary_flag`,`empty_flag`," +
            "   `length`,`create_user`, `create_time`) " +
            "  VALUES" +
            "  <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\"> " +
            "   (#{item.id}, #{item.publicMouldId}, #{item.fieldType}, #{item.name}, " +
            "    #{item.description},#{item.standardCategoryId},#{item.fieldStandard},#{item.primaryFlag},#{item.emptyFlag}," +
            "    #{item.length}, #{item.createUser}, #{item.createTime})" +
            "  </foreach> " +
            " </script>")
    int insertPublicMouldFieldBatch(List<DwPublicMouldField> pmfList);
    /**
     * 批量更新公共字段模型关联字段
     * @param updateList
     * @return
     */
    @Update("<script>" +
            " <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\"> " +
            "   update  dw_public_mould_field SET " +
            "    <if test=\"item.name!=null and item.name!=''\">  name = #{item.name}  </if>" +
            "    <if test=\"item.fieldType!=null and item.fieldType!=''\">  ,field_type = #{item.fieldType}  </if>" +
            "    <if test=\"item.description!=null and item.description!=''\">  ,description = #{item.description} </if>" +
            "    <if test=\"item.standardCategoryId!=null \">  ,standard_category_id = #{item.standardCategoryId} </if>" +
            "    <if test=\"item.fieldStandard!=null and item.fieldStandard!=''\">  ,field_standard = #{item.fieldStandard} </if>" +
            "    <if test=\"item.primaryFlag!=null\">  ,primary_flag = #{item.primaryFlag} </if>" +
            "    <if test=\"item.emptyFlag!=null \">  ,empty_flag = #{item.emptyFlag} </if>" +
            "    <if test=\"item.length!=null\">  ,length = #{item.length} </if>" +
            "   where delete_model=1 and id = #{item.id} " +
            " </foreach> " +
            "</script>")
    int updatePublicMouldFieldBatch(List<DwPublicMouldField> updateList);

    /**查询公共集下的字段信息**/
    @Select("<script>" +
            " select `id`, `public_mould_id`, `name`, `field_type`, `description`,`standard_category_id`, `field_standard`, `primary_flag`, `empty_flag`, `length`, `create_user`, `create_time` \n" +
            " from dw_public_mould_field" +
            " ${ew.customSqlSegment}  " +
            "</script>")
    List<DwPublicMouldField> selectMouldFields(@Param(Constants.WRAPPER) QueryWrapper<DwPublicMouldField> q);

    /**
     * 查询数据元的分类目录
     * @param cq
     * @return
     */
    @Select("<script>" +
            " select id,parent_id,name,path,is_leaf as isLeaf,level " +
            " from dw_category a " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    List<DwDataElementTreeResponse> selectDataElementCategories(@Param(Constants.WRAPPER) QueryWrapper<DwCategory> cq);
}
