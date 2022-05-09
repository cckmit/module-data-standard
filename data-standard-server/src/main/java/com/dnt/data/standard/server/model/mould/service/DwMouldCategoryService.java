package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldCategoryRequest;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;

/**
 * @description: 模型层级--服务接口层 <br>
 * @date: 2021/8/2 下午6:06 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMouldCategoryService extends BaseService<DwMouldCategory> {
    /**
     * 获模型层级分类树型列表
     * @param projectId
     * @return
     */
    List<DwMouldCategory> selectTreeList(Long projectId);

    /**
     * 分类目录树没有子目录则不展示一级目录
     * @param projectId
     * @return
     */
    List<DwMouldCategory> selectNewTreeList(Long projectId);

    /**
     * 获取模型层级分类分页列表
     * @param request
     * @return
     */
    IPage<DwMouldCategory> selectDwMouldCategoryPage(DwMouldCategoryRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwMouldCategory detailMouldCategory(Long id);

    /**
     * 添加分类
     * @param request
     * @param userCode
     * @return
     */
    R saveMouldCategory(DwMouldCategoryRequest request, String userCode);

    /**
     * 修改分类
     * @param request
     * @param userCode
     * @return
     */
    R updateMouldCategory(DwMouldCategoryRequest request, String userCode);

    /**
     * 删除分类
     * @param id
     * @param userCode
     * @return
     */
    R deleteMouldCategory(Long id, String userCode);


}
