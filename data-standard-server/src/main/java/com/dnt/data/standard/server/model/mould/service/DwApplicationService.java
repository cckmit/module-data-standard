package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.request.DwApplicationRequest;
import com.dnt.data.standard.server.model.mould.entity.DwApplication;
import com.dnt.data.standard.server.model.service.BaseService;

/**
 * @description: 所属应用--服务接口层 <br>
 * @date: 2021/7/28 下午1:04 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwApplicationService extends BaseService<DwApplication> {
    /**
     * 获取所属应用分页列表
     * @param request
     * @return
     */
    IPage<DwApplication> selectApplicationPage(DwApplicationRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwApplication detailApplication(Long id);

    /**
     * 添加所属应用
     * @param request
     * @param userCode
     * @return
     */
    R saveApplication(DwApplicationRequest request, String userCode);

    /**
     * 修改所属应用
     * @param request
     * @param userCode
     * @return
     */
    R updateApplication(DwApplicationRequest request, String userCode);

    /**
     * 删除所属应用
     * @param id
     * @param userCode
     * @return
     */
    int deleteApplication(Long id, String userCode);
}
