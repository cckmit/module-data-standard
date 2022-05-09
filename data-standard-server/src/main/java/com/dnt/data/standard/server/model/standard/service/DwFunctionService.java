package com.dnt.data.standard.server.model.standard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.standard.entity.DwFunction;
import com.dnt.data.standard.server.model.standard.entity.request.DwFunctionRequest;

/**
 * @description: 函数--服务接口层 <br>
 * @date: 2021/7/19 上午11:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

public interface DwFunctionService extends BaseService<DwFunction> {
    /**
     * 获取函数分页数据
     * @param request
     * @return
     */
    IPage<DwFunction> selectFunctionPage(DwFunctionRequest request);

    /**
     * 函数详情
     * @param id
     * @return
     */
    DwFunction detailFunction(Long id);

    /**
     * 保存函数
     * @param request
     * @param userCode
     * @return
     */
    R saveFunction(DwFunctionRequest request, String userCode);

    /**
     * 更新函数信息
     * @param request
     * @param userCode
     * @return
     */
    R updateFunction(DwFunctionRequest request, String userCode);

    /**
     * 删除函数信息
     * @param id
     * @param userCode
     * @return
     */
    int deleteFunction(Long id, String userCode);
}
