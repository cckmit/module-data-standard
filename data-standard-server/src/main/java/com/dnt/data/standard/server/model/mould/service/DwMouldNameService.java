package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.mould.entity.DwMouldName;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldNameRequest;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @description: 模型命名规则--服务接口层 <br>
 * @date: 2021/8/4 下午3:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMouldNameService extends BaseService<DwMouldName> {
    /**模型命名规则的目录下拉列表
     *
     * @return
     */
    List<Map<String, Object>> selectCatalogueItem();

    /**模型命名规则的二级目录下拉列表
     *
     * @param oneCatalogueId
     * @return
     */
    List<Map<String, Object>> selectTwoCatalogueItem(Long oneCatalogueId);

    /**模型规则页自定义下拉列表
     *
     * @return
     */
    List<DwMouldCategory> selectCustomMouldNameItem();

    /**获取模型命名规则分页列表
     *
     * @param request
     * @return
     */
    IPage<DwMouldName> selectMouldNamePage(DwMouldNameRequest request);

    /**查看详情
     *
     * @param id
     * @return
     */
    DwMouldName detailMouldName(Long id);

    /**添加模型命名规则
     *
     * @param request
     * @param userCode
     * @return
     */
    R saveMouldName(DwMouldNameRequest request, String userCode);

    /**修改模型命名规则
     *
     * @param request
     * @param userCode
     * @return
     */
    R updateMouldName(DwMouldNameRequest request, String userCode);

    /**删除模型命名规则
     *
     * @param id
     * @param userCode
     * @return
     */
    int deleteMouldName(Long id, String userCode);

}
