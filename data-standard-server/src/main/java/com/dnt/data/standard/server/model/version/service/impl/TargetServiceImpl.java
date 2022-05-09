package com.dnt.data.standard.server.model.version.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.standard.dao.DwTargetMapper;
import com.dnt.data.standard.server.model.standard.entity.DwTarget;
import com.dnt.data.standard.server.model.version.entity.response.VersionDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description:  数据指标列表查询--服务接口 <br>
 * @date: 2022/4/18 下午3:52 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
@Service("target")
public class TargetServiceImpl implements VersionCategoryDataList {

    @Resource
    private DwTargetMapper dwTargetMapper;
    @Override
    public IPage<Map<String, Object>> selectCategoryDataPageList(Page<CategoryPageListRequest> page, QueryWrapper<CategoryPageListRequest> wq) {
        if(log.isInfoEnabled()) {
            log.info("TargetServiceImpl-->selectCategoryDataPageList");
        }

        //数据指标列表查询
        IPage<Map<String,Object>> pList = this.dwTargetMapper.selectCategoryDataPageList(page,wq);

        return pList;
    }
    /**
     * 项目下的数据 满足发布数数据返回的字段名
     * @param projectId
     * @param selectDataIdList
     * @return
     */
    @Override
    public List<VersionDataResponse> selectDataByProjectId(Long projectId,List<Long> selectDataIdList) {
        if(log.isInfoEnabled()) {
            log.info("TargetServiceImpl-->selectDataByProjectId 项目下的数据 满足发布数数据返回的字段名");
        }
        List<VersionDataResponse> vdrList = new ArrayList<>();
        if(CollectionUtils.isEmpty(selectDataIdList)){
            return vdrList;
        }
        //NO.1 项目下的数据 满足发布数数据返回的字段名
        List<DwTarget> mList = this.dwTargetMapper.selectDataByProjectId(projectId);
        if(CollectionUtils.isEmpty(mList)){
            return vdrList;
        }
        //NO.2 根据选择的数据ID 构建返回的数据对象
        for (Long selectId : selectDataIdList) {
            //NO.3 查询选择数据元关联字段
            for(DwTarget de :mList){
                Long id = de.getId();
                if(selectId.longValue() == id.longValue()){
                    VersionDataResponse vdr = VersionDataResponse.builder()
                            .dataId(id)
                            .dataCategoryId(de.getCategoryId())
                            .dataName(de.getName())
                            .dataCode(de.getCode())
                            .dataAlias(de.getAlias())
                            .dataDescription("")
                            .dataReleaseStatus(de.getReleaseStatus())
                            .dataCreateUser(de.getCreateUser())
                            .dataCreateTime(de.getCreateTime())
                            .dataUpdateUser(de.getUpdateUser())
                            .dataUpdateTime(de.getUpdateTime())
                            .dataJson(JSON.toJSONString(de))
                            .build();
                    vdrList.add(vdr);
                }
            }
        }
        //返回选择数据的信息 向操作日志表中插入数据
        return vdrList;
    }
}
