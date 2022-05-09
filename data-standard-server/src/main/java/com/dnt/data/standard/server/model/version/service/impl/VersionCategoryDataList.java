package com.dnt.data.standard.server.model.version.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.version.entity.response.VersionDataResponse;

import java.util.List;
import java.util.Map;

/**
 * @description: 选择分类下的业务数据--列表接口 <br>
 * @date: 2022/4/18 下午3:47 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface VersionCategoryDataList {
    /**
     * 分类下的数据信息
     * @param page
     * @param wq
     * @return
     */
    IPage<Map<String,Object>> selectCategoryDataPageList(Page<CategoryPageListRequest> page, QueryWrapper<CategoryPageListRequest> wq);

    /**
     * 项目下的数据
     * @param projectId
     * @param selectDataIdList
     * @return
     */
    List<VersionDataResponse> selectDataByProjectId(Long projectId,
                                                    List<Long> selectDataIdList);
}
