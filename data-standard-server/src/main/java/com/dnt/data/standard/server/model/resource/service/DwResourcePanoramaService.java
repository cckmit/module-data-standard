package com.dnt.data.standard.server.model.resource.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.resource.entity.request.CategoryLinkRequest;
import com.dnt.data.standard.server.model.resource.entity.request.DwResourcePanoramaRequest;
import com.dnt.data.standard.server.model.resource.entity.request.ResourcePanoramaRequest;
import com.dnt.data.standard.server.model.resource.entity.response.CategoryLinkResponse;
import com.dnt.data.standard.server.model.resource.entity.DwResourcePanorama;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @description: 资产全景--服务接口层 <br>
 * @date: 2021/11/8 下午1:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwResourcePanoramaService extends BaseService<DwResourcePanorama> {
    /**
     * 资产全景列表
     * @param request
     * @return
     */
    List<DwResourcePanorama> selectPanoramaList(DwResourcePanoramaRequest request);

    /**
     * 更新资产全景状态
     * @param userCode
     * @param request
     * @return
     */
    R updatePanoramaStatus(String userCode, DwResourcePanoramaRequest request);

    /**
     * 获取资产全景下拉列表
     * @return
     */
    List<Map<String,Object>> getResourcePanoramaItem();

    /**
     * 获取模型链路配置列表
     * @return
     */
    List<Map<String,Object>> selectMouldCategory();

    /**
     * 选择模型链路中展示模型层级
     * @param request
     * @return
     */
    List<CategoryLinkResponse> selectCategoryLink(CategoryLinkRequest request);

    /**
     * 查看资产全景详情
     * @param id
     * @return
     */
    ResourcePanoramaRequest detailResourcePanorama(Long id);

    /**
     * 添加资产全景
     * @param request
     * @param userCode
     * @return
     */
    R saveResourcePanorama(ResourcePanoramaRequest request, String userCode);

    /**
     * 修改资产全景
     * @param request
     * @param userCode
     * @return
     */
    R updateResourcePanorama(ResourcePanoramaRequest request, String userCode);

    /**
     * 渲染资产全景的层级关系
     * @param userCode
     * @param request
     * @return
     */
    R doRenderResourcePanorama(String userCode, DwResourcePanoramaRequest request);

    /**
     * 下一级资产全景的数据与层级关系
     * @param userCode
     * @param request
     * @return
     */
    R doRenderChildPanorama(String userCode, DwResourcePanoramaRequest request);
}
