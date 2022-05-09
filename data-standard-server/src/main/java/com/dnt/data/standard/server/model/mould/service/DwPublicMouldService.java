package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMould;
import com.dnt.data.standard.server.model.mould.entity.request.DwPublicMouldRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwDataElementTreeResponse;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @description: 公共字段模型--服务接口层 <br>
 * @date: 2021/7/29 下午4:27 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwPublicMouldService extends BaseService<DwPublicMould> {
    /**查询公共字段模型下拉列表
     *
     * @param dataElementCategoryId
     * @return
     */
    Map<String, List<Map<String, Object>>> selectPublicMouldItem(Long dataElementCategoryId);

    /**获取公共字段模型分页列表
     *
     * @param request
     * @return
     */
    IPage<DwPublicMould> selectPublicMouldPage(DwPublicMouldRequest request);

    /**查看详情
     *
     * @param id
     * @return
     */
    DwPublicMould detailPubicMould(Long id);

    /**添加公共字段模型
     *
     * @param request
     * @param userCode
     * @return
     */
    R savePublicMould(DwPublicMouldRequest request, String userCode);

    /**修改公共字段模型
     *
     * @param request
     * @param userCode
     * @return
     */
    R updatePublicMould(DwPublicMouldRequest request, String userCode);

    /**删除公共字段模型
     *
     * @param id
     * @param userCode
     * @return
     */
    int deletePublicMould(Long id, String userCode);

    /**查询数据元列表的目录树
     *
     * @return
     */
    List<DwDataElementTreeResponse> selectDataElementCategoryTree();
}
