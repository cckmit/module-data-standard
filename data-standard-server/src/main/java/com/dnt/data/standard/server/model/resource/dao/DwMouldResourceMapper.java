package com.dnt.data.standard.server.model.resource.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.resource.entity.DwMouldResourceOperatorLog;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.resource.entity.DwMouldResource;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @description: 模型资源--数据库实现层 <br>
 * @date: 2021/10/11 下午5:46 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Mapper
public interface DwMouldResourceMapper extends BaseDao<DwMouldResource> {
    /**
     * 获取资源分页列表
     * @param page
     * @param q
     * @return
     */
    @Select(" select a.id,a.category_id,a.name,IFNULL(a.db_name,'') as dbName ,a.category_path,a.category_path_name,a.owner_name,a.status,a.type,a.create_time,a.create_user" +
            " from dw_mould_resource a " +
            " left join dw_category c on a.category_id = c.id and c.delete_model=1 " +
            " ${ew.customSqlSegment} ")
    IPage<DwMouldResource> selectMouldResourcePage(Page<DwMouldResource> page,
                                                   @Param(Constants.WRAPPER) QueryWrapper<DwMouldResource> q);

    /**
     * 批量删除 模型资源
     * @param rq
     * @param userCode
     * @return
     */
    @Update("update dw_mould_resource set delete_model=0 ,update_user=#{userCode},update_time=now() " +
            " ${ew.customSqlSegment} ")
    int deleteMouldResourceBatch(@Param(Constants.WRAPPER) QueryWrapper<DwMouldResource> rq,
                                 @Param("userCode") String userCode);

    /**
     * 根据数据源类型查询数据源信息
     * @param typeId
     * @return
     */
    @Select("<script> select project_id as projectId,project_name as projectName " +
            " from dw_mould_physics  " +
            " where delete_model=1 " +
            " <if test='typeId gt 0 '>  and data_source_id = #{typeId} </if> " +
            " group by project_id " +
            "</script>")
    @Deprecated
    List<Map<String, Object>> selectSourceItem(@Param("typeId") Long typeId);

    /**
     * 根据项目信息查询数据库下拉列表
     * @param projectId
     * @return
     */
    @Select("<script> select project_id as dbId,db_name as dbName " +
            " from dw_mould_physics  " +
            " where delete_model=1 " +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " group by project_id " +
            "</script>")
    @Deprecated
    List<Map<String, Object>> selectDbItem(@Param("projectId") Long projectId);

    /**
     * 数据库表下拉列表
     * @param dbId
     * @return
     */
    @Select("<script> select mould_id as tableId,mould_name as tableName " +
            " from dw_mould_physics  " +
            " where delete_model=1 and project_id = #{dbId} " +
            "</script>")
    @Deprecated
    List<Map<String, Object>> selectTableItem(@Param("dbId") Long dbId);

    /**
     * 数据库下的字段下拉列表
     * @param tableId
     * @return
     */
    @Select("<script> select id as fieldId,name as fieldName " +
            " from dw_mould_field  " +
            " where delete_model=1 and mould_id = #{tableId} " +
            "</script>")
    List<Map<String, Object>> selectTableFieldItem(@Param("tableId") Long tableId);

    /**
     * 发布资源汇总信息
     * @param mrq
     * @return
     */
    @Select("<script> " +
            "  select count(a.id) as `ALL`, \n" +
            "    SUM(case a.type when 1 then 1 else 0 end ) as `tableCount`,\n" +
            "    SUM(case a.type when 2 then 1 else 0 end ) as `quotaCount` \n" +
            "  from dw_mould_resource a" +
            " ${ew.customSqlSegment} " +
            " </script>")
    Map<String,Object> selectMetaStatisticData(@Param(Constants.WRAPPER) QueryWrapper<DwMouldResource> mrq);

    /**
     * 资源分布（根据目录汇总）
     * @param mrq
     * @return
     */
    @Select("<script> " +
            "  select a.id as `id` , count(a.id) as `value`, b.name as `name` \n" +
            "  from dw_mould_resource a " +
            "  left join dw_category b on a.category_id = b.id and b.delete_model=1 " +
            " ${ew.customSqlSegment} " +
            " </script>")
    List<Map<String, Object>> selectCategoryStatisticData(@Param(Constants.WRAPPER) QueryWrapper<DwMouldResource> mrq);

    /**
     * 资产盘点查询已有资源的数据类型
     * @param q
     * @return
     */
    @Select("<script>" +
            " select a.data_source_type_id as dataSourceTypeId,a.data_source_type_name as dataSourceTypeName " +
            "  from dw_mould_resource a" +
            " ${ew.customSqlSegment} " +
            "</script>")
    List<Map<String,Object>> selectHaveDataDataSourceType(@Param(Constants.WRAPPER) QueryWrapper<DwMouldResource> q);

    /**
     * 昨日发布的资源信息
     * @param projectId
     * @return
     */
    @Select("<script>" +
            " select data_source_type_id as dataSourceTypeId,count(id) as yesterdayCount  " +
            " from dw_mould_resource \n" +
            " where project_id = #{projectId} and DATE(create_time)= DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            " group by data_source_type_id  " +
            "</script>")
    List<Map<String, Long>> selectYesterdayRelease(@Param("projectId") Long projectId);

    /**
     * 查询出所有已发布的资源信息
     * @param mr
     * @return
     */
    @Select("<script>" +
            " select data_source_type_id as dataSourceTypeId,data_source_id as dataSourceId,db_id as dbId,`name`,IFNULL(owner_name,'') as ownerName,category_path as categoryPath"  +
            "  from dw_mould_resource" +
            "  ${ew.customSqlSegment} " +
            "</script>")
    List<Map<String, Object>> selectResourceReleases(@Param(Constants.WRAPPER) QueryWrapper<DwMouldResource> mr);

