package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeValueRequest;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;

/* *
 * @desc    通用业务属性Value值接口层
 * @Return: 
 * @author: ZZP
 * @date:  2022/5/18 15:30
 * @Version V1.1.0
 */
public interface DwCurrencyAttributeValueService extends BaseService<DwCurrencyAttributeValue> {

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwCurrencyAttributeValue detailCurrencyAttributeValue(Long id);

    /**
     * 添加通用业务属性
     * @param request
     * @param userCode
     * @return
     */
    R saveCurrencyAttributeValue(DwCurrencyAttributeValueRequest request, String userCode);

    /**
     * 修改通用业务属性
     * @param request
     * @param userCode
     * @return
     */
    R updateCurrencyAttributeValue(DwCurrencyAttributeValueRequest request, String userCode);

    /**
     * 删除通用业务属性
     * @param id
     * @param userCode
     * @return
     */
    int deleteCurrencyAttributeValue(Long id, String userCode);

    /* *
     * @desc   获取树形属性值
     * @Return: java.util.List
     * @author: ZZP
     * @date:  2022/5/18 16:26
     * @Version V1.1.0
     */
    List selectCurrencyAttributeValueTree(DwCurrencyAttributeValueRequest request);
}
