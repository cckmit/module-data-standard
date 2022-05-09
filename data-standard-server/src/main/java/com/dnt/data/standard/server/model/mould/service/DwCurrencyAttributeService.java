package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.service.BaseService;

/**
 * @description: 通用业务属性--服务接口层 <br>
 * @date: 2021/8/18 下午18:04 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwCurrencyAttributeService extends BaseService<DwCurrencyAttribute> {
    /**
     * 获取数通用业务属性分页列表
     * @param request
     * @return
     */
    IPage<DwCurrencyAttribute> selectCurrencyAttributePage(DwCurrencyAttributeRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwCurrencyAttribute detailCurrencyAttribute(Long id);

    /**
     * 添加通用业务属性
     * @param request
     * @param userCode
     * @return
     */
    R saveCurrencyAttribute(DwCurrencyAttributeRequest request, String userCode);

    /**
     * 修改通用业务属性
     * @param request
     * @param userCode
     * @return
     */
    R updateCurrencyAttribute(DwCurrencyAttributeRequest request, String userCode);

    /**
     * 删除通用业务属性
     * @param id
     * @param userCode
     * @return
     */
    int deleteCurrencyAttribute(Long id, String userCode);
}