    /**
     * 按天获取数据
     * @param projectId
     * @param dataSourceType
     * @return
     */
    @Select("<script>" +
            " select DATE_FORMAT(create_time,'%Y-%m-%d') as dayStr , count(id) as insertCount \n" +
            " from dw_mould_resource \n" +
            " where `status`=1 and delete_model=1 \n" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " GROUP BY 1 " +
            "</script>")
    List<Map<String, Object>> selectIncrementTrendDayInsertList(@Param("projectId")Long projectId, Long dataSourceType);

    /**
     * 按天获取数据
     * @param projectId
     * @param dataSourceType
     * @return
     */
    @Select("<script>" +
            " select DATE_FORMAT(update_time,'%Y-%m-%d') as dayStr , count(id) as updateCount \n" +
            " from dw_mould_resource \n" +
            " where `status`=1 and delete_model=1 \n" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " GROUP BY 1 " +
            "</script>")
    List<Map<String, Object>> selectIncrementTrendDayUpdateList(@Param("projectId") Long projectId,Long dataSourceType);


    /**
     * 按月获取数据
     * @param projectId
     * @param dataSourceType
     * @return
     */
    @Select("<script>" +
            " select DATE_FORMAT(create_time,'%Y-%m') as dayStr , count(id) as insertCount \n" +
            " from dw_mould_resource \n" +
            " where `status`=1 and delete_model=1 \n" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " GROUP BY 1 " +
            "</script>")
    List<Map<String, Object>> selectIncrementTrendMonthInsertList(@Param("projectId")Long projectId, Long dataSourceType);

    /**
     * 按月获取数据
     * @param projectId
     * @param dataSourceType
     * @return
     */
    @Select("<script>" +
            " select DATE_FORMAT(update_time,'%Y-%m') as dayStr , count(id) as updateCount \n" +
            " from dw_mould_resource \n" +
            " where `status`=1 and delete_model=1 \n" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " GROUP BY 1 " +
            "</script>")
    List<Map<String, Object>> selectIncrementTrendMonthUpdateList(@Param("projectId") Long projectId,Long dataSourceType);

    /**
     * 资产盘点数据价值排行
     * @param projectId
     * @param dataSourceType
     * @return
     */
    @Select("<script>" +
            " select id,name,db_name as dbName,0 as showCount,0 as useCount,0 as importCount from dw_mould_resource a " +
            " where a.`status`=1 and a.delete_model=1 and a.data_source_type_id=#{dataSourceTypeId}" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            "</script>")
    List<Map<String, Object>> selectDataValueRank(@Param("projectId") Long projectId,
                                                  @Param("dataSourceTypeId") Long dataSourceType);

    /**
     * 资产盘点数据表Top10
     * @param projectId
     * @param dataSourceType
     * @return
     */
    @Select("<script>" +
            "   select id,name,db_name as dbName, IFNULL(disk_space_size,0) as showCount " +
            "   from dw_mould_resource a  " +
            "   where a.`status`=1 and a.delete_model=1 and a.data_source_type_id=#{dataSourceTypeId}" +
            "   <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            "   order by disk_space_size desc \n" +
            "   limit 10 " +
            "</script>")
    List<Map<String, Object>> selectTop10Tables(@Param("projectId")Long projectId,
                                                @Param("dataSourceTypeId") Long dataSourceType);

    /**
     * 查看/搜索操作记录
     * @param oLog
     * @return
     */
    @Insert("<script>" +
            " INSERT INTO " +
            "   `dw_mould_resource_operator_log` " +
            "   (`id`,`resource_id`, `project_id`,`db_id`,`db_name`,`table_id`,`table_name`,`operator_user`,`operator_flag`,`search_content`, `create_user`) " +
            "  VALUES" +
            "   (#{id},#{resourceId},#{projectId}, #{dbId},#{dbName},#{tableId},#{tableName},#{operatorUser},#{operatorFlag},#{searchContent},#{createUser})" +
            " </script>")
    int insertResourceOperatorLog(DwMouldResourceOperatorLog oLog);

    /**
     * 联想输入
     * @param projectId
     * @param searchContent
     * @return
     */
    @Select("<script>" +
            " select id as id ,name as name " +
            " from dw_mould_resource " +
            " where name like CONCAT( '%',#{content},'%') " +
            "</script>")
    List<Map<String, Object>> selectResourceTips(@Param("projectId")Long projectId,
                                                 @Param("content") String searchContent);

    /**
     * 按天获取数据
     * @param projectId
     * @param searchType
     * @return
     */
    @Select("<script>" +
            " select DATE_FORMAT(create_time,'%Y-%m-%d') as dayStr , count(id) as showCount \n" +
            " from dw_mould_resource_operator_log \n" +
            " where `operator_flag`='show' and delete_model=1 \n" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " GROUP BY 1 " +
            "</script>")
    List<Map<String, Object>> selectDaySearchTrends(@Param("projectId")Long projectId,
                                                    Integer searchType);

    /**
     * 按月获取数据
     * @param projectId
     * @param searchType
     * @return
     */
    @Select("<script>" +
            " select DATE_FORMAT(create_time,'%Y-%m') as dayStr , count(id) as showCount \n" +
            " from dw_mould_resource_operator_log \n" +
            " where `operator_flag`='show' and delete_model=1 \n" +
            " <if test='projectId gt 0 '> and project_id = #{projectId} </if>" +
            " GROUP BY 1 " +
            "</script>")
    List<Map<String, Object>> selectMonthSearchTrends(@Param("projectId")Long projectId,
                                                      Integer searchType);
}
