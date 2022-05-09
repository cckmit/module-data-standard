package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwSource;
import com.dnt.data.standard.server.model.mould.entity.request.DwSourceRequest;
import com.dnt.data.standard.server.model.service.BaseService;

/**
 * @description: 来源系统--服务接口层 <br>
 * @date: 2021/7/28 下午1:04 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwSourceService extends BaseService<DwSource> {
    /**
     * 获取数来源系统分页列表
     *
     * @param request
     * @return
     */
    IPage<DwSource> selectSourcePage(DwSourceRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwSource detailSource(Long id);

    /**
     * 添加来源系统
     *
     * @param request
     * @param userCode
     * @return
     */
    R saveSource(DwSourceRequest request, String userCode);

    /**
     * 修改来源系统
     *
     * @param request
     * @param userCode
     * @return
     */
    R updateSource(DwSourceRequest request, String userCode);

    /**
     * 删除来源系统
     *
     * @param id
     * @param userCode
     * @return
     */
    int deleteSource(Long id, String userCode);
}
