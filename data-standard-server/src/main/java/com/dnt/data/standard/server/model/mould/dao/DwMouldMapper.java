package com.dnt.data.standard.server.model.mould.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.mould.entity.DwMould;
import com.dnt.data.standard.server.model.mould.entity.DwMouldField;
import com.dnt.data.standard.server.model.mould.entity.DwMouldFieldPartition;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMouldField;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 模型管理--数据库实现层 <br>
 * @date: 2021/8/4 下午4:40 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwMouldMapper extends BaseDao<DwMould> {
    /**
     * 根据选择的公共字段集ID 查询关联的字段
     * @param q
     * @return
     */
    @Select("<script>" +
            "  select id,public_mould_id as publicMouldId,name,field_type as fieldType,description," +
            "    field_standard as fieldStandard ,primary_flag as primaryFlag," +
            "    empty_flag as emptyFlag,length " +
            "  from dw_public_mould_field " +
            "  ${ew.customSqlSegment}  " +
            "</script>")
    List<Map<String, Object>> selectPublicMouldField(@Param(Constants.WRAPPER) QueryWrapper<DwPublicMouldField> q);

    @Select("<script>" +
            "  select id,`name`,`order` " +
            "  from dw_data_source_type " +
            "  order by `order` " +
            "</script>")
    List<Map<String, Object>> selectDDLSourceTypeItem();

    /**
     * 批量增加模型关联字段
     * @param mfList
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_mould_field` " +
            "   (`id`, `key`,`mould_id`, `field_type`, `name`, " +
            "    `description`, `standard_category_id`,`field_standard`,`primary_flag`,`empty_flag`," +
            "   `length`,`create_user`, `create_time`) " +
            "  VALUES " +
            "  <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\"> " +
            "   (#{item.id},#{item.key}, #{item.mouldId}, #{item.fieldType}, #{item.name}, " +
            "    #{item.description},#{item.standardCategoryId},#{item.fieldStandard},#{item.primaryFlag},#{item.emptyFlag}," +
            "    #{item.length}, #{item.createUser}, #{item.createTime})" +
            "  </foreach> " +
            " </script>")
    int insertMouldFieldBatch(@Param("list") List<DwMouldField> mfList);

    /**
     * 批量增加模型关联分区字段
     * @param mfList
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_mould_field_partition` " +
            "   (`id`, `mould_id`, `field_type`, `name`, " +
            "    `description`,`create_user`, `create_time`) " +
            "  VALUES" +
            "  <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\"> " +
            "   (#{item.id}, #{item.mouldId}, #{item.fieldType}, #{item.name}, " +
            "    #{item.description}, #{item.createUser}, #{item.createTime})" +
            "  </foreach> " +
            " </script>")
    int insertMouldFieldPartitionsBatch(@Param("list") List<DwMouldFieldPartition> mfList);

    /**
     * 批量更新模型关联字段
     * @param updateList
     */
    @Update("<script>" +
            " <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\"> " +
            "   update  dw_mould_field SET " +
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
    void updateMouldFieldBatch(@Param("list") List<DwMouldField> updateList);

    /**
     * 批量更新模型关联分区
     * @param updatePList
     */
    @Update("<script>" +
            " <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\"> " +
            "   update  dw_mould_field_partition SET " +
            "    <if test=\"item.name!=null and item.name!=''\">  name = #{item.name}  </if>" +
            "    <if test=\"item.fieldType!=null and item.fieldType!=''\">  ,field_type = #{item.fieldType}  </if>" +
            "    <if test=\"item.description!=null and item.description!=''\">  ,description = #{item.description} </if>" +
            "   where delete_model=1 and id = #{item.id} " +
            " </foreach> " +
            "</script>")
    void updateMouldFieldPartitionsBatch(@Param("list")List<DwMouldFieldPartition> updatePList);

    /**
     * 获取模型分页列表接口
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.type_id,a.type_name,a.name,a.description,a.storage_lifecycle,a.mould_status,a.mould_type,a.physics_status,a.create_user,a.create_time " +
            " from dw_mould a " +
            " left join dw_mould_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwMould> selectDwMouldPage(Page<DwMould> page,
                                     @Param(Constants.WRAPPER) QueryWrapper<DwMould> q);

    /**
     * 根据模型ID 查询 模型关联字段 dw_mould_field
     * @param mouldId
     * @return
     */
    @Select("<script>" +
            "  select a.id,a.key,a.mould_id,a.name,a.field_type,b.code as fieldTypeName ,a.description,a.standard_category_id,a.field_standard,de.name as fieldStandardName ,a.primary_flag,a.empty_flag,a.length,a.create_user,a.create_time" +
            "  from dw_mould_field a " +
            "  left join dw_mould_field_type b on a.field_type = b.id and b.delete_model=1 " +
            "  left join dw_data_element de on a.field_standard = de.id " +
            "  where a.delete_model=1 and a.mould_id=#{mouldId}" +
            "</script>")
    List<DwMouldField> selectMouldFields(@Param("mouldId") Long mouldId);

    /**
     * 根据模型ID 查询模型关联分区 dw_mould_field_partition
     * @param mouldId
     * @return
     */
    @Select("<script>" +
            "  select a.id,a.mould_id,a.name,a.field_type,b.code as fieldTypeName,a.description,a.create_user,a.create_time" +
            "  from dw_mould_field_partition a" +
            "  left join dw_mould_field_type b on a.field_type = b.id and b.delete_model=1 " +
            "  where a.delete_model=1 and a.mould_id=#{mouldId}" +
            "</script>")
    List<DwMouldFieldPartition> selectMouldFieldPartitions(@Param("mouldId") Long mouldId);

    /**
     * 通用业务属性下拉列表
     * @param type
     * @return
     */
    @Select("<script>" +
            "  select id,attribute_type as type,attribute_name as name" +
            "  from dw_currency_attribute " +
            "  where delete_model=1 and attribute_type=#{type}" +
            "</script>")
    List<Map<String, Object>> selectCurrencyAttributeList(@Param("type") int type);

    /**
     * 通用业务属性负责人下拉列表
     * @return
     */
    @Select("<script>" +
            "  select id,user_code,employ_name" +
            "  from dw_user " +
            "  where delete_model=1" +
            "</script>")
    List<Map<String, Object>> selectMouldBossheadItem();


    /**
     * 根据 字典ID 与字段名称  验证数据是否重复
     * @param mId
     * @param dfName
     * @return
     */
    @Select("<script>" +
            " select id,name from dw_mould_field " +
            " where delete_model=1 and mould_id=#{mId} and name=#{dfName}" +
            "</script>")
    List<DwMouldField> selectMouldFieldByIdName(@Param("mId") Long mId,
                                                @Param("dfName")String dfName);

    /**
     * 查询模型字段分类信息
     * @return
     */
    @Select("<script>" +
            "  select id,code,name from dw_mould_field_type where delete_model=1 " +
            "</script>")
    List<Map<String, Object>> selectMouldFieldTypeList();

    /**
     * 批量删除模型下的字段
     * @param mouldId
     * @return
     */
    @Delete("<script> delete from dw_mould_field where delete_model=1 and mould_id = #{mouldId}</script>")
    int deleteMouldField(@Param("mouldId") Long mouldId);

    /**
     * 批量删除模型关联分区
     * @param mouldId
     * @return
     */
    @Delete("<script> delete from dw_mould_field_partition where delete_model=1 and mould_id = #{mouldId} </script>")
    int deleteMouldFieldPartitions(@Param("mouldId") Long mouldId);
}
