package com.dnt.data.standard.server.model.resource.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.resource.entity.DwResourcePanorama;
import com.dnt.data.standard.server.model.resource.entity.response.CategoryLinkResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @description: 资产全景--数据库实现层 <br>
 * @date: 2021/11/8 下午2:02 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwResourcePanoramaMapper extends BaseDao<DwResourcePanorama> {
    /**
     * 查询已上线的 资产全景信息
     * @return
     */
    @Select("<script> select id as id,name as name " +
            " from dw_resource_panorama  " +
            " where delete_model=1 and status= 1 " +
            " order by id desc " +
            "</script>")
    List<Map<String, Object>> getResourcePanoramaItem();

    /**
     * 获取模型链路配置列表
     * @return
     */
    @Select("<script> select id as id,code as code ,name as name " +
            " from dw_mould_category  " +
            " where delete_model=1 and parent_id=0 " +
            " order by id desc " +
            "</script>")
    List<Map<String, Object>> selectMouldCategory();

    @Select("<script>" +
            " select id,name,parent_id as parentId,is_leaf as isLeaf,path,level " +
            " from dw_mould_category " +
            " ${ew.customSqlSegment}" +
            "</script>")
    List<CategoryLinkResponse> selectCategoryLink(@Param(Constants.WRAPPER) QueryWrapper<CategoryLinkResponse> q);
}
