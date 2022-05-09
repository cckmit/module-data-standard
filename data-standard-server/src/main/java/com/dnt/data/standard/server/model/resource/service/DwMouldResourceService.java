package com.dnt.data.standard.server.model.resource.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.resource.entity.DwMouldResource;
import com.dnt.data.standard.server.model.resource.entity.request.*;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @description: 模型资源--服务接口层 <br>
 * @date: 2021/10/12 上午9:51 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMouldResourceService extends BaseService<DwMouldResource> {
    /**
     * 获取资源分页列表
     * @param request
     * @return
     */
    IPage<DwMouldResource> selectMouldResourcePage(DwMouldResourceRequest request);

    /**
     * 模型资源的上下线
     * @param request
     * @return
     */
    R updateResourceStatus(DwMouldResourceRequest request);

    /**
     * 删除模型资源
     * @param ids
     * @param userCode
     * @return
     */
    int deleteMouldResource(List<Long> ids, String userCode);

    /**
     * 获取引入数据源类型的下拉列表
     * @return
     */
    JSONArray selectSourceTypeItem();

    /**
     * 根据数据源类型查询数据源下拉列表
     * @param typeId
     * @return
     */
    JSONArray selectSourceItem(Long typeId);

    /**
     * 根据项目信息查询数据库下拉列表
     * @param projectId
     * @return
     */
    List<Map<String,Object>> selectDbItem(Long projectId);

    /**
     * 数据库表下拉列表
     * @param dbId
     * @return
     */
    List<Map<String, Object>> selectTableItem(Long dbId);

    /**
     * 模型资源发布操作
     * @param userCode
     * @param request
     * @return
     */
    R doResourceRelease(String userCode, DwMouldResourceReleaseRequest request);

    /**
     * 模型资源统计汇总信息接口
     * @return
     */
    R getResourceStatistic(Long projectId);

    /**
     * 资产盘点查询已有资源的数据类型
     * @return
     */
    R getHaveDataDataSourceType(Long projectId);

    /**
     * 资产盘点已接入数据源数据预览
     * @return
     */
    R getDataPreview(Long projectId);

    /**
     * 资产盘点数据地图分布
     * @param request
     * @return
     */
    R getDataDistribution(DataDistributionRequest request);

    /**
     * 资产盘点数据源分布
     * @param request
     * @return
     */
    R getCategoryStatisticInfo(DataDistributionRequest request);

    /**
     * 资产盘点数据价值排行
     * @param request
     * @return
     */
    R getDataValueRank(DataValueRankRequest request);

    /**
     * 资产盘点数据库Top10
     * @param request
     * @return
     */
    R getTop10Databases(DataValueRankRequest request);

    /**
     * 资产盘点数据表Top10
     * @param request
     * @return
     */
    R getTop10Tables(DataValueRankRequest request);

    /**
     * 资产盘点元数据变化趋势
     * @param request
     * @return
     */
    R getDataIncrementTrend(DataDistributionRequest request);

    /**
     * 资产盘点资产查询趋势
     * @param request
     * @return
     */
    R getSearchTrend(DataDistributionRequest request);

    /**
     * 联想输入
     * @param projectId
     * @param searchContent
     * @return
     */
    List<Map<String,Object>> inputTips(Long projectId,String searchContent);

    /**
     * 查看搜索操作记录
     * @param userCode
     * @param request
     * @return
     */
    R doOperator(String userCode, OperatorRequest request);

    /**
     * 数据库下的字段下拉列表
     * @param tableId
     * @return
     */
    List<Map<String, Object>> selectTableFieldItem(Long tableId);
}
