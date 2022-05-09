package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwMouldNameAttribute;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldNameAttributeRequest;
import com.dnt.data.standard.server.model.service.BaseService;

/**
 * @description: 模型命名属性--服务接口层 <br>
 * @date: 2021/7/27 下午12:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMouldNameAttributeService extends BaseService<DwMouldNameAttribute> {
    /**
     * 获数模型命名属性分页列表
     * @param request
     * @return
     */
    IPage<DwMouldNameAttribute> selectMouldNameAttributePage(DwMouldNameAttributeRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwMouldNameAttribute detailMouldNameAttribute(Long id);

    /**
     * 添加模型命名属性
     * @param request
     * @param userCode
     * @return
     */
    R saveMouldNameAttribute(DwMouldNameAttributeRequest request, String userCode);

    /**
     * 修改模型命名属性
     * @param request
     * @param userCode
     * @return
     */
    R updateMouldNameAttribute(DwMouldNameAttributeRequest request, String userCode);

    /**
     * 删除模型命名属性
     * @param id
     * @param userCode
     * @return
     */
    int deleteMouldNameAttribute(Long id, String userCode);
}
