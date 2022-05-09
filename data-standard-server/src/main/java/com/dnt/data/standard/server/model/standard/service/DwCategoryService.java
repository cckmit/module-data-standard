package com.dnt.data.standard.server.model.standard.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.standard.entity.request.DwCategoryRequest;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;

import java.util.List;

/**
 * @description: 业务分类-服务接口层 <br>
 * @date: 2021/7/8 下午5:41 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwCategoryService extends BaseService<DwCategory> {

    /**
     * 获取分类树型列表
     * @param projectId
     * @param dwType
     * @return
     */
    List<DwCategory> selectTreeList(Long projectId,String dwType);

    /**
     * 查询单个分类详情
     * @param id
     * @return
     */
    DwCategory detailCategory(Long id);

    /**
     * 删除
     * @param id
     * @param userCode
     * @return
     */
    R deleteCategory(Long id,String userCode);

    /**
     * 编辑
     * @param request
     * @param userCode
     * @return
     */
    R updateCategory(DwCategoryRequest request, String userCode);

    /**
     * 添加
     * @param request
     * @param userCode
     * @return
     */
    R saveCategory(DwCategoryRequest request, String userCode);

}