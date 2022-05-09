package com.dnt.data.standard.server.model.standard.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.standard.entity.DwDict;
import com.dnt.data.standard.server.model.standard.entity.DwDictField;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDictExcel;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 数据字典--数据库实现层 <br>
 * @date: 2021/7/12 下午1:32 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwDictMapper extends BaseDao<DwDict> {
    /**
     * 插入数据字典关联字段
     * @param fields
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_dict_field` " +
            "   (`id`, `dict_id`, `key_code`, `key_name`, " +
            "    `description`, `create_user`, `create_time`) " +
            "  VALUES" +
            "  <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\",\"> " +
            "   (#{item.id}, #{item.dictId}, #{item.keyCode}, #{item.keyName}, " +
            "    #{item.description}, #{item.createUser}, #{item.createTime})" +
            "  </foreach> " +
            " </script>")
    int insertDictFieldBatch(@Param("list") List<DwDictField> fields);

    /**
     * 查询当前数据字典下  关联的字段集合
     * @param updateFields
     * @return
     */
    @Update("<script>" +
            " <foreach collection=\"list\" item=\"item\" index=\"index\" open=\"\" close=\"\" separator=\";\"> " +
            "   update  dw_dict_field SET " +
            "    <if test=\"item.keyCode!=null and item.keyCode!=''\">  key_code = #{item.keyCode}  </if>" +
            "    <if test=\"item.keyName!=null and item.keyName!=''\">  ,key_name = #{item.keyName}  </if>" +
            "    <if test=\"item.description!=null and item.description!=''\">  ,description = #{item.description} </if>" +
            "   where delete_model=1 and id = #{item.id} " +
            " </foreach> " +
            "</script>")
    int updateDictFieldBatch(@Param("list") List<DwDictField> updateFields);

    /**
     * 查询数据字典关联字段
     * @param dictId
     * @return
     */
    @Select("<script>" +
            " select id,dict_id,key_code,key_name,description,delete_model" +
            " from dw_dict_field " +
            " where  delete_model=1 and dict_id=#{dictId}" +
            "</script>")
    List<DwDictField> selectDictFieldList(@Param("dictId") Long dictId);

    /**
     * 删除数据字典关联字段
     * @param id
     * @param userCode
     * @return
     */
    @Update("<script>" +
            " update dw_dict_field set delete_model=0,update_user=#{userCode},update_time=now() " +
            " where delete_model=1 and id=#{id} " +
            "</script>")
    int deleteDictField(@Param("id") Long id,
                        @Param("userCode") String userCode);

    /**
     * 分页查询数据字典 数据
     * @param page
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.id,a.category_id,a.code,a.name,a.alias,a.description,a.release_status,a.create_user,a.create_time " +
            " from dw_dict a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    IPage<DwDict> selectDictPage(IPage<DwDict> page,
                                 @Param(Constants.WRAPPER) QueryWrapper<DwDict> q);

    /**
     * 根据指定的条件查询 数据字典信息
     * @param q
     * @return
     */
    @Select("<script>" +
            " select b.name as categoryName,a.code,a.name,a.alias,a.description,a.create_user,a.create_time " +
            " from dw_dict a " +
            " left join dw_category b on a.category_id=b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment}  " +
            "</script>")
    List<DwDictExcel> selectDictList(@Param(Constants.WRAPPER) QueryWrapper<DwDict> q);

    /**
     * 根据指定的字段查询是否重复
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    @Select("<script>" +
            "select count(id) as result from" +
            "  dw_dict  " +
            " where ${property}=#{value} and delete_model=1" +
            "  <if test='categoryId!=null'> and category_id = #{categoryId} </if>" +
            "</script>")
    @Override
    boolean isExist(@Param("property") String property,
                    @Param("value") String value,
                    @Param("categoryId") Long categoryId);


    /**
     * 根据 字典ID 与字段名称  验证数据是否重复
     * @param dictId
     * @param dfName
     * @return
     */
    @Select("<script>" +
            " select id,key_name from dw_dict_field " +
            " where delete_model=1 and dict_id=#{dictId} and key_name=#{dfName}" +
            "</script>")
    List<DwDictField> selectDictFieldByIdName(@Param("dictId") Long dictId,
                                              @Param("dfName") String dfName);

    /**
     * 数据字典列表查询
     * @param page
     * @param wq
     * @return
     */
    @Select("<script>" +
            " select a.id,a.name,a.code,a.alias,a.description,a.release_status as releaseStatus ,'dict' as dwType " +
            " from dw_dict a" +
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
            " from dw_dict a  " +
            " where a.delete_model=1 " +
            "  <if test='projectId!=null'> and project_id = #{projectId} </if>" +
            "</script>")
    List<DwDict> selectDataByProjectId(@Param("projectId") Long projectId);

}
