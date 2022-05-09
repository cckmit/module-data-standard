package com.dnt.data.standard.server.model.version.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.version.entity.DwVersion;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.version.entity.request.DwVersionRequest;
import com.dnt.data.standard.server.model.version.entity.request.VersionReleaseRequest;
import com.dnt.data.standard.server.model.version.entity.response.DwVersionResponse;

import java.util.List;
import java.util.Map;

/**
 * @description: 管理管理-服务接口层 <br>
 * @date: 2022/4/14 上午9:52 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwVersionService extends BaseService<DwVersion> {
    /**
     * 获取版本管理分页列表
     * @param request
     * @return
     */
    IPage<DwVersion> selectVersionPage(DwVersionRequest request);

    /**
     * 查看版本管理详情
     * @param id
     * @return
     */
    DwVersionResponse detailVersion(Long id);

    /**
     * 删除版本管理
     * @param id
     * @return
     */
    R deleteVersion(Long id);

    /**
     * 根据项目ID 获取发布数据的目录树
     * @param projectId
     * @return
     */
    List<DwCategory> selectReleaseTree(Long projectId);

    /**
     * 查询不同数据类型下的分页列表
     * @param request
     * @return
     */
    IPage<Map<String,Object>> selectCategoryPageList(CategoryPageListRequest request);

    /**
     * 查询不同类型下分页列表的表头
     * @return
     */
    Map<Integer,List<Map<Object,Object>>> selectCategoryPageHeader();

    /**
     * 版本的发布功能
     * @param request
     * @return
     */
    R doVersionRelease(VersionReleaseRequest request);
}
