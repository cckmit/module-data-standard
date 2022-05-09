package com.dnt.data.standard.server.model.standard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.standard.entity.request.DwTargetAttributeRequest;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.standard.entity.DwTargetAttribute;

/**
 * @description: 指标属性--服务接口层 <br>
 * @date: 2021/7/15 下午3:47 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwTargetAttributeService extends BaseService<DwTargetAttribute> {
    /**
     * 查看分页
     * @param request
     * @return
     */
    IPage<DwTargetAttribute> selectTargetAttributePage(DwTargetAttributeRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwTargetAttribute detailTargetAttribute(Long id);

    /**
     * 添加指标属性
     * @param request
     * @param userCode
     * @return
     */
    R saveTargetAttribute(DwTargetAttributeRequest request, String userCode);

    /**
     * 修改指标属性
     * @param request
     * @param userCode
     * @return
     */
    R updateTargetAttribute(DwTargetAttributeRequest request, String userCode);

    /**
     * 删除指标属性
     * @param id
     * @param userCode
     * @return
     */
    int deleteTargetAttribute(Long id, String userCode);
}
